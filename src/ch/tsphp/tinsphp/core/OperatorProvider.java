/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.TypeVariableNames;
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
    private final Map<String, ISymbol> builtInSymbols;

    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            StandardConstraintAndVariables standardConstraintAndVariables,
            Map<String, ISymbol> theBuiltInSymbols) {
        super(theSymbolFactory, theOverloadResolver, standardConstraintAndVariables);
        builtInSymbols = theBuiltInSymbols;
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
        defineCloneAndNewOperator();
        defineAtAndCastOperator();

        defineControlFlowOperators();
        defineGlobalFunctions();
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
                //control flow operators
                pair("if", TokenTypes.If),
                pair("while", TokenTypes.While),
                pair("do", TokenTypes.Do),
                pair("for", TokenTypes.For),
                pair("foreach", TokenTypes.Foreach),
                pair("switch", TokenTypes.Switch),
                pair("catch", TokenTypes.Catch),
                //global function
                pair("echo", TokenTypes.Echo),
                pair("exit", TokenTypes.Exit),
                pair("throw", TokenTypes.Throw),
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
            addToBinaryOperators(operator, std.falseTypeSymbol, std.falseTypeSymbol, std.falseTypeSymbol);
            //true x bool -> true
            addToBinaryOperators(operator, std.trueTypeSymbol, std.boolTypeSymbol, std.trueTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //true x ~{as bool} -> true

            //bool x true -> true
            addToBinaryOperators(operator, std.boolTypeSymbol, std.trueTypeSymbol, std.trueTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x true -> true

            //bool x bool -> bool
            addToBinaryOperators(operator, std.boolTypeSymbol, std.boolTypeSymbol, std.boolTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool

        }

        Pair<String, Integer> xorWeak = pair("xor", TokenTypes.LogicXorWeak);
        //false x true -> true
        addToBinaryOperators(xorWeak, std.falseTypeSymbol, std.trueTypeSymbol, std.trueTypeSymbol);
        //true x false -> true
        addToBinaryOperators(xorWeak, std.trueTypeSymbol, std.falseTypeSymbol, std.trueTypeSymbol);
        //false x false -> false
        addToBinaryOperators(xorWeak, std.falseTypeSymbol, std.falseTypeSymbol, std.falseTypeSymbol);
        //true x true -> false
        addToBinaryOperators(xorWeak, std.trueTypeSymbol, std.trueTypeSymbol, std.falseTypeSymbol);
        //bool x bool -> bool
        addToBinaryOperators(xorWeak, std.boolTypeSymbol, std.boolTypeSymbol, std.boolTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x ~{as bool} -> bool

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] andOperators = new Pair[]{
                pair("and", TokenTypes.LogicAndWeak),
                pair("&&", TokenTypes.LogicAnd)
        };
        for (Pair<String, Integer> operator : andOperators) {
            //false x bool -> false
            addToBinaryOperators(operator, std.falseTypeSymbol, std.boolTypeSymbol, std.falseTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //false x ~{as bool} -> false

            //bool x false -> false
            addToBinaryOperators(operator, std.boolTypeSymbol, std.falseTypeSymbol, std.falseTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{bool} x false -> false

            //true x true -> true
            addToBinaryOperators(operator, std.trueTypeSymbol, std.trueTypeSymbol, std.trueTypeSymbol);
            //bool x bool -> bool
            addToBinaryOperators(operator, std.boolTypeSymbol, std.boolTypeSymbol, std.boolTypeSymbol);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool
        }

        Pair<String, Integer> logicNot = new Pair<>("!", TokenTypes.LogicNot);
        addToUnaryOperators(logicNot, std.falseTypeSymbol, std.trueTypeSymbol);
        addToUnaryOperators(logicNot, std.trueTypeSymbol, std.falseTypeSymbol);
        addToUnaryOperators(logicNot, std.boolTypeSymbol, std.boolTypeSymbol);
    }

    private void defineAssignmentOperators() {
        IFunctionType function;

        //Tlhs x Trhs -> Tlhs \ Tlhs > Trhs
        IVariable lhs = std.variableTLhs;
        IVariable rhs = std.variableTRhs;
        IVariable rtn = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, T_LHS);
        IOverloadBindings collection = createBindings(lhs, rhs, rtn);
        collection.addLowerRefBound(T_LHS, new TypeVariableConstraint(T_RHS));
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
            addToBinaryOperators(operator, std.intTypeSymbol, std.intTypeSymbol, std.intTypeSymbol);
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
            addToBinaryOperators(operator, std.intTypeSymbol, std.intTypeSymbol, std.intTypeSymbol);
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
                    operator, std.stringTypeSymbol, std.stringTypeSymbol, std.stringTypeSymbol);
        }

        Pair<String, Integer> bitwiseNot = pair("~", TokenTypes.BitwiseNot);
        //int -> int
        addToUnaryOperators(bitwiseNot, std.intTypeSymbol, std.intTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} -> int

        //string -> string
        addToUnaryOperators(bitwiseNot, std.stringTypeSymbol, std.stringTypeSymbol);
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
            addToBinaryOperators(operator, std.mixedTypeSymbol, std.mixedTypeSymbol, std.boolTypeSymbol);
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
        IVariable rtn = std.variableTReturn;
        IOverloadBindings collection = createBindings(conditionVariable, ifVariable, elseVariable, rtn);
        collection.addUpperTypeBound(tCondition, std.falseTypeSymbol);
        conditionVariable.setHasFixedType();
        collection.addUpperTypeBound(tIf, std.mixedTypeSymbol);
        ifVariable.setHasFixedType();
        collection.addLowerRefBound(T_RETURN, new TypeVariableConstraint(tElse));
        IFunctionType function = symbolFactory.createFunctionType(
                "?", collection, Arrays.asList(conditionVariable, ifVariable, elseVariable), rtn);
        addToOperators(TokenTypes.QuestionMark, function);


        //true x Tif x mixed -> Tif
        //expanded: true x Tif x mixed -> Treturn \ Treturn > Tif
        conditionVariable = symbolFactory.createVariable("$condition", tCondition);
        ifVariable = symbolFactory.createVariable("$if", tIf);
        elseVariable = symbolFactory.createVariable("$else", tElse);
        rtn = std.variableTReturn;
        collection = createBindings(conditionVariable, ifVariable, elseVariable, rtn);
        collection.addUpperTypeBound(tCondition, std.trueTypeSymbol);
        conditionVariable.setHasFixedType();
        collection.addUpperTypeBound(tElse, std.mixedTypeSymbol);
        elseVariable.setHasFixedType();
        collection.addLowerRefBound(T_RETURN, new TypeVariableConstraint(tIf));
        function = symbolFactory.createFunctionType(
                "?", collection, Arrays.asList(conditionVariable, ifVariable, elseVariable), rtn);
        addToOperators(TokenTypes.QuestionMark, function);

        //bool x Tif x Telse -> (Tif | Telse)
        //expanded: bool x Tif x Telse -> Treturn \ Treturn > Tif, Treturn > Telse
        conditionVariable = symbolFactory.createVariable("$condition", tCondition);
        ifVariable = symbolFactory.createVariable("$if", tIf);
        elseVariable = symbolFactory.createVariable("$else", tElse);
        rtn = std.variableTReturn;
        collection = createBindings(conditionVariable, ifVariable, elseVariable, rtn);
        collection.addUpperTypeBound(tCondition, std.boolTypeSymbol);
        conditionVariable.setHasFixedType();
        collection.addLowerRefBound(T_RETURN, new TypeVariableConstraint(tIf));
        collection.addLowerRefBound(T_RETURN, new TypeVariableConstraint(tElse));
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
            //T x T -> T \ T < num
            IVariable lhs = std.tLhs;
            IVariable rhs = std.tRhs;
            IVariable rtn = std.tReturn;
            IOverloadBindings collection = createBindings(lhs, rhs, rtn);
            collection.addUpperTypeBound("T", std.numTypeSymbol);
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
            addToBinaryOperators(operator, std.boolTypeSymbol, std.boolTypeSymbol, std.intTypeSymbol);

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
                std.arrayTypeSymbol, std.arrayTypeSymbol, std.arrayTypeSymbol);
        addToBinaryOperators(pair("+=", TokenTypes.PlusAssign),
                std.arrayTypeSymbol, std.arrayTypeSymbol, std.arrayTypeSymbol);

        createDivOperators();
        createModuloOperators();
        createUnaryArithmeticOperators();
    }

    private void createDivOperators() {
        IFunctionType function;

        //bool x bool -> (int | false)
        addToBinaryOperators(pair("/", TokenTypes.Divide), std.boolTypeSymbol, std.boolTypeSymbol, std.intOrFalse);


        //T x T -> (T | false) \ float < T < num
        //expanded: T x T -> Treturn \ float < T < num, Treturn > T, Treturn > false
        IVariable lhs = std.tLhs;
        IVariable rhs = std.tRhs;
        IVariable rtn = std.variableTReturn;
        IOverloadBindings collection = createBindings(lhs, rhs, rtn);
        collection.addLowerTypeBound("T", std.floatTypeSymbol);
        collection.addUpperTypeBound("T", std.numTypeSymbol);
        collection.addLowerRefBound(T_RETURN, new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(T_RETURN, std.falseTypeSymbol);
        function = symbolFactory.createFunctionType("/", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.Divide, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //float x ~{as float} -> (float | false)
        //~{as float} x float -> (float | false)
        //~{as num} x ~{as num} -> (num | false)


        //(int | bool) x bool -> (int | false)
        IUnionTypeSymbol intOrBool = symbolFactory.createUnionTypeSymbol();
        intOrBool.addTypeSymbol(std.intTypeSymbol);
        intOrBool.addTypeSymbol(std.boolTypeSymbol);
        addToBinaryOperators(pair("/=", TokenTypes.DivideAssign), intOrBool, std.boolTypeSymbol, std.intOrFalse);

        //(T | false) x T -> (T | false) \ float < T < num
        //expanded: Tlhs x T -> Treturn \ float < T < num, Tlhs > T, Tlhs > false
        lhs = std.tLhs;
        rhs = std.tRhs;
        rtn = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, T_LHS);
        collection = createBindings(lhs, rhs, rtn);
        collection.addLowerTypeBound("T", std.floatTypeSymbol);
        collection.addUpperTypeBound("T", std.numTypeSymbol);
        collection.addLowerRefBound(T_LHS, new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(T_LHS, std.falseTypeSymbol);
        function = symbolFactory.createFunctionType("/=", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.DivideAssign, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(T | ~{as T}) x ~{as T} -> (T | false) \ float < T < num
    }

    private void createModuloOperators() {
        //int x int -> (int | false)
        addToBinaryOperators(pair("%", TokenTypes.Modulo), std.intTypeSymbol, std.intTypeSymbol, std.intOrFalse);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} x ~{as int} -> (int | false)

        //(int | false) x int -> (int | false)
        addToBinaryOperators(pair("%=", TokenTypes.ModuloAssign), std.intOrFalse, std.intTypeSymbol, std.intOrFalse);

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

        IUnionTypeSymbol numOrBool = symbolFactory.createUnionTypeSymbol();
        numOrBool.addTypeSymbol(std.numTypeSymbol);
        numOrBool.addTypeSymbol(std.boolTypeSymbol);

        for (Pair<String, Integer> operator : incrDecrOperators) {
            //T -> T \ T < (num | bool)
            IVariable expr = std.tExpr;
            IVariable rtn = std.tReturn;
            IOverloadBindings collection = createBindings(expr, rtn);
            collection.addUpperTypeBound("T", numOrBool);
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, Arrays.asList(expr), rtn);
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(T | ~T) -> T \ T < num
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] unaryPlusMinusOperators = new Pair[]{
                pair("-", TokenTypes.UNARY_MINUS),
                pair("+", TokenTypes.UNARY_PLUS)
        };
        for (Pair<String, Integer> operator : unaryPlusMinusOperators) {
            //bool x int
            addToUnaryOperators(operator, std.boolTypeSymbol, std.intTypeSymbol);
            //T -> T \ T < num
            IVariable expr = std.tExpr;
            IVariable rtn = std.tReturn;
            IOverloadBindings collection = createBindings(expr, rtn);
            collection.addUpperTypeBound("T", std.numTypeSymbol);
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, Arrays.asList(expr), rtn);
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as num} -> num
        }
    }

    private void defineDotOperator() {
        addToBinaryOperators(
                pair(".", TokenTypes.Dot), std.stringTypeSymbol, std.stringTypeSymbol, std.stringTypeSymbol);
        addToBinaryOperators(
                pair(".=", TokenTypes.DotAssign), std.stringTypeSymbol, std.stringTypeSymbol, std.stringTypeSymbol);

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
        addToBinaryOperators(instanceOf, std.mixedTypeSymbol, std.mixedTypeSymbol, std.boolTypeSymbol);
    }

    private void defineCloneAndNewOperator() {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: T -> T \ T < object
        //T -> T
        IVariable expr = std.tExpr;
        IVariable rtn = std.tReturn;
        IOverloadBindings collection = createBindings(expr, rtn);
        IFunctionType function = symbolFactory.createFunctionType("clone", collection, Arrays.asList(expr), rtn);
        addToOperators(TokenTypes.Clone, function);

        //TODO TINS-349 structural constraints
        //not all classes return itself, some return null as well in error cases
        //see https://wiki.php.net/rfc/internal_constructor_behaviour
        //T -> T
        expr = std.tExpr;
        rtn = std.tReturn;
        collection = createBindings(expr, rtn);
        function = symbolFactory.createFunctionType("new", collection, Arrays.asList(expr), rtn);
        addToOperators(TokenTypes.New, function);
    }

    private void defineAtAndCastOperator() {
        //T -> T
        IVariable expr = std.tExpr;
        IVariable rtn = std.tReturn;
        IOverloadBindings collection = createBindings(expr, rtn);
        IFunctionType function = symbolFactory.createFunctionType("@", collection, Arrays.asList(expr), rtn);
        addToOperators(TokenTypes.At, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //T1 x T2 -> T1 \ T2 < ~{as T1}
        //simplified version for now: T1 x T2 -> T1 or expanded: Tlhs x Trhs -> Treturn \ Treturn > Tlhs
        IVariable lhs = std.variableTLhs;
        IVariable rhs = std.variableTRhs;
        rtn = std.variableTReturn;
        collection = createBindings(lhs, rhs, rtn);
        collection.addLowerRefBound(T_RETURN, new TypeVariableConstraint(T_LHS));
        function = symbolFactory.createFunctionType("cast", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.CAST, function);
    }

    private void defineControlFlowOperators() {
        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("if", TokenTypes.If), std.boolTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} -> void

        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("while", TokenTypes.While), std.boolTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} -> void

        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("do", TokenTypes.Do), std.boolTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} -> void

        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("for", TokenTypes.For), std.boolTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} -> void


        //key and value are switched
        //array x mixed x (int|string) -> mixed
        IUnionTypeSymbol intOrString = symbolFactory.createUnionTypeSymbol();
        intOrString.addTypeSymbol(std.intTypeSymbol);
        intOrString.addTypeSymbol(std.stringTypeSymbol);

        IVariable arr = symbolFactory.createVariable("$arr", "Tarr");
        arr.setHasFixedType();
        IVariable value = symbolFactory.createVariable("$value", "Tvalue");
        value.setHasFixedType();
        IVariable key = symbolFactory.createVariable("$key", "Tkey");
        key.setHasFixedType();
        IVariable rtn = std.fixTReturn;
        rtn.setHasFixedType();
        IOverloadBindings collection = createBindings(arr, value, key, rtn);
        collection.addLowerTypeBound("Tarr", std.arrayTypeSymbol);
        collection.addUpperTypeBound("Tarr", std.arrayTypeSymbol);
        collection.addLowerTypeBound("Tkey", intOrString);
        collection.addUpperTypeBound("Tkey", intOrString);
        collection.addLowerTypeBound("Tvalue", std.mixedTypeSymbol);
        collection.addUpperTypeBound("Tvalue", std.mixedTypeSymbol);
        IFunctionType function
                = symbolFactory.createFunctionType("foreach", collection, Arrays.asList(arr, value, key), rtn);
        addToOperators(TokenTypes.Foreach, function);

        //TODO rstoll TINS-391 - Introduce void as own type
        //scalar -> mixed
        addToUnaryOperators(pair("switch", TokenTypes.Switch), std.scalarTypeSymbol, std.mixedTypeSymbol);

        //Tlhs x Trhs -> Trhs \ Trhs > Tlhs
        IVariable lhs = std.variableTLhs;
        IVariable rhs = std.variableTRhs;
        rtn = std.variableTReturn;
        collection = createBindings(lhs, rhs, rtn);
        collection.addLowerRefBound(T_RHS, new TypeVariableConstraint(T_LHS));
        function = symbolFactory.createFunctionType("catch", collection, Arrays.asList(lhs, rhs), rtn);
        addToOperators(TokenTypes.Catch, function);
    }

    private void defineGlobalFunctions() {
        //TODO rstoll TINS-391 - Introduce void as own type
        //string -> mixed
        addToUnaryOperators(pair("echo", TokenTypes.Echo), std.stringTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as string} -> void

        //TODO rstoll TINS-391 - Introduce void as own type
        //int -> mixed
        addToUnaryOperators(pair("exit", TokenTypes.Exit), std.intTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-391 - Introduce void as own type
        //string -> mixed
        addToUnaryOperators(pair("exit", TokenTypes.Exit), std.stringTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as string} -> void

        //TODO rstoll TINS-394 introduce nothing as own type
        // Exception -> mixed
        ITypeSymbol exception = (ITypeSymbol) builtInSymbols.get("\\Exception");
        addToUnaryOperators(pair("throw", TokenTypes.Throw), exception, std.mixedTypeSymbol);
    }

    private void addToBinaryOperators(
            Pair<String, Integer> operator,
            ITypeSymbol leftBound,
            ITypeSymbol rightBound,
            ITypeSymbol returnBound) {

        IOverloadBindings collection = createBindings(std.fixTLhs, std.fixTRhs, std.fixTReturn);
        collection.addUpperTypeBound(T_LHS, leftBound);
        collection.addUpperTypeBound(T_RHS, rightBound);
        collection.addLowerTypeBound(T_RETURN, returnBound);

        IFunctionType function = symbolFactory.createFunctionType(
                operator.first, collection, std.fixBinaryParameterIds, std.fixTReturn);

        addToOperators(operator.second, function);
    }

    private void addToUnaryOperators(
            Pair<String, Integer> operator, ITypeSymbol formalBound, ITypeSymbol returnBound) {

        IOverloadBindings collection = createBindings(std.fixTExpr, std.fixTReturn);
        collection.addUpperTypeBound(T_EXPR, formalBound);
        collection.addLowerTypeBound(T_RETURN, returnBound);

        IFunctionType function = symbolFactory.createFunctionType(
                operator.first, collection, std.fixUnaryParameterId, std.fixTReturn);

        addToOperators(operator.second, function);
    }

    private void addToOperators(int operatorType, IFunctionType functionTypeSymbol) {
        IMinimalMethodSymbol overloadSymbol = builtInOperators.get(operatorType);
        overloadSymbol.addOverload(functionTypeSymbol);
    }

}

