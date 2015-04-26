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
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_EXPR;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RETURN;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_RHS;
import static ch.tsphp.tinsphp.symbols.TypeVariableNames.RETURN_VARIABLE_NAME;

@SuppressWarnings("checkstyle:todocomment")
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
        IOverloadBindings collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(VAR_LHS, new TypeVariableReference(T_LHS));
        collection.addVariable(VAR_RHS, new TypeVariableReference(T_RHS));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(T_LHS));
        collection.addLowerRefBound(T_LHS, new TypeVariableReference(T_RHS));
        function = symbolFactory.createFunctionType("=", collection, std.binaryParameterIds);
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
        final String varCondition = "$condition";
        final String varIf = "$if";
        final String varElse = "$else";
        final String tCondition = "Tcondition";
        final String tIf = "Tif";
        final String tElse = "Telse";
        IVariable conditionVariable = symbolFactory.createVariable(varCondition);
        IVariable ifVariable = symbolFactory.createVariable(varIf);
        IVariable elseVariable = symbolFactory.createVariable(varElse);
        List<IVariable> parameters = Arrays.asList(conditionVariable, ifVariable, elseVariable);

        //false x mixed x Telse -> Telse
        //expanded: false x mixed x Telse -> Treturn \ Treturn > Telse
        IOverloadBindings collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(varCondition, fixReference(tCondition));
        collection.addVariable(varIf, fixReference(tIf));
        collection.addVariable(varElse, reference(tElse));
        collection.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        collection.addUpperTypeBound(tCondition, std.falseTypeSymbol);
        collection.addUpperTypeBound(tIf, std.mixedTypeSymbol);
        collection.addLowerRefBound(T_RETURN, new TypeVariableReference(tElse));

        IFunctionType function = symbolFactory.createFunctionType("?", collection, parameters);
        addToOperators(TokenTypes.QuestionMark, function);


        //true x Tif x mixed -> Tif
        //expanded: true x Tif x mixed -> Treturn \ Treturn > Tif
        collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(varCondition, fixReference(tCondition));
        collection.addVariable(varIf, reference(tIf));
        collection.addVariable(varElse, fixReference(tElse));
        collection.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        collection.addUpperTypeBound(tCondition, std.trueTypeSymbol);
        collection.addUpperTypeBound(tElse, std.mixedTypeSymbol);
        collection.addLowerRefBound(T_RETURN, new TypeVariableReference(tIf));

        function = symbolFactory.createFunctionType("?", collection, parameters);
        addToOperators(TokenTypes.QuestionMark, function);

        //bool x Tif x Telse -> (Tif | Telse)
        //expanded: bool x Tif x Telse -> Treturn \ Treturn > Tif, Treturn > Telse
        collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(varCondition, fixReference(tCondition));
        collection.addVariable(varIf, reference(tIf));
        collection.addVariable(varElse, reference(tElse));
        collection.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        collection.addUpperTypeBound(tCondition, std.boolTypeSymbol);
        collection.addLowerRefBound(T_RETURN, new TypeVariableReference(tIf));
        collection.addLowerRefBound(T_RETURN, new TypeVariableReference(tElse));
        function = symbolFactory.createFunctionType("?", collection, parameters);
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
            IOverloadBindings collection = createBinaryTBindings();
            collection.addUpperTypeBound("T", std.numTypeSymbol);
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, std.binaryParameterIds);
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
        IOverloadBindings collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(VAR_LHS, reference("T"));
        collection.addVariable(VAR_RHS, reference("T"));
        collection.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        collection.addLowerTypeBound("T", std.floatTypeSymbol);
        collection.addUpperTypeBound("T", std.numTypeSymbol);
        collection.addLowerRefBound(T_RETURN, new TypeVariableReference("T"));
        collection.addLowerTypeBound(T_RETURN, std.falseTypeSymbol);

        function = symbolFactory.createFunctionType("/", collection, std.binaryParameterIds);
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
        //expanded: Tlhs x Trhs -> Tlhs \  (float | Trhs) < Tlhs, float < Trhs < num

        collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(VAR_LHS, new TypeVariableReference(T_LHS));
        collection.addVariable(VAR_RHS, new TypeVariableReference(T_RHS));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(T_LHS));

        collection.addLowerTypeBound(T_RHS, std.floatTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.numTypeSymbol);
        collection.addLowerRefBound(T_LHS, new TypeVariableReference(T_RHS));
        collection.addLowerTypeBound(T_LHS, std.falseTypeSymbol);

        function = symbolFactory.createFunctionType("/=", collection, std.binaryParameterIds);
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

            IOverloadBindings collection = createUnaryTBindings();

            collection.addUpperTypeBound("T", numOrBool);
            IFunctionType function = symbolFactory.createFunctionType(
                    operator.first, collection, std.unaryParameterId);
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
            IOverloadBindings collection = createUnaryTBindings();
            collection.addUpperTypeBound("T", std.numTypeSymbol);
            IFunctionType function = symbolFactory.createFunctionType(operator.first, collection, std.unaryParameterId);
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
        IOverloadBindings collection = createUnaryTBindings();
        IFunctionType function = symbolFactory.createFunctionType("clone", collection, std.unaryParameterId);
        addToOperators(TokenTypes.Clone, function);

        //TODO TINS-349 structural constraints
        //not all classes return itself, some return null as well in error cases
        //see https://wiki.php.net/rfc/internal_constructor_behaviour
        //T -> T
        collection = createUnaryTBindings();
        function = symbolFactory.createFunctionType("new", collection, std.unaryParameterId);
        addToOperators(TokenTypes.New, function);
    }

    private void defineAtAndCastOperator() {
        //T -> T
        IOverloadBindings collection = createUnaryTBindings();
        IFunctionType function = symbolFactory.createFunctionType("@", collection, std.unaryParameterId);
        addToOperators(TokenTypes.At, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //T1 x T2 -> T1 \ T2 < ~{as T1}
        //simplified version for now: T1 x T2 -> T1 or expanded: Tlhs x Trhs -> Treturn \ Treturn > Tlhs
        collection = createVariableBinaryBindings();
        collection.addLowerRefBound(T_RETURN, new TypeVariableReference(T_LHS));
        function = symbolFactory.createFunctionType("cast", collection, std.binaryParameterIds);
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

        String varArr = "$arr";
        String varValue = "$value";
        String varKey = "$key";
        String tArr = "Tarr";
        String tValue = "Tvalue";
        String tKey = "Tkey";
        IVariable arr = symbolFactory.createVariable(varArr);
        IVariable value = symbolFactory.createVariable(varValue);
        IVariable key = symbolFactory.createVariable(varKey);

        IOverloadBindings collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(varArr, fixReference(tArr));
        collection.addVariable(varValue, fixReference(tValue));
        collection.addVariable(varKey, fixReference(tKey));
        collection.addVariable(RETURN_VARIABLE_NAME, fixReference(T_RETURN));

        collection.addLowerTypeBound(tArr, std.arrayTypeSymbol);
        collection.addUpperTypeBound(tArr, std.arrayTypeSymbol);
        collection.addLowerTypeBound(tValue, std.mixedTypeSymbol);
        collection.addUpperTypeBound(tValue, std.mixedTypeSymbol);
        collection.addLowerTypeBound(tKey, intOrString);
        collection.addUpperTypeBound(tKey, intOrString);
        collection.addLowerTypeBound(T_RETURN, std.mixedTypeSymbol);
        collection.addUpperTypeBound(T_RETURN, std.mixedTypeSymbol);
        IFunctionType function
                = symbolFactory.createFunctionType("foreach", collection, Arrays.asList(arr, value, key));
        addToOperators(TokenTypes.Foreach, function);

        //TODO rstoll TINS-391 - Introduce void as own type
        //scalar -> mixed
        addToUnaryOperators(pair("switch", TokenTypes.Switch), std.scalarTypeSymbol, std.mixedTypeSymbol);

        //TODO rstoll TINS-394 introduce nothing as own type
        // Exception -> mixed
        ITypeSymbol exception = (ITypeSymbol) builtInSymbols.get("\\Exception");
        addToUnaryOperators(pair("throw", TokenTypes.Throw), exception, std.mixedTypeSymbol);

        //Tlhs x Trhs -> Trhs \ Trhs > Tlhs
        collection = new OverloadBindings(symbolFactory, overloadResolver);
        collection.addVariable(VAR_LHS, reference(T_LHS));
        collection.addVariable(VAR_RHS, reference(T_RHS));
        collection.addVariable(RETURN_VARIABLE_NAME, reference(T_RHS));

        collection.addLowerRefBound(T_RHS, new TypeVariableReference(T_LHS));
        function = symbolFactory.createFunctionType("catch", collection, std.binaryParameterIds);
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
    }

    private void addToBinaryOperators(
            Pair<String, Integer> operator,
            ITypeSymbol leftBound,
            ITypeSymbol rightBound,
            ITypeSymbol returnBound) {

        IOverloadBindings collection = createFixBinaryBindings();
        collection.addUpperTypeBound(T_LHS, leftBound);
        collection.addUpperTypeBound(T_RHS, rightBound);
        collection.addLowerTypeBound(T_RETURN, returnBound);

        IFunctionType function = symbolFactory.createFunctionType(operator.first, collection, std.binaryParameterIds);
        addToOperators(operator.second, function);
    }

    private void addToUnaryOperators(
            Pair<String, Integer> operator, ITypeSymbol formalBound, ITypeSymbol returnBound) {

        IOverloadBindings collection = createFixUnaryBindings();
        collection.addUpperTypeBound(T_EXPR, formalBound);
        collection.addLowerTypeBound(T_RETURN, returnBound);

        IFunctionType function = symbolFactory.createFunctionType(operator.first, collection, std.unaryParameterId);
        addToOperators(operator.second, function);
    }

    private void addToOperators(int operatorType, IFunctionType functionTypeSymbol) {
        IMinimalMethodSymbol methodSymbol = builtInOperators.get(operatorType);
        functionTypeSymbol.fix();
        methodSymbol.addOverload(functionTypeSymbol);
    }

}

