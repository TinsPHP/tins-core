/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.constraints.TypeVariableCollection;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_EXPR;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RETURN;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RHS;

public class OperatorProvider extends AProvider implements IOperatorsProvider
{

    private Map<Integer, IMinimalMethodSymbol> builtInOperators;

    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            StandardConstraintAndVariables standardConstraintAndVariables) {
        super(theSymbolFactory, theOverloadResolver, standardConstraintAndVariables);
    }

    @Override
    public Map<Integer, IMinimalMethodSymbol> getOperators() {
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
            builtInOperators.put(operatorType.second, symbolFactory.createMinimalMethodSymbol(operatorType.first));
        }
    }

    private void defineLogicOperators() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] orOperators = new Pair[]{
                pair("or", TokenTypes.LogicOrWeak),
                pair("||", TokenTypes.LogicOr)
        };
        for (Pair<String, Integer> operator : orOperators) {
            //false x false -> false
            addToBinaryOperators(operator, std.falseTypeConstraint, std.falseTypeConstraint, std.falseTypeConstraint);
            //true x bool -> true
            addToBinaryOperators(operator, std.trueTypeConstraint, std.boolTypeConstraint, std.trueTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //true x ~{as bool} -> true

            //bool x true -> true
            addToBinaryOperators(operator, std.boolTypeConstraint, std.trueTypeConstraint, std.trueTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x true -> true

            //bool x bool -> bool
            addToBinaryOperators(operator, std.boolTypeConstraint, std.boolTypeConstraint, std.boolTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool

        }

        Pair<String, Integer> xorWeak = pair("xor", TokenTypes.LogicXorWeak);
        //false x true -> true
        addToBinaryOperators(xorWeak, std.falseTypeConstraint, std.trueTypeConstraint, std.trueTypeConstraint);
        //true x false -> true
        addToBinaryOperators(xorWeak, std.trueTypeConstraint, std.falseTypeConstraint, std.trueTypeConstraint);
        //false x false -> false
        addToBinaryOperators(xorWeak, std.falseTypeConstraint, std.falseTypeConstraint, std.falseTypeConstraint);
        //true x true -> false
        addToBinaryOperators(xorWeak, std.trueTypeConstraint, std.trueTypeConstraint, std.falseTypeConstraint);
        //bool x bool -> bool
        addToBinaryOperators(xorWeak, std.boolTypeConstraint, std.boolTypeConstraint, std.boolTypeConstraint);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x ~{as bool} -> bool

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] andOperators = new Pair[]{
                pair("and", TokenTypes.LogicAndWeak),
                pair("&&", TokenTypes.LogicAnd)
        };
        for (Pair<String, Integer> operator : andOperators) {
            //false x bool -> false
            addToBinaryOperators(operator, std.falseTypeConstraint, std.boolTypeConstraint, std.falseTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //false x ~{as bool} -> false

            //bool x false -> false
            addToBinaryOperators(operator, std.boolTypeConstraint, std.falseTypeConstraint, std.falseTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{bool} x false -> false

            //true x true -> true
            addToBinaryOperators(operator, std.trueTypeConstraint, std.trueTypeConstraint, std.trueTypeConstraint);
            //bool x bool -> bool
            addToBinaryOperators(operator, std.boolTypeConstraint, std.boolTypeConstraint, std.boolTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool
        }

        Pair<String, Integer> logicNot = new Pair<>("!", TokenTypes.LogicNot);
        addToUnaryOperators(logicNot, std.falseTypeConstraint, std.trueTypeConstraint);
        addToUnaryOperators(logicNot, std.trueTypeConstraint, std.falseTypeConstraint);
        addToUnaryOperators(logicNot, std.boolTypeConstraint, std.boolTypeConstraint);
    }

    private void defineAssignmentOperators() {
        IFunctionType function;

        //Tlhs x Trhs -> Tlhs \ Tlhs > Trhs
        IVariable lhs = symbolFactory.createVariable("$lhs", T_LHS);
        IVariable rhs = symbolFactory.createVariable("$rhs", T_RHS);
        IVariable rtn = symbolFactory.createVariable("rtn", T_LHS);
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound(T_LHS, new TypeVariableConstraint(T_RHS));
        function = symbolFactory.createFunctionType("=", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.Assign, function);

        //Other assignment operators can be found in the corresponding sections.
        //For instance, += is in createAssignmentOperators
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
            //int x int -> int
            addToBinaryOperators(operator, std.intTypeConstraint, std.intTypeConstraint, std.intTypeConstraint);
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
            //int x int -> int
            addToBinaryOperators(operator, std.intTypeConstraint, std.intTypeConstraint, std.intTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(int | ~{as int}) x ~{as int} -> int
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
            //string x string -> string
            addToBinaryOperators(
                    operator, std.stringTypeConstraint, std.stringTypeConstraint, std.stringTypeConstraint);
        }

        Pair<String, Integer> bitwiseNot = pair("~", TokenTypes.BitwiseNot);
        //int -> int
        addToUnaryOperators(bitwiseNot, std.intTypeConstraint, std.intTypeConstraint);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} -> int

        //string -> string
        addToUnaryOperators(bitwiseNot, std.stringTypeConstraint, std.stringTypeConstraint);
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
            addToBinaryOperators(operator, std.mixedTypeConstraint, std.mixedTypeConstraint, std.boolTypeConstraint);
        }
    }

    private void defineTernaryOperator() {
        final String tCondition = "Tcondition";
        final String tIf = "Tif";
        String tElse = "Telse";

        //false x mixed x Telse -> Telse
        //expanded: false x mixed x Telse -> Treturn \ Treturn > Telse
        IVariable conditionVariable = symbolFactory.createVariable("$condition", tCondition);
        IVariable ifVariable = symbolFactory.createVariable("$if", tIf);
        IVariable elseVariable = symbolFactory.createVariable("$else", tElse);
        IVariable rtn = std.variableTypedReturnVariable;
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addUpperBound(tCondition, std.falseTypeConstraint);
        conditionVariable.setHasFixedType();
        collection.addUpperBound(tIf, std.mixedTypeConstraint);
        ifVariable.setHasFixedType();
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(tElse));
        IFunctionType function = symbolFactory.createFunctionType(
                "?", collection, Arrays.asList(conditionVariable, ifVariable, elseVariable), rtn);
        addToOperators(TokenTypes.QuestionMark, function);


        //true x Tif x mixed -> Tif
        //expanded: true x Tif x mixed -> Treturn \ Treturn > Tif
        conditionVariable = symbolFactory.createVariable("$condition", tCondition);
        ifVariable = symbolFactory.createVariable("$if", tIf);
        elseVariable = symbolFactory.createVariable("$else", tElse);
        rtn = std.variableTypedReturnVariable;
        collection = new TypeVariableCollection(overloadResolver);
        collection.addUpperBound(tCondition, std.trueTypeConstraint);
        conditionVariable.setHasFixedType();
        collection.addUpperBound(tElse, std.mixedTypeConstraint);
        elseVariable.setHasFixedType();
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(tIf));
        function = symbolFactory.createFunctionType(
                "?", collection, Arrays.asList(conditionVariable, ifVariable, elseVariable), rtn);
        addToOperators(TokenTypes.QuestionMark, function);

        //bool x Tif x Telse -> (Tif | Telse)
        //expanded: bool x Tif x Telse -> Treturn \ Treturn > Tif, Treturn > Telse
        conditionVariable = symbolFactory.createVariable("$condition", tCondition);
        ifVariable = symbolFactory.createVariable("$if", tIf);
        elseVariable = symbolFactory.createVariable("$else", tElse);
        rtn = std.variableTypedReturnVariable;
        collection = new TypeVariableCollection(overloadResolver);
        collection.addUpperBound(tCondition, std.boolTypeConstraint);
        conditionVariable.setHasFixedType();
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(tIf));
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(tElse));
        function = symbolFactory.createFunctionType(
                "?", collection, Arrays.asList(conditionVariable, ifVariable, elseVariable), rtn);
        addToOperators(TokenTypes.QuestionMark, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x Tif x Telse -> (Tif | Telse)
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
            //T x T -> T \ T < num, T > T
            IVariable lhs = std.tLhs;
            IVariable rhs = std.tRhs;
            IVariable rtn = std.tReturn;
            ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
            collection.addUpperBound("T", std.numTypeConstraint);
            collection.addLowerBound("T", new TypeVariableConstraint("T"));
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, Arrays.asList(lhs, rhs), rtn);
            addToOperators(operator.second, function);
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] nonAssignOperators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply)
        };
        for (Pair<String, Integer> operator : nonAssignOperators) {
            //bool x bool -> int
            addToBinaryOperators(operator, std.boolTypeConstraint, std.boolTypeConstraint, std.intTypeConstraint);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as T} x ~{as T} -> T \ T < num
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] assignOperators = new Pair[]{
                pair("+=", TokenTypes.PlusAssign),
                pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign),
        };
        for (Pair<String, Integer> operator : assignOperators) {
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(T | ~{as T}) x T -> T \ T < num
            //(T | ~{as T}) x ~{as T} -> T \ T < num
        }

        //array x array -> array
        addToBinaryOperators(pair("+", TokenTypes.Plus),
                std.arrayTypeConstraint, std.arrayTypeConstraint, std.arrayTypeConstraint);
        addToBinaryOperators(pair("+=", TokenTypes.PlusAssign),
                std.arrayTypeConstraint, std.arrayTypeConstraint, std.arrayTypeConstraint);

        createDivOperators();
        createModuloOperators();
        createUnaryArithmeticOperators();
    }

    private void createDivOperators() {
        IFunctionType function;

        //bool x bool -> (int | false)
        addToBinaryOperators(pair("/", TokenTypes.Divide),
                std.boolTypeConstraint, std.boolTypeConstraint, std.intOrFalseTypeConstraint);


        //T x T -> (T | false) \ float < T < num
        //expanded: T x T -> Treturn \ float < T < num, Treturn > T, Treturn > false
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        IVariable lhs = std.tLhs;
        IVariable rhs = std.tRhs;
        IVariable rtn = symbolFactory.createVariable("rtn", T_RETURN);
        collection.addLowerBound("T", std.floatTypeConstraint);
        collection.addUpperBound("T", std.numTypeConstraint);
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint("T"));
        collection.addLowerBound(T_RETURN, std.falseTypeConstraint);
        function = symbolFactory.createFunctionType("/", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.Divide, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //float x ~{as float} -> (float | false)
        //~{as float} x float -> (float | false)
        //~{as num} x ~{as num} -> (num | false)


        //(int | bool) x bool -> (int | false)
        IUnionTypeSymbol intOrBoolTypeSymbol = symbolFactory.createUnionTypeSymbol();
        intOrBoolTypeSymbol.addTypeSymbol(std.intTypeSymbol);
        intOrBoolTypeSymbol.addTypeSymbol(std.boolTypeSymbol);
        intOrBoolTypeSymbol.seal();
        addToBinaryOperators(pair("/=", TokenTypes.DivideAssign),
                new TypeConstraint(intOrBoolTypeSymbol), std.boolTypeConstraint, std.intOrFalseTypeConstraint);

        //(T | false) x T -> (T | false) \ float < T < num
        //expanded: Tlhs x T -> Treturn \ float < T < num, Tlhs > T, Tlhs > false
        lhs = symbolFactory.createVariable("$lhs", T_LHS);
        rhs = std.tRhs;
        rtn = symbolFactory.createVariable("rtn", T_LHS);
        collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound("T", std.floatTypeConstraint);
        collection.addUpperBound("T", std.numTypeConstraint);
        collection.addLowerBound(T_LHS, new TypeVariableConstraint("T"));
        collection.addLowerBound(T_LHS, std.falseTypeConstraint);
        function = symbolFactory.createFunctionType("/=", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.DivideAssign, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(T | ~{as T}) x ~{as T} -> (T | false) \ float < T < num
    }

    private void createModuloOperators() {
        //int x int -> (int | false)
        addToBinaryOperators(pair("%", TokenTypes.Modulo),
                std.intTypeConstraint, std.intTypeConstraint, std.intOrFalseTypeConstraint);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} x ~{as int} -> (int | false)

        //(int | false) x int -> (int | false)
        addToBinaryOperators(pair("%=", TokenTypes.ModuloAssign),
                std.intOrFalseTypeConstraint, std.intTypeConstraint, std.intOrFalseTypeConstraint);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(int | ~{as int}) x ~{as int} -> (int | false)
    }

    private void createUnaryArithmeticOperators() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] incrDecrOperators = new Pair[]{
                pair("++", TokenTypes.PRE_INCREMENT),
                pair("++", TokenTypes.POST_INCREMENT),
                pair("--", TokenTypes.PRE_DECREMENT),
                pair("--", TokenTypes.POST_DECREMENT),
        };

        IUnionTypeSymbol numOrBoolTypeSymbol = symbolFactory.createUnionTypeSymbol();
        numOrBoolTypeSymbol.addTypeSymbol(std.numTypeSymbol);
        numOrBoolTypeSymbol.addTypeSymbol(std.boolTypeSymbol);
        numOrBoolTypeSymbol.seal();

        for (Pair<String, Integer> operator : incrDecrOperators) {
            //T -> T \ T < (num | bool), T > T
            IVariable expr = std.tExpr;
            IVariable rtn = std.tReturn;
            ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
            collection.addUpperBound("T", new TypeConstraint(numOrBoolTypeSymbol));
            collection.addLowerBound("T", new TypeVariableConstraint("T"));
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, Arrays.asList(expr), rtn);
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(T | ~T) -> T \ T < num, T > T
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] unaryPlusMinusOperators = new Pair[]{
                pair("-", TokenTypes.UNARY_MINUS),
                pair("+", TokenTypes.UNARY_PLUS)
        };
        for (Pair<String, Integer> operator : unaryPlusMinusOperators) {
            //bool x int
            addToUnaryOperators(operator, std.boolTypeConstraint, std.intTypeConstraint);
            //T -> T \ T < num, T > T
            IVariable expr = std.tExpr;
            IVariable rtn = std.tReturn;
            ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
            collection.addUpperBound("T", std.numTypeConstraint);
            collection.addLowerBound("T", new TypeVariableConstraint("T"));
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, Arrays.asList(expr), rtn);
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as num} -> num
        }
    }

    private void defineDotOperator() {
        addToBinaryOperators(
                pair(".", TokenTypes.Dot), std.stringTypeConstraint, std.stringTypeConstraint,
                std.stringTypeConstraint);
        addToBinaryOperators(
                pair(".=", TokenTypes.DotAssign), std.stringTypeConstraint, std.stringTypeConstraint,
                std.stringTypeConstraint);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as string} x ~{as string} -> string

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(string | ~{as string}) x ~{as string} -> string
    }

    private void defineInstanceOfOperator() {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: object x mixed -> bool
        //mixed x mixed -> bool
        Pair<String, Integer> instanceOf = pair("instanceof", TokenTypes.Instanceof);
        addToBinaryOperators(instanceOf, std.mixedTypeConstraint, std.mixedTypeConstraint, std.boolTypeConstraint);
    }

    private void defineCloneAndNewOperator() {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: T -> T \ T < object
        //T -> T
        IVariable expr = std.tExpr;
        IVariable rtn = std.tReturn;
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        IFunctionType function = symbolFactory.createFunctionType(
                "clone", collection, Arrays.asList(expr), rtn);
        addToOperators(TokenTypes.Clone, function);

        //TODO TINS-349 structural constraints
        //not all classes return itself, some return null as well in error cases
        //see https://wiki.php.net/rfc/internal_constructor_behaviour
        //T -> T
        expr = std.tExpr;
        rtn = std.tReturn;
        collection = new TypeVariableCollection(overloadResolver);
        function = symbolFactory.createFunctionType("new", collection, Arrays.asList(expr), rtn);
        addToOperators(TokenTypes.New, function);
    }

    private void defineAtAndCastOperator() {
        //T -> T
        IVariable expr = std.tExpr;
        IVariable rtn = std.tReturn;
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        IFunctionType function = symbolFactory.createFunctionType(
                "@", collection, Arrays.asList(expr), rtn);
        addToOperators(TokenTypes.At, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //T1 x T2 -> T1 \ T2 < ~{as T1}
        //simplified version for now: T1 x T2 -> T1 or expanded: Tlhs x Trhs -> Treturn \ Treturn > Tlhs
        IVariable lhs = symbolFactory.createVariable("$lhs", T_LHS);
        lhs.setHasFixedType();
        IVariable rhs = symbolFactory.createVariable("$rhs", T_RHS);
        rtn = std.fixTypedReturnVariable;
        collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(T_LHS));
        function = symbolFactory.createFunctionType("cast", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.CAST, function);
    }

    private void addToBinaryOperators(
            Pair<String, Integer> operator,
            TypeConstraint leftBound,
            TypeConstraint rightBound,
            TypeConstraint returnBound) {

        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addUpperBound(T_LHS, leftBound);
        collection.addUpperBound(T_RHS, rightBound);
        collection.addLowerBound(T_RETURN, returnBound);

        IFunctionType function = symbolFactory.createFunctionType(
                operator.first, collection, std.binaryParameterIds, std.fixTypedReturnVariable);

        addToOperators(operator.second, function);
    }

    private void addToUnaryOperators(
            Pair<String, Integer> operator, TypeConstraint formalBound, TypeConstraint returnBound) {

        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addUpperBound(T_EXPR, formalBound);
        collection.addLowerBound(T_RETURN, returnBound);

        IFunctionType function = symbolFactory.createFunctionType(
                operator.first, collection, std.unaryParameterId, std.fixTypedReturnVariable);

        addToOperators(operator.second, function);
    }

    private void addToOperators(int operatorType, IFunctionType functionTypeSymbol) {
        IMinimalMethodSymbol overloadSymbol = builtInOperators.get(operatorType);
        overloadSymbol.addOverload(functionTypeSymbol);
    }

}

