/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.constraints.TransferConstraint;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.constraints.UnionConstraint;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;

public class OperatorProvider implements IOperatorsProvider
{
    private final ISymbolFactory symbolFactory;
    private Map<Integer, IOverloadSymbol> builtInOperators;
    private final ITypeSymbol falseTypeSymbol;
    private final ITypeSymbol trueTypeSymbol;
    private final ITypeSymbol boolTypeSymbol;
    private final ITypeSymbol intTypeSymbol;
    private final ITypeSymbol floatTypeSymbol;
    private final ITypeSymbol numTypeSymbol;
    private final ITypeSymbol stringTypeSymbol;
    private final ITypeSymbol arrayTypeSymbol;
    private final TypeConstraint falseTypeConstraint;
    private final TypeConstraint trueTypeConstraint;
    private final TypeConstraint boolTypeConstraint;
    private final TypeConstraint intTypeConstraint;
    private final TypeConstraint floatTypeConstraint;
    private final TypeConstraint numTypeConstraint;
    private final TypeConstraint stringTypeConstraint;
    private final TypeConstraint arrayTypeConstraint;
    private final TypeConstraint mixedTypeConstraint;
    private List<String> parameterIds;
    private List<String> unaryParameterId;
    private final IUnionTypeSymbol numOrFalse;
    private final IUnionTypeSymbol floatOrFalse;
    private final IUnionTypeSymbol intOrFalse;

    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {

        symbolFactory = theSymbolFactory;

        falseTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.FALSE);
        trueTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.TRUE);
        boolTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.BOOL);
        intTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.INT);
        floatTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.FLOAT);
        numTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.NUM);
        stringTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.STRING);
        arrayTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.ARRAY);
        falseTypeConstraint = new TypeConstraint(falseTypeSymbol);
        trueTypeConstraint = new TypeConstraint(trueTypeSymbol);
        boolTypeConstraint = new TypeConstraint(boolTypeSymbol);
        intTypeConstraint = new TypeConstraint(intTypeSymbol);
        floatTypeConstraint = new TypeConstraint(floatTypeSymbol);
        numTypeConstraint = new TypeConstraint(numTypeSymbol);
        stringTypeConstraint = new TypeConstraint(stringTypeSymbol);
        arrayTypeConstraint = new TypeConstraint(arrayTypeSymbol);
        mixedTypeConstraint = new TypeConstraint(thePrimitiveType.get(PrimitiveTypeNames.MIXED));

        numOrFalse = symbolFactory.createUnionTypeSymbol();
        numOrFalse.addTypeSymbol(numTypeSymbol);
        numOrFalse.addTypeSymbol(falseTypeSymbol);
        numOrFalse.seal();

        floatOrFalse = symbolFactory.createUnionTypeSymbol();
        floatOrFalse.addTypeSymbol(floatTypeSymbol);
        floatOrFalse.addTypeSymbol(falseTypeSymbol);
        floatOrFalse.seal();

        intOrFalse = symbolFactory.createUnionTypeSymbol();
        intOrFalse.addTypeSymbol(intTypeSymbol);
        intOrFalse.addTypeSymbol(falseTypeSymbol);
        intOrFalse.seal();

        parameterIds = Arrays.asList("$lhs", "$rhs");
        unaryParameterId = Arrays.asList("$expr");
    }

    @Override
    public Map<Integer, IOverloadSymbol> getOperators() {
        if (builtInOperators == null) {
            createOperators();
        }
        return builtInOperators;
    }

    private void createOperators() {
        builtInOperators = new HashMap<>();
        addOperatorLists();
        defineLogicOperators();
        defineAssignmentOperators();
        defineBitLevelOperators();
        defineComparisonOperators();
        defineTernaryOperator();
        defineArithmeticOperators();
        defineDotOperator();
        defineInstanceOfOperator();
        defineAtAndCastOperator();
        defineCloneAndNewOperator();
    }

    private void addOperatorLists() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operatorTypes = new Pair[]{
                //binary operators
                pair("or", TokenTypes.LogicOrWeak), pair("xor", TokenTypes.LogicXorWeak),
                pair("and", TokenTypes.LogicAndWeak),
                pair("=", TokenTypes.Assign), pair("+=", TokenTypes.PlusAssign), pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign), pair("/=", TokenTypes.DivideAssign),
                pair("&=", TokenTypes.BitwiseAndAssign), pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("%=", TokenTypes.ModuloAssign), pair(".=", TokenTypes.DotAssign),
                pair("<<=", TokenTypes.ShiftLeftAssign), pair(">>=", TokenTypes.ShiftRightAssign),
                pair("||", TokenTypes.LogicOr), pair("&&", TokenTypes.LogicAnd),
                pair("|", TokenTypes.BitwiseOr), pair("&", TokenTypes.BitwiseAnd), pair("^", TokenTypes.BitwiseXor),
                pair("==", TokenTypes.Equal), pair("===", TokenTypes.Identical),
                pair("!=", TokenTypes.NotEqual), pair("!==", TokenTypes.NotIdentical),
                pair("<", TokenTypes.LessThan), pair("<=", TokenTypes.LessEqualThan),
                pair(">", TokenTypes.GreaterThan), pair(">=", TokenTypes.GreaterEqualThan),
                pair("<<", TokenTypes.ShiftLeft), pair(">>", TokenTypes.ShiftRight),
                pair("+", TokenTypes.Plus), pair("-", TokenTypes.Minus), pair("*", TokenTypes.Multiply),
                pair("/", TokenTypes.Divide), pair("%", TokenTypes.Modulo), pair(".", TokenTypes.Dot),
                pair("instanceof", TokenTypes.Instanceof),
                //unary operators
                pair("cast", TokenTypes.CAST),
                pair("++", TokenTypes.PRE_INCREMENT), pair("--", TokenTypes.PRE_DECREMENT),
                pair("@", TokenTypes.At), pair("~", TokenTypes.BitwiseNot), pair("!", TokenTypes.LogicNot),
                pair("-", TokenTypes.UNARY_MINUS), pair("+", TokenTypes.UNARY_PLUS),
                pair("++", TokenTypes.POST_INCREMENT), pair("--", TokenTypes.POST_DECREMENT),
                pair("clone", TokenTypes.Clone), pair("new", TokenTypes.New),
                //ternary operators
                pair("?", TokenTypes.QuestionMark),
        };

        for (Pair<String, Integer> operatorType : operatorTypes) {
            builtInOperators.put(operatorType.second, symbolFactory.createOverloadSymbol(operatorType.first));
        }
    }

    private void defineLogicOperators() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] orOperators = new Pair[]{
                pair("or", TokenTypes.LogicOrWeak),
                pair("||", TokenTypes.LogicOr)
        };
        for (Pair<String, Integer> operator : orOperators) {
            addToBinaryOperators(operator, falseTypeConstraint, falseTypeConstraint, falseTypeSymbol);
            addToBinaryOperators(operator, trueTypeConstraint, boolTypeConstraint, trueTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //true x ~{as bool} -> true
            addToBinaryOperators(operator, boolTypeConstraint, trueTypeConstraint, trueTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x true -> true
            addToBinaryOperators(operator, boolTypeConstraint, boolTypeConstraint, boolTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool
        }

        Pair<String, Integer> xorWeak = pair("xor", TokenTypes.LogicXorWeak);
        addToBinaryOperators(xorWeak, falseTypeConstraint, trueTypeConstraint, trueTypeSymbol);
        addToBinaryOperators(xorWeak, trueTypeConstraint, falseTypeConstraint, trueTypeSymbol);
        addToBinaryOperators(xorWeak, falseTypeConstraint, falseTypeConstraint, falseTypeSymbol);
        addToBinaryOperators(xorWeak, trueTypeConstraint, trueTypeConstraint, falseTypeSymbol);
        addToBinaryOperators(xorWeak, boolTypeConstraint, boolTypeConstraint, boolTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x ~{as bool} -> bool

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] andOperators = new Pair[]{
                pair("and", TokenTypes.LogicAndWeak),
                pair("&&", TokenTypes.LogicAnd)
        };
        for (Pair<String, Integer> operator : andOperators) {
            //TODO rstoll TINS-347 create overloads for conversion constraints
            addToBinaryOperators(operator, falseTypeConstraint, boolTypeConstraint, falseTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //false x ~{as bool} -> false
            addToBinaryOperators(operator, boolTypeConstraint, falseTypeConstraint, falseTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{bool} x false -> false
            addToBinaryOperators(operator, trueTypeConstraint, trueTypeConstraint, trueTypeSymbol);
            addToBinaryOperators(operator, boolTypeConstraint, boolTypeConstraint, boolTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool
        }

        Pair<String, Integer> logicNot = new Pair<>("!", TokenTypes.LogicNot);
        addToUnaryOperators(logicNot, falseTypeConstraint, trueTypeSymbol);
        addToUnaryOperators(logicNot, trueTypeConstraint, falseTypeSymbol);
        addToUnaryOperators(logicNot, boolTypeConstraint, boolTypeSymbol);
    }

    private void defineAssignmentOperators() {
        //Tvar x Tval -> Tvar / Tvar > Tval

        //can be simplified, for test purposes it is quasi
        // function assign(&$lhs, $rhs){
        //   $lhs = $rhs;
        //   return $lhs;
        // }
        IFunctionTypeSymbol function;
        ITypeVariableSymbolWithRef lhs = createByRefTypeVariableWithRef("$lhs");
        ITypeVariableSymbolWithRef rhs = createTypeVariableWithRef("$rhs");
        ITypeVariableSymbolWithRef rtn = createTypeVariableWithRef("return");

        ITypeVariableSymbol rhs1 = createTypeVariable("$rhs1");
        rhs.addRefVariable(rhs1);
        rhs1.setConstraint(rhs);
        ITypeVariableSymbol lhs1 = createTypeVariable("$lhs1");
        lhs.addRefVariable(lhs1);
        lhs1.setConstraint(rhs1);
        ITypeVariableSymbol rtn1 = createTypeVariable("$rtn1");
        rtn.addRefVariable(rtn1);
        rtn1.setConstraint(new TransferConstraint(lhs1));
        Deque<ITypeVariableSymbol> typeVariables = new ArrayDeque<>();
        typeVariables.add(rhs1);
        typeVariables.add(lhs1);
        typeVariables.add(rtn1);
        List<ITypeVariableSymbolWithRef> parameters = Arrays.asList(lhs, rhs);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("=", parameters, rtn, typeVariables);
        function.addInputConstraint("$lhs", mixedTypeConstraint);
        function.addInputConstraint("$rhs", mixedTypeConstraint);
        addToOperators(TokenTypes.Assign, function);

        //Other assignment operators can be found in the corresponding sections.
        //For instance, += is in createAssignmentOperators
    }

    private ITypeVariableSymbolWithRef createByRefTypeVariableWithRef(String name) {
        ITypeVariableSymbolWithRef typeVariableWithRef = createTypeVariableWithRef(name);
        typeVariableWithRef.setIsByRef();
        return typeVariableWithRef;
    }

    private ITypeVariableSymbolWithRef createTypeVariableWithRef(String name) {
        ITypeVariableSymbolWithRef typeSymbolWithRef = symbolFactory.createMinimalTypeVariableSymbolWithRef(name);
        typeSymbolWithRef.setType(symbolFactory.createUnionTypeSymbol());
        return typeSymbolWithRef;
    }

    private ITypeVariableSymbol createTypeVariable(String name) {
        ITypeVariableSymbol typeVariableSymbol = symbolFactory.createMinimalTypeVariableSymbol(name);
        typeVariableSymbol.setType(symbolFactory.createUnionTypeSymbol());
        return typeVariableSymbol;
    }

    private void defineBitLevelOperators() {

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] intResultingNonAssignOperators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd),
                pair("<<", TokenTypes.ShiftLeft),
                pair(">>", TokenTypes.ShiftRight),
        };
        for (Pair<String, Integer> operator : intResultingNonAssignOperators) {
            addToBinaryOperators(operator, intTypeConstraint, intTypeConstraint, intTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as int} x ~{as int} -> int
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] intResultingAssignOperators = new Pair[]{
                pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("&=", TokenTypes.BitwiseAndAssign),
                pair("<<=", TokenTypes.ShiftLeftAssign),
                pair(">>=", TokenTypes.ShiftRightAssign),
        };
        for (Pair<String, Integer> operator : intResultingAssignOperators) {
            addToBinaryOperators(operator, intTypeConstraint, intTypeConstraint, intTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //Tvar x ~{as int} -> int \ Tvar < ~{as int} / Tvar > int
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] stringResultingOperators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd),
                pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("&=", TokenTypes.BitwiseAndAssign),
        };
        for (Pair<String, Integer> operator : stringResultingOperators) {
            addToBinaryOperators(operator, stringTypeConstraint, stringTypeConstraint, stringTypeSymbol);
        }

        Pair<String, Integer> bitwiseNot = pair("~", TokenTypes.BitwiseNot);
        addToUnaryOperators(bitwiseNot, intTypeConstraint, intTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} -> int
        addToUnaryOperators(bitwiseNot, stringTypeConstraint, stringTypeSymbol);
    }

    private void defineComparisonOperators() {

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("==", TokenTypes.Equal),
                pair("===", TokenTypes.Identical),
                pair("!=", TokenTypes.NotEqual),
                pair("!==", TokenTypes.NotIdentical),
                pair("<", TokenTypes.LessThan),
                pair("<=", TokenTypes.LessEqualThan),
                pair(">", TokenTypes.GreaterThan),
                pair(">=", TokenTypes.GreaterEqualThan),
        };
        for (Pair<String, Integer> operator : operators) {
            addToBinaryOperators(operator, mixedTypeConstraint, mixedTypeConstraint, boolTypeSymbol);
        }
    }


    private void defineTernaryOperator() {
        IFunctionTypeSymbol function;

        //false x Ttrue x Tfalse -> Tfalse
        Deque<ITypeVariableSymbol> typeVariables = new ArrayDeque<>();
        ITypeVariableSymbolWithRef cond = createTypeVariableWithRef("$cond");
        ITypeVariableSymbolWithRef ifTypeVariable = createTypeVariableWithRef("$if");
        ITypeVariableSymbolWithRef elseTypeVariable = createTypeVariableWithRef("$else");
        ITypeVariableSymbolWithRef rtn = createTypeVariableWithRef("return");
        rtn.setConstraint(new TransferConstraint(elseTypeVariable));
        typeVariables.add(rtn);
        List<ITypeVariableSymbolWithRef> parameters = Arrays.asList(cond, ifTypeVariable, elseTypeVariable);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("?", parameters, rtn, typeVariables);
        function.addInputConstraint("$cond", falseTypeConstraint);
        function.addInputConstraint("$if", mixedTypeConstraint);
        function.addInputConstraint("$else", mixedTypeConstraint);
        addToOperators(TokenTypes.QuestionMark, function);

        //true x Ttrue x Tfalse -> Ttrue
        typeVariables = new ArrayDeque<>();
        cond = createTypeVariableWithRef("$cond");
        ifTypeVariable = createTypeVariableWithRef("$if");
        elseTypeVariable = createTypeVariableWithRef("$else");
        rtn = createTypeVariableWithRef("return");
        rtn.setConstraint(new TransferConstraint(ifTypeVariable));
        typeVariables.add(rtn);
        parameters = Arrays.asList(cond, ifTypeVariable, elseTypeVariable);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("?", parameters, rtn, typeVariables);
        function.addInputConstraint("$cond", trueTypeConstraint);
        function.addInputConstraint("$if", mixedTypeConstraint);
        function.addInputConstraint("$else", mixedTypeConstraint);
        addToOperators(TokenTypes.QuestionMark, function);

        //bool x Ttrue x Tfalse -> {Ttrue V Tfalse}
        typeVariables = new ArrayDeque<>();
        cond = createTypeVariableWithRef("$cond");
        ifTypeVariable = createTypeVariableWithRef("$if");
        elseTypeVariable = createTypeVariableWithRef("$else");
        rtn = createTypeVariableWithRef("return");
        ITypeVariableSymbol ifElse = createTypeVariable("$ifElse");
        ifElse.setConstraint(new UnionConstraint(ifTypeVariable, elseTypeVariable));
        rtn.setConstraint(new TransferConstraint(ifElse));
        typeVariables.add(ifElse);
        typeVariables.add(rtn);
        parameters = Arrays.asList(cond, ifTypeVariable, elseTypeVariable);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("?", parameters, rtn, typeVariables);
        function.addInputConstraint("$cond", boolTypeConstraint);
        function.addInputConstraint("$if", mixedTypeConstraint);
        function.addInputConstraint("$else", mixedTypeConstraint);
        addToOperators(TokenTypes.QuestionMark, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x Ttrue x Tfalse -> {Ttrue V Tfalse}
    }


    private void defineArithmeticOperators() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply),
                pair("+=", TokenTypes.PlusAssign),
                pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign),
        };

        for (Pair<String, Integer> operator : operators) {
            addToBinaryOperators(operator, intTypeConstraint, intTypeConstraint, intTypeSymbol);
            addToBinaryOperators(operator, floatTypeConstraint, floatTypeConstraint, floatTypeSymbol);
            addToBinaryOperators(operator, numTypeConstraint, numTypeConstraint, numTypeSymbol);
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] nonAssignOperators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply)
        };
        for (Pair<String, Integer> operator : nonAssignOperators) {
            addToBinaryOperators(operator, boolTypeConstraint, boolTypeConstraint, intTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as num} x ~{as num} -> num
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] assignOperators = new Pair[]{
                pair("+=", TokenTypes.PlusAssign),
                pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign),
        };
        for (Pair<String, Integer> operator : assignOperators) {
            //Tvar x bool -> int \ Tvar < bool /  Tvar > int
            IFunctionTypeSymbol function =
                    symbolFactory.createAssignFunctionTypeSymbol(operator.first, parameterIds, intTypeSymbol);
            function.addInputConstraint("$lhs", boolTypeConstraint);
            function.addInputConstraint("$rhs", boolTypeConstraint);
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //Tvar x ~{as num} -> Tvar \ Tvar < ~{as num} / Tvar > num
        }

        addToBinaryOperators(pair("+", TokenTypes.Plus), arrayTypeConstraint, arrayTypeConstraint, arrayTypeSymbol);
        addToBinaryOperators(
                pair("+=", TokenTypes.PlusAssign), arrayTypeConstraint, arrayTypeConstraint, arrayTypeSymbol);

        createDivOperators();
        createModuloOperators();
        createUnaryArithmeticOperators();
    }

    private void createDivOperators() {
        Pair<String, Integer> div = pair("/", TokenTypes.Divide);
        addToBinaryOperators(div, boolTypeConstraint, boolTypeConstraint, intOrFalse);
        addToBinaryOperators(div, floatTypeConstraint, floatTypeConstraint, floatOrFalse);
        addToBinaryOperators(div, numTypeConstraint, numTypeConstraint, numOrFalse);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as num} x ~{as num} -> {num V false}

        IFunctionTypeSymbol function;

        //Tvar x bool -> {int V false} \ Tvar < bool / Tvar > {int V false}
        function = symbolFactory.createAssignFunctionTypeSymbol("/=", parameterIds, intOrFalse);
        function.addInputConstraint("$lhs", boolTypeConstraint);
        function.addInputConstraint("$rhs", boolTypeConstraint);
        addToOperators(TokenTypes.DivideAssign, function);

        //Tvar x float -> {float V false} \ Tvar < float / Tvar > {float V false}
        function = symbolFactory.createAssignFunctionTypeSymbol("/=", parameterIds, floatOrFalse);
        function.addInputConstraint("$lhs", floatTypeConstraint);
        function.addInputConstraint("$rhs", floatTypeConstraint);
        addToOperators(TokenTypes.DivideAssign, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //Tvar x ~{as float} -> {float V false} \ Tvar < ~{as float} / Tvar > {float V false}

        //Tvar x num -> {num V false} \ Tvar < num / Tvar > {num V false}
        function = symbolFactory.createAssignFunctionTypeSymbol("/=", parameterIds, numOrFalse);
        function.addInputConstraint("$lhs", numTypeConstraint);
        function.addInputConstraint("$rhs", numTypeConstraint);
        addToOperators(TokenTypes.DivideAssign, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //Tvar x ~{as num} -> Tvar \ Tvar < ~{as num} / Tvar > {num V false}
    }

    private void createModuloOperators() {
        Pair<String, Integer> modulo = pair("%", TokenTypes.Modulo);
        addToBinaryOperators(modulo, intTypeConstraint, intTypeConstraint, intOrFalse);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} x ~{as int} -> {int V false}

        IFunctionTypeSymbol function;

        //Tvar x int -> {int V false} \ Tvar < int / Tvar > {int V false}
        function = symbolFactory.createAssignFunctionTypeSymbol("%=", parameterIds, intOrFalse);
        function.addInputConstraint("$lhs", intTypeConstraint);
        function.addInputConstraint("$rhs", intTypeConstraint);
        addToOperators(TokenTypes.ModuloAssign, function);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //Tvar x ~{as int} -> {int V false} \ Tvar < ~{as int} / Tvar > {int V false}
    }

    private void createUnaryArithmeticOperators() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] incrDecrOperators = new Pair[]{
                pair("++", TokenTypes.PRE_INCREMENT),
                pair("++", TokenTypes.POST_INCREMENT),
                pair("--", TokenTypes.PRE_DECREMENT),
                pair("--", TokenTypes.POST_DECREMENT),
        };
        for (Pair<String, Integer> operator : incrDecrOperators) {
            addToUnaryOperators(operator, boolTypeConstraint, boolTypeSymbol);
            addToUnaryOperators(operator, intTypeConstraint, intTypeSymbol);
            addToUnaryOperators(operator, floatTypeConstraint, floatTypeSymbol);
            addToUnaryOperators(operator, numTypeConstraint, numTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //Tvar -> Tvar \ Tvar > num, Tvar < ~{as num}
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] unaryPlusMinusOperators = new Pair[]{
                pair("-", TokenTypes.UNARY_MINUS),
                pair("+", TokenTypes.UNARY_PLUS)
        };
        for (Pair<String, Integer> operator : unaryPlusMinusOperators) {
            addToUnaryOperators(operator, boolTypeConstraint, intTypeSymbol);
            addToUnaryOperators(operator, intTypeConstraint, intTypeSymbol);
            addToUnaryOperators(operator, floatTypeConstraint, floatTypeSymbol);
            addToUnaryOperators(operator, numTypeConstraint, numTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as num} -> num
        }
    }

    private void defineDotOperator() {
        addToBinaryOperators(pair(".", TokenTypes.Dot), stringTypeConstraint, stringTypeConstraint, stringTypeSymbol);
        addToBinaryOperators(
                pair(".=", TokenTypes.DotAssign), stringTypeConstraint, stringTypeConstraint, stringTypeSymbol);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as string} x ~{as string} -> string

        //Tvar x ~{as string} -> Tvar \ Tvar < ~{as string} / Tvar > int
        //TODO rstoll TINS-347 create overloads for conversion constraints
    }

    private void defineInstanceOfOperator() {
        //T x T -> bool
        addToBinaryOperators(
                pair("instanceof", TokenTypes.Instanceof), mixedTypeConstraint, mixedTypeConstraint, boolTypeSymbol);
    }

    private void defineCloneAndNewOperator() {
        //T -> T
        IFunctionTypeSymbol function = symbolFactory.createIdentityFunctionTypeSymbol("clone", "$expr");
        function.addInputConstraint("$expr", mixedTypeConstraint);
        addToOperators(TokenTypes.Clone, function);

        //TODO TINS-349 structural constraints
        //not all classes return itself, some return null as well in error cases
        //see https://wiki.php.net/rfc/internal_constructor_behaviour
        //T -> T
        function = symbolFactory.createIdentityFunctionTypeSymbol("new", "$expr");
        function.addInputConstraint("$expr", mixedTypeConstraint);
        addToOperators(TokenTypes.New, function);
    }

    private void defineAtAndCastOperator() {
        //T -> T
        IFunctionTypeSymbol function = symbolFactory.createIdentityFunctionTypeSymbol("@", "$expr");
        function.addInputConstraint("$expr", mixedTypeConstraint);
        addToOperators(TokenTypes.At, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //T x T2 -> T \ T2 < ~{T}

        ITypeVariableSymbolWithRef lhs = createByRefTypeVariableWithRef("$lhs");
        ITypeVariableSymbolWithRef rhs = createTypeVariableWithRef("$rhs");
        ITypeVariableSymbolWithRef rtn = createTypeVariableWithRef("return");
        rtn.setConstraint(new TransferConstraint(lhs));
        Deque<ITypeVariableSymbol> typeVariables = new ArrayDeque<>();
        typeVariables.add(rtn);
        List<ITypeVariableSymbolWithRef> parameters = Arrays.asList(lhs, rhs);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("cast", parameters, rtn, typeVariables);
        function.addInputConstraint("$lhs", mixedTypeConstraint);
        function.addInputConstraint("$rhs", mixedTypeConstraint);
        addToOperators(TokenTypes.CAST, function);
    }

    private IFunctionTypeSymbol addToBinaryOperators(Pair<String, Integer> operator,
            TypeConstraint leftParameterType, TypeConstraint rightParameterType, ITypeSymbol returnType) {

        IFunctionTypeSymbol function = symbolFactory.createConstantFunctionTypeSymbol(
                operator.first, parameterIds, returnType);
        function.addInputConstraint("$lhs", leftParameterType);
        function.addInputConstraint("$rhs", rightParameterType);
        addToOperators(operator.second, function);
        return function;
    }

    private IFunctionTypeSymbol addToUnaryOperators(
            Pair<String, Integer> operator, TypeConstraint formalParameterType, ITypeSymbol returnType) {

        IFunctionTypeSymbol function = symbolFactory.createConstantFunctionTypeSymbol(
                operator.first, unaryParameterId, returnType);

        function.addInputConstraint("$expr", formalParameterType);
        addToOperators(operator.second, function);
        return function;
    }

    private void addToOperators(int operatorType, IFunctionTypeSymbol functionTypeSymbol) {
        IOverloadSymbol overloadSymbol = builtInOperators.get(operatorType);
        overloadSymbol.addOverload(functionTypeSymbol);
    }

}

