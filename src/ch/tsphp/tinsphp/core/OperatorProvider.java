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
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;

public class OperatorProvider implements IOperatorsProvider
{
    private final ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;
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
    private final List<String> parameterIds;
    private final List<String> unaryParameterId;

    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {

        symbolFactory = theSymbolFactory;
        primitiveTypes = thePrimitiveType;
        falseTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.FALSE);
        trueTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.TRUE);
        boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);
        intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        floatTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.FLOAT);
        numTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.NUM);
        stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);
        arrayTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.ARRAY);
        falseTypeConstraint = new TypeConstraint(falseTypeSymbol);
        trueTypeConstraint = new TypeConstraint(trueTypeSymbol);
        boolTypeConstraint = new TypeConstraint(boolTypeSymbol);
        intTypeConstraint = new TypeConstraint(intTypeSymbol);
        floatTypeConstraint = new TypeConstraint(floatTypeSymbol);
        numTypeConstraint = new TypeConstraint(numTypeSymbol);
        stringTypeConstraint = new TypeConstraint(stringTypeSymbol);
        arrayTypeConstraint = new TypeConstraint(arrayTypeSymbol);
        mixedTypeConstraint = new TypeConstraint(primitiveTypes.get(PrimitiveTypeNames.MIXED));

        unaryParameterId = Arrays.asList("$expr");
        parameterIds = Arrays.asList("$lhs", "$rhs");
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
        defineArithmeticOperators();
        defineInstanceOfOperator();
        defineDotOperator();
        defineAtOperator();
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
                //pair("cast",TokenTypes.CAST),
                pair("instanceof", TokenTypes.Instanceof),
                //unary operators
                pair("++", TokenTypes.PRE_INCREMENT), pair("--", TokenTypes.PRE_DECREMENT),
                pair("@", TokenTypes.At), pair("~", TokenTypes.BitwiseNot), pair("!", TokenTypes.LogicNot),
                pair("-", TokenTypes.UNARY_MINUS), pair("+", TokenTypes.UNARY_PLUS),
                pair("++", TokenTypes.POST_INCREMENT), pair("--", TokenTypes.POST_DECREMENT),
                pair("clone", TokenTypes.Clone), pair("new", TokenTypes.New)
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
                pair("or", TokenTypes.LogicOrWeak),
                pair("||", TokenTypes.LogicOr)
        };
        for (Pair<String, Integer> operator : andOperators) {
            //TODO rstoll TINS-347 create overloads for conversion constraints
            addToBinaryOperators(operator, falseTypeConstraint, boolTypeConstraint, trueTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //false x ~{as bool} -> false
            addToBinaryOperators(operator, boolTypeConstraint, falseTypeConstraint, trueTypeSymbol);
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
        IFunctionTypeSymbol function;
        Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
        ITypeVariableSymbol lhs = createByRefTypeVariable("$lhs");
        ITypeVariableSymbol rhs = createTypeVariable("$rhs");
        ITypeVariableSymbol rtn = createTypeVariable("return");
        lhs.addConstraint(rhs);
        rtn.addConstraint(lhs);
        typeVariables.put("$lhs", lhs);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("=", parameterIds, typeVariables);
        addToOperators(TokenTypes.Assign, function);

        //Other assignment operators can be found in the corresponding sections.
        //For instance, += is in createAssignmentOperators
    }

    private ITypeVariableSymbol createByRefTypeVariable(String name) {
        ITypeVariableSymbol typeVariableSymbol = symbolFactory.createMinimalTypeVariableSymbol(name);
        typeVariableSymbol.setIsByRef();
        return typeVariableSymbol;
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
            addToBinaryOperators(operator, intTypeConstraint, intTypeConstraint, intTypeSymbol);
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

    @SuppressWarnings("checkstyle:methodlength")
    private void defineArithmeticOperators() {
        IUnionTypeSymbol numOrFalse = symbolFactory.createUnionTypeSymbol();
        numOrFalse.addTypeSymbol(numTypeSymbol);
        numOrFalse.addTypeSymbol(falseTypeSymbol);
        numOrFalse.seal();

        IUnionTypeSymbol floatOrFalse = symbolFactory.createUnionTypeSymbol();
        floatOrFalse.addTypeSymbol(floatTypeSymbol);
        floatOrFalse.addTypeSymbol(falseTypeSymbol);
        floatOrFalse.seal();

        IUnionTypeSymbol intOrFalse = symbolFactory.createUnionTypeSymbol();
        intOrFalse.addTypeSymbol(intTypeSymbol);
        intOrFalse.addTypeSymbol(falseTypeSymbol);
        intOrFalse.seal();


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
            addToBinaryOperators(operator, numTypeConstraint, numTypeConstraint, intTypeSymbol);
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
            ITypeVariableSymbol lhs = createByRefTypeVariable("$lhs");
            ITypeVariableSymbol rhs = createTypeVariable("$rhs");
            ITypeVariableSymbol rtn = createTypeVariable("return");

            lhs.addConstraint(intTypeConstraint);
            rtn.addConstraint(intTypeConstraint);
            Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
            typeVariables.put("$lhs", lhs);
            typeVariables.put("$rhs", rhs);
            typeVariables.put("return", rtn);
            IFunctionTypeSymbol function
                    = symbolFactory.createPolymorphicFunctionTypeSymbol(operator.first, parameterIds, typeVariables);
            function.addParameterConstraint("$lhs", boolTypeConstraint);
            function.addParameterConstraint("$rhs", boolTypeConstraint);
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //Tvar x ~{as num} -> Tvar \ Tvar < ~{as num} / Tvar > num
        }

        addToBinaryOperators(pair("+", TokenTypes.Plus), arrayTypeConstraint, arrayTypeConstraint, arrayTypeSymbol);
        addToBinaryOperators(pair("+=", TokenTypes.PlusAssign), arrayTypeConstraint, arrayTypeConstraint,
                arrayTypeSymbol);

        Pair<String, Integer> div = pair("/", TokenTypes.Divide);
        addToBinaryOperators(div, boolTypeConstraint, boolTypeConstraint, intOrFalse);
        addToBinaryOperators(div, floatTypeConstraint, floatTypeConstraint, floatOrFalse);
        addToBinaryOperators(div, numTypeConstraint, numTypeConstraint, numOrFalse);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as num} x ~{as num} -> {num V false}


        // divAssign operator /=
        {
            IFunctionTypeSymbol function;

            //Tvar x float -> {float V false} \ Tvar < float / Tvar > {float V false}
            ITypeVariableSymbol lhs = createByRefTypeVariable("$lhs");
            ITypeVariableSymbol rhs = createTypeVariable("$rhs");
            ITypeVariableSymbol rtn = createTypeVariable("return");

            TypeConstraint floatOrFalseConstraint = new TypeConstraint(floatOrFalse);
            lhs.addConstraint(floatOrFalseConstraint);
            rtn.addConstraint(floatOrFalseConstraint);
            Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
            typeVariables.put("$lhs", lhs);
            typeVariables.put("$rhs", rhs);
            typeVariables.put("return", rtn);
            function = symbolFactory.createPolymorphicFunctionTypeSymbol("/=", parameterIds, typeVariables);
            function.addParameterConstraint("$lhs", floatTypeConstraint);
            function.addParameterConstraint("$rhs", floatTypeConstraint);
            addToOperators(TokenTypes.DivideAssign, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //Tvar x float -> {float V false} \ Tvar < ~{as float} / Tvar > {float V false}

            //Tvar x num -> {num V false} \ Tvar < num / Tvar > {num V false}
            lhs = createByRefTypeVariable("$lhs");
            rhs = createTypeVariable("$rhs");
            rtn = createTypeVariable("return");

            TypeConstraint numOrFalseConstraint = new TypeConstraint(numOrFalse);
            lhs.addConstraint(numOrFalseConstraint);
            rtn.addConstraint(numOrFalseConstraint);
            typeVariables = new HashMap<>();
            typeVariables.put("$lhs", lhs);
            typeVariables.put("$rhs", rhs);
            typeVariables.put("return", rtn);
            function = symbolFactory.createPolymorphicFunctionTypeSymbol("/=", parameterIds, typeVariables);
            function.addParameterConstraint("$lhs", numTypeConstraint);
            function.addParameterConstraint("$rhs", numTypeConstraint);
            addToOperators(TokenTypes.DivideAssign, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //Tvar x ~{as num} -> Tvar \ Tvar < ~{as num} / Tvar > {num V false}
        }

        Pair<String, Integer> modulo = pair("%", TokenTypes.Modulo);
        addToBinaryOperators(modulo, intTypeConstraint, intTypeConstraint, intOrFalse);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} x ~{as int} -> {int V false}

        //Tvar x int -> {int V false} \ Tvar < int / Tvar > {int V false}
        IFunctionTypeSymbol function;
        ITypeVariableSymbol lhs = createByRefTypeVariable("$lhs");
        ITypeVariableSymbol rhs = createTypeVariable("$rhs");
        ITypeVariableSymbol rtn = createTypeVariable("return");

        TypeConstraint intOrFalseConstraint = new TypeConstraint(intOrFalse);
        lhs.addConstraint(intOrFalseConstraint);
        rtn.addConstraint(intOrFalseConstraint);
        Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
        typeVariables.put("$lhs", lhs);
        typeVariables.put("$rhs", rhs);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("/=", parameterIds, typeVariables);
        function.addParameterConstraint("$lhs", intTypeConstraint);
        function.addParameterConstraint("$rhs", intTypeConstraint);
        addToOperators(TokenTypes.DivideAssign, function);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //Tvar x ~{as int} -> {int V false} \ Tvar < ~{as int} / Tvar > {int V false}


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

    private void defineInstanceOfOperator() {
        IFunctionTypeSymbol function;
        List<String> instanceofParamIds = Arrays.asList("$cond", "$if", "$else");


        //false x Ttrue x Tfalse -> Tfalse
        Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
        ITypeVariableSymbol cond = createTypeVariable("$cond");
        ITypeVariableSymbol ifTypeVariable = createTypeVariable("$if");
        ITypeVariableSymbol elseTypeVariable = createTypeVariable("$else");
        ITypeVariableSymbol rtn = createTypeVariable("return");
        rtn.addConstraint(elseTypeVariable);
        typeVariables.put("$cond", cond);
        typeVariables.put("$if", ifTypeVariable);
        typeVariables.put("$else", elseTypeVariable);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("instanceof", instanceofParamIds, typeVariables);
        function.addParameterConstraint("$cond", falseTypeConstraint);
        addToOperators(TokenTypes.Assign, function);

        //true x Ttrue x Tfalse -> Ttrue
        typeVariables = new HashMap<>();
        cond = createTypeVariable("$cond");
        ifTypeVariable = createTypeVariable("$if");
        elseTypeVariable = createTypeVariable("$else");
        rtn = createTypeVariable("return");
        rtn.addConstraint(ifTypeVariable);
        typeVariables.put("$cond", cond);
        typeVariables.put("$if", ifTypeVariable);
        typeVariables.put("$else", elseTypeVariable);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("instanceof", instanceofParamIds, typeVariables);
        function.addParameterConstraint("$cond", trueTypeConstraint);
        addToOperators(TokenTypes.Assign, function);

        //bool x Ttrue x Tfalse -> {Ttrue V Tfalse}
        typeVariables = new HashMap<>();
        cond = createTypeVariable("$cond");
        ifTypeVariable = createTypeVariable("$if");
        elseTypeVariable = createTypeVariable("$else");
        rtn = createTypeVariable("return");
        rtn.addConstraint(ifTypeVariable);
        rtn.addConstraint(elseTypeVariable);
        typeVariables.put("$cond", cond);
        typeVariables.put("$if", ifTypeVariable);
        typeVariables.put("$else", elseTypeVariable);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("instanceof", instanceofParamIds, typeVariables);
        function.addParameterConstraint("$cond", boolTypeConstraint);
        addToOperators(TokenTypes.Assign, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x Ttrue x Tfalse -> {Ttrue V Tfalse}
    }

    private void defineDotOperator() {
        addToBinaryOperators(pair(".", TokenTypes.Dot), stringTypeConstraint, stringTypeConstraint, stringTypeSymbol);
        addToBinaryOperators(pair(".=", TokenTypes.DotAssign), stringTypeConstraint, stringTypeConstraint,
                stringTypeSymbol);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as string} x ~{as string} -> string

        //Tvar x ~{as string} -> Tvar \ Tvar < ~{as string} / Tvar > int
        //TODO rstoll TINS-347 create overloads for conversion constraints
    }

    private void defineCloneAndNewOperator() {
        IFunctionTypeSymbol function;

        //T -> T
        Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
        ITypeVariableSymbol expr = createTypeVariable("$expr");
        ITypeVariableSymbol rtn = createTypeVariable("return");
        rtn.addConstraint(expr);
        typeVariables.put("$expr", expr);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("clone", parameterIds, typeVariables);
        addToOperators(TokenTypes.Clone, function);

        //TODO TINS-349 structural constraints
        //not all classes return itself, some return null as well in error cases
        //see https://wiki.php.net/rfc/internal_constructor_behaviour
        //T -> T
        typeVariables = new HashMap<>();
        expr = createTypeVariable("$expr");
        rtn = createTypeVariable("return");
        rtn.addConstraint(expr);
        typeVariables.put("$expr", expr);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("new", parameterIds, typeVariables);
        addToOperators(TokenTypes.New, function);
    }

    private void defineAtOperator() {
        IFunctionTypeSymbol function;

        //T -> T
        Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();
        ITypeVariableSymbol expr = createTypeVariable("$expr");
        ITypeVariableSymbol rtn = createTypeVariable("return");
        rtn.addConstraint(expr);
        typeVariables.put("$expr", expr);
        typeVariables.put("return", rtn);
        function = symbolFactory.createPolymorphicFunctionTypeSymbol("@", parameterIds, typeVariables);
        addToOperators(TokenTypes.Clone, function);
    }

    private IFunctionTypeSymbol addToBinaryOperators(Pair<String, Integer> operator,
            TypeConstraint leftParameterType, TypeConstraint rightParameterType, ITypeSymbol returnType) {

        IFunctionTypeSymbol function = symbolFactory.createConstantFunctionTypeSymbol(
                operator.first, parameterIds, returnType);
        function.addParameterConstraint("$lhs", leftParameterType);
        function.addParameterConstraint("$lhs", rightParameterType);
        addToOperators(operator.second, function);
        return function;
    }

    private IFunctionTypeSymbol addToUnaryOperators(
            Pair<String, Integer> operator, TypeConstraint formalParameterType, ITypeSymbol returnType) {

        IFunctionTypeSymbol function = symbolFactory.createConstantFunctionTypeSymbol(
                operator.first, unaryParameterId, returnType);

        function.addParameterConstraint("$expr", formalParameterType);
        addToOperators(operator.second, function);
        return function;
    }

    private void addToOperators(int operatorType, IFunctionTypeSymbol functionTypeSymbol) {
        IOverloadSymbol overloadSymbol = builtInOperators.get(operatorType);
        overloadSymbol.addOverload(functionTypeSymbol);
    }

}



