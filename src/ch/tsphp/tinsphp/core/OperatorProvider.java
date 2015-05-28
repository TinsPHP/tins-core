/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.TinsPHPConstants.RETURN_VARIABLE_NAME;
import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_EXPR;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RETURN;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_RHS;

@SuppressWarnings("checkstyle:todocomment")
public class OperatorProvider extends AProvider implements IOperatorsProvider
{

    private Map<Integer, IMinimalMethodSymbol> builtInOperators;
    private final Map<String, ISymbol> builtInSymbols;

    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            ITypeHelper theTypeHelper,
            StandardConstraintAndVariables standardConstraintAndVariables,
            Map<String, ISymbol> theBuiltInSymbols) {
        super(theSymbolFactory, theTypeHelper, standardConstraintAndVariables);
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
            //true x {as bool} -> true
            addToBinaryOperators(operator, std.trueTypeSymbol, std.asBoolTypeSymbol, std.trueTypeSymbol);

            //bool x true -> true
            addToBinaryOperators(operator, std.boolTypeSymbol, std.trueTypeSymbol, std.trueTypeSymbol);
            //{as bool} x true -> true
            addToBinaryOperators(operator, std.asBoolTypeSymbol, std.trueTypeSymbol, std.trueTypeSymbol);

            //bool x bool -> bool
            addToBinaryOperators(operator, std.boolTypeSymbol, std.boolTypeSymbol, std.boolTypeSymbol);
            //{as bool} x {as bool} -> bool
            addToBinaryOperators(operator, std.asBoolTypeSymbol, std.asBoolTypeSymbol, std.boolTypeSymbol);
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
        //{as bool} x {as bool} -> bool
        addToBinaryOperators(xorWeak, std.asBoolTypeSymbol, std.asBoolTypeSymbol, std.boolTypeSymbol);

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] andOperators = new Pair[]{
                pair("and", TokenTypes.LogicAndWeak),
                pair("&&", TokenTypes.LogicAnd)
        };
        for (Pair<String, Integer> operator : andOperators) {
            //false x bool -> false
            addToBinaryOperators(operator, std.falseTypeSymbol, std.boolTypeSymbol, std.falseTypeSymbol);
            //false x {as bool} -> false
            addToBinaryOperators(operator, std.falseTypeSymbol, std.asBoolTypeSymbol, std.falseTypeSymbol);

            //bool x false -> false
            addToBinaryOperators(operator, std.boolTypeSymbol, std.falseTypeSymbol, std.falseTypeSymbol);
            //{as bool} x false -> false
            addToBinaryOperators(operator, std.asBoolTypeSymbol, std.falseTypeSymbol, std.falseTypeSymbol);

            //true x true -> true
            addToBinaryOperators(operator, std.trueTypeSymbol, std.trueTypeSymbol, std.trueTypeSymbol);
            //bool x bool -> bool
            addToBinaryOperators(operator, std.boolTypeSymbol, std.boolTypeSymbol, std.boolTypeSymbol);
            //{as bool} x {as bool} -> bool
            addToBinaryOperators(operator, std.asBoolTypeSymbol, std.asBoolTypeSymbol, std.boolTypeSymbol);
        }

        Pair<String, Integer> logicNot = new Pair<>("!", TokenTypes.LogicNot);
        addToUnaryOperators(logicNot, std.falseTypeSymbol, std.trueTypeSymbol);
        addToUnaryOperators(logicNot, std.trueTypeSymbol, std.falseTypeSymbol);
        addToUnaryOperators(logicNot, std.boolTypeSymbol, std.boolTypeSymbol);
    }

    private void defineAssignmentOperators() {
        IFunctionType function;

        //Tlhs x Trhs -> Tlhs \ Trhs <: Tlhs
        IOverloadBindings overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerRefBound(T_LHS, new TypeVariableReference(T_RHS));
        function = symbolFactory.createFunctionType("=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.Assign, function);

        //Other assignment operators can be found in the corresponding sections.
        //For instance, += is in createAssignmentOperators
    }

    private void defineBitLevelOperators() {
        createBitLevelNonAssignOperators();
        createBitLevelAssignOperators();
    }

    private void createBitLevelNonAssignOperators() {
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
            //{as int} x {as int} -> int
            addToBinaryOperators(operator, std.asIntTypeSymbol, std.asIntTypeSymbol, std.intTypeSymbol);
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] stringResultingOperators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd)
        };
        for (Pair<String, Integer> operator : stringResultingOperators) {
            //string x string -> string
            addToBinaryOperators(operator, std.stringTypeSymbol, std.stringTypeSymbol, std.stringTypeSymbol);
        }

        Pair<String, Integer> bitwiseNot = pair("~", TokenTypes.BitwiseNot);
        //int -> int
        addToUnaryOperators(bitwiseNot, std.intTypeSymbol, std.intTypeSymbol);
        //float -> int
        addToUnaryOperators(bitwiseNot, std.floatTypeSymbol, std.intTypeSymbol);
        //string -> string
        addToUnaryOperators(bitwiseNot, std.stringTypeSymbol, std.stringTypeSymbol);
    }

    private void createBitLevelAssignOperators() {
        IFunctionType function;

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] intResultingAssignOperators = new Pair[]{
                pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("&=", TokenTypes.BitwiseAndAssign),
                pair("<<=", TokenTypes.ShiftLeftAssign),
                pair(">>=", TokenTypes.ShiftRightAssign),
        };
        for (Pair<String, Integer> operator : intResultingAssignOperators) {
            //Tlhs x int -> Tlhs \ int <: Tlhs <: int
            IOverloadBindings overloadBindings = createAssignOverloadBindings();
            overloadBindings.addLowerTypeBound(T_LHS, std.intTypeSymbol);
            overloadBindings.addUpperTypeBound(T_LHS, std.intTypeSymbol);
            overloadBindings.addUpperTypeBound(T_RHS, std.intTypeSymbol);
            overloadBindings.fixType(VAR_RHS);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

            //Tlhs x {as int} -> Tlhs \ int <: Tlhs <: {as int}
            overloadBindings = createAssignOverloadBindings();
            overloadBindings.addLowerTypeBound(T_LHS, std.intTypeSymbol);
            overloadBindings.addUpperTypeBound(T_LHS, std.asIntTypeSymbol);
            overloadBindings.addUpperTypeBound(T_RHS, std.asIntTypeSymbol);
            overloadBindings.fixType(VAR_RHS);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] stringResultingAssignOperators = new Pair[]{
                pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("&=", TokenTypes.BitwiseAndAssign)
        };
        for (Pair<String, Integer> operator : stringResultingAssignOperators) {
            //Tlhs x string -> Tlhs \ string <: Tlhs <: string
            IOverloadBindings overloadBindings = createAssignOverloadBindings();
            overloadBindings.addLowerTypeBound(T_LHS, std.stringTypeSymbol);
            overloadBindings.addUpperTypeBound(T_LHS, std.stringTypeSymbol);
            overloadBindings.addUpperTypeBound(T_RHS, std.stringTypeSymbol);
            overloadBindings.fixType(VAR_RHS);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);
        }
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
        //expanded: false x mixed x Telse -> Treturn \ Treturn :> Telse
        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(varCondition, fixReference(tCondition));
        overloadBindings.addVariable(varIf, fixReference(tIf));
        overloadBindings.addVariable(varElse, reference(tElse));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        overloadBindings.addUpperTypeBound(tCondition, std.falseTypeSymbol);
        overloadBindings.addUpperTypeBound(tIf, std.mixedTypeSymbol);
        overloadBindings.addLowerRefBound(T_RETURN, reference(tElse));

        IFunctionType function = symbolFactory.createFunctionType("?", overloadBindings, parameters);
        addToOperators(TokenTypes.QuestionMark, function);


        //true x Tif x mixed -> Tif
        //expanded: true x Tif x mixed -> Treturn \ Treturn :> Tif
        overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(varCondition, fixReference(tCondition));
        overloadBindings.addVariable(varIf, reference(tIf));
        overloadBindings.addVariable(varElse, fixReference(tElse));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        overloadBindings.addUpperTypeBound(tCondition, std.trueTypeSymbol);
        overloadBindings.addUpperTypeBound(tElse, std.mixedTypeSymbol);
        overloadBindings.addLowerRefBound(T_RETURN, reference(tIf));

        function = symbolFactory.createFunctionType("?", overloadBindings, parameters);
        addToOperators(TokenTypes.QuestionMark, function);


        //bool x Tif x Telse -> (Tif | Telse)
        //expanded: bool x Tif x Telse -> Treturn \ Treturn :> Tif, Treturn :> Telse
        overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(varCondition, fixReference(tCondition));
        overloadBindings.addVariable(varIf, reference(tIf));
        overloadBindings.addVariable(varElse, reference(tElse));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        overloadBindings.addUpperTypeBound(tCondition, std.boolTypeSymbol);
        overloadBindings.addLowerRefBound(T_RETURN, reference(tIf));
        overloadBindings.addLowerRefBound(T_RETURN, reference(tElse));

        function = symbolFactory.createFunctionType("?", overloadBindings, parameters);
        addToOperators(TokenTypes.QuestionMark, function);


        //{as bool} x Tif x Telse -> (Tif | Telse)
        //expanded: {as bool} x Tif x Telse -> Treturn \ Treturn :> Tif, Treturn :> Telse
        overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(varCondition, fixReference(tCondition));
        overloadBindings.addVariable(varIf, reference(tIf));
        overloadBindings.addVariable(varElse, reference(tElse));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));

        overloadBindings.addUpperTypeBound(tCondition, std.asBoolTypeSymbol);
        overloadBindings.addLowerRefBound(T_RETURN, reference(tIf));
        overloadBindings.addLowerRefBound(T_RETURN, reference(tElse));

        function = symbolFactory.createFunctionType("?", overloadBindings, parameters);
        addToOperators(TokenTypes.QuestionMark, function);
    }

    private void defineArithmeticOperators() {
        createArithmeticNonAssignOperators();
        createArithmeticAssignOperators();

        createModuloOperators();
        createUnaryArithmeticOperators();
    }

    private void createArithmeticNonAssignOperators() {
        IFunctionType function;
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] nonAssignOperators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply)
        };

        for (Pair<String, Integer> operator : nonAssignOperators) {
            //int x int -> int
            addToBinaryOperators(operator, std.intTypeSymbol, std.intTypeSymbol, std.intTypeSymbol);
            //float x float -> float
            addToBinaryOperators(operator, std.floatTypeSymbol, std.floatTypeSymbol, std.floatTypeSymbol);

            //T x {as T} -> T \ T <: num
            //expanded: T1 x T2 -> T1 \ T1 <: num, T2 <: {as T1}
            IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
            overloadBindings.addVariable(VAR_LHS, reference("T1"));
            overloadBindings.addVariable(VAR_RHS, reference("T2"));
            overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference("T1"));
            //bind convertible type to Tlhs
            IConvertibleTypeSymbol asT1 = symbolFactory.createConvertibleTypeSymbol();
            asT1.setOverloadBindings(overloadBindings, "T1");
            overloadBindings.addUpperTypeBound("T1", std.numTypeSymbol);
            overloadBindings.addUpperTypeBound("T2", asT1);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

            //{as T} x T -> T \ T <: num
            //expanded: {as T2} x T2 -> T2 \ T1 <: {as T2}, T2 <: num
            overloadBindings = symbolFactory.createOverloadBindings();
            overloadBindings.addVariable(VAR_LHS, reference("T1"));
            overloadBindings.addVariable(VAR_RHS, reference("T2"));
            overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference("T2"));
            //bind convertible type to Tlhs
            IConvertibleTypeSymbol asT2 = symbolFactory.createConvertibleTypeSymbol();
            asT2.setOverloadBindings(overloadBindings, "T2");
            overloadBindings.addUpperTypeBound("T1", asT2);
            overloadBindings.addUpperTypeBound("T2", std.numTypeSymbol);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

            //{as T} x {as T} -> T \ T <: num
            //expanded: T1 x T1 -> T2 \ T1 <: {as T2}, T2 <: num
            overloadBindings = symbolFactory.createOverloadBindings();
            overloadBindings.addVariable(VAR_LHS, reference("T1"));
            overloadBindings.addVariable(VAR_RHS, reference("T1"));
            overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference("T2"));
            //bind convertible type to T2
            asT2 = symbolFactory.createConvertibleTypeSymbol();
            asT2.setOverloadBindings(overloadBindings, "T2");
            overloadBindings.addUpperTypeBound("T1", asT2);
            overloadBindings.addUpperTypeBound("T2", std.numTypeSymbol);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);
        }

        //array x array -> array
        addToBinaryOperators(pair("+", TokenTypes.Plus), std.arrayTypeSymbol, std.arrayTypeSymbol, std.arrayTypeSymbol);

        createDivOperator();
    }

    private void createDivOperator() {
        //float x float -> (float | falseType)
        addToBinaryOperators(pair("/", TokenTypes.Divide), std.floatTypeSymbol, std.floatTypeSymbol, std.floatOrFalse);

        //float x {as float} -> (float | falseType)
        addToBinaryOperators(pair("/", TokenTypes.Divide), std.floatTypeSymbol, std.asFloatTypeSymbol,
                std.floatOrFalse);

        //{as float} x float -> (float | falseType)
        addToBinaryOperators(pair("/", TokenTypes.Divide), std.asFloatTypeSymbol, std.floatTypeSymbol,
                std.floatOrFalse);

        //{as num} x {as num} -> (num | falseType)
        addToBinaryOperators(pair("/", TokenTypes.Divide), std.asNumTypeSymbol, std.asNumTypeSymbol, std.numOrFalse);
    }

    private void createArithmeticAssignOperators() {
        IFunctionType function;
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] assignOperators = new Pair[]{
                pair("+=", TokenTypes.PlusAssign),
                pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign),
        };
        for (Pair<String, Integer> operator : assignOperators) {
            //Tlhs x int -> Tlhs \ int <: Tlhs <: int
            IOverloadBindings overloadBindings = createAssignOverloadBindings();
            overloadBindings.addLowerTypeBound(T_LHS, std.intTypeSymbol);
            overloadBindings.addUpperTypeBound(T_LHS, std.intTypeSymbol);
            overloadBindings.addUpperTypeBound(T_RHS, std.intTypeSymbol);
            overloadBindings.fixType(VAR_RHS);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

            //Tlhs x float -> Tlhs \ float <: Tlhs <: float
            overloadBindings = createAssignOverloadBindings();
            overloadBindings.addLowerTypeBound(T_LHS, std.floatTypeSymbol);
            overloadBindings.addUpperTypeBound(T_LHS, std.floatTypeSymbol);
            overloadBindings.addUpperTypeBound(T_RHS, std.floatTypeSymbol);
            overloadBindings.fixType(VAR_RHS);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

            //Tlhs x {as Tlhs} -> Tlhs \ Tlhs <: num
            overloadBindings = createAssignOverloadBindings();
            //bind convertible type to Tlhs
            IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
            asTlhs.setOverloadBindings(overloadBindings, T_LHS);
            overloadBindings.addUpperTypeBound(T_LHS, std.numTypeSymbol);
            overloadBindings.addUpperTypeBound(T_RHS, asTlhs);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

            //Tlhs x Trhs -> Tlhs \ Tlhs <: {as Trhs}, Trhs <: num
            overloadBindings = createAssignOverloadBindings();
            //bind convertible type to Trhs
            IConvertibleTypeSymbol asTrhs = symbolFactory.createConvertibleTypeSymbol();
            asTrhs.setOverloadBindings(overloadBindings, T_RHS);
            overloadBindings.addUpperTypeBound(T_LHS, asTrhs);
            overloadBindings.addUpperTypeBound(T_RHS, std.numTypeSymbol);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.binaryParameterIds);
            addToOperators(operator.second, function);

        }

        //Tlhs x array -> Tlhs \ array <: Tlhs <: array
        IOverloadBindings overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.arrayTypeSymbol);
        overloadBindings.addUpperTypeBound(T_LHS, std.arrayTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RHS, std.arrayTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("+=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.PlusAssign, function);

        createDivAssignOperator();
    }

    private void createDivAssignOperator() {
        IFunctionType function;

        //Tlhs x float -> Tlhs \ (float | falseType) <: Tlhs <: (float | falseType)
        IOverloadBindings overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.floatOrFalse);
        overloadBindings.addUpperTypeBound(T_LHS, std.floatOrFalse);
        overloadBindings.addUpperTypeBound(T_RHS, std.floatTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("/=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.DivideAssign, function);

        //Tlhs x {as float} -> Tlhs \ (float | falseType) <: Tlhs <: (float | falseType)
        overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.floatOrFalse);
        overloadBindings.addUpperTypeBound(T_LHS, std.floatOrFalse);
        overloadBindings.addUpperTypeBound(T_RHS, std.asFloatTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("/=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.DivideAssign, function);


        //Tlhs x float -> Tlhs \ (float | falseType) <: Tlhs <: {as float}
        overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.floatOrFalse);
        overloadBindings.addUpperTypeBound(T_LHS, std.asFloatTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RHS, std.floatTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("/=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.DivideAssign, function);

        //Tlhs x {as num} -> Tlhs \ (num | falseType) <: Tlhs <: {as num}
        overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.numOrFalse);
        overloadBindings.addUpperTypeBound(T_LHS, std.asNumTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RHS, std.asNumTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("/=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.DivideAssign, function);
    }

    private void createModuloOperators() {
        IFunctionType function;

        //int x int -> (int | false)
        addToBinaryOperators(pair("%", TokenTypes.Modulo), std.intTypeSymbol, std.intTypeSymbol, std.intOrFalse);

        //{as int} x {as int} -> (int | false)
        addToBinaryOperators(pair("%", TokenTypes.Modulo), std.asIntTypeSymbol, std.asIntTypeSymbol, std.intOrFalse);


        //Tlhs x int -> Tlhs \ (int | falseType) <: Tlhs <: (int | falseType)
        IOverloadBindings overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.intOrFalse);
        overloadBindings.addUpperTypeBound(T_LHS, std.intOrFalse);
        overloadBindings.addUpperTypeBound(T_RHS, std.intTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("%=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.ModuloAssign, function);

        //Tlhs x {as int} -> Tlhs \ (int | falseType) <: Tlhs <: {as int}
        overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.intOrFalse);
        overloadBindings.addUpperTypeBound(T_LHS, std.asIntTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RHS, std.asIntTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType("%=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.ModuloAssign, function);
    }

    private void createUnaryArithmeticOperators() {
        IFunctionType function;

        IUnionTypeSymbol stringOrIntOrFloat = symbolFactory.createUnionTypeSymbol();
        stringOrIntOrFloat.addTypeSymbol(std.stringTypeSymbol);
        stringOrIntOrFloat.addTypeSymbol(std.intTypeSymbol);
        stringOrIntOrFloat.addTypeSymbol(std.floatTypeSymbol);

        IUnionTypeSymbol nullOrInt = symbolFactory.createUnionTypeSymbol();
        nullOrInt.addTypeSymbol(std.nullTypeSymbol);
        nullOrInt.addTypeSymbol(std.intTypeSymbol);

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] incrDecrOperators = new Pair[]{
                pair("++", TokenTypes.PRE_INCREMENT),
                pair("++", TokenTypes.POST_INCREMENT),
                pair("--", TokenTypes.PRE_DECREMENT),
                pair("--", TokenTypes.POST_DECREMENT),
        };
        for (Pair<String, Integer> operator : incrDecrOperators) {
            //T -> T \ int <: T <: int
            createIncrDecrOverload(operator, std.intTypeSymbol);
            //T -> T \ float <: T <: float
            createIncrDecrOverload(operator, std.floatTypeSymbol);
            //T -> T \ (string | int | float) <: T <: (string | int | float)
            createIncrDecrOverload(operator, stringOrIntOrFloat);
            //T -> T
            IOverloadBindings overloadBindings = createUnaryTBindings();
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.unaryParameterId);
            addToOperators(operator.second, function);
        }

        //T -> T \ (nullType | int) <: T <: (nullType | int)
        createIncrDecrOverload(pair("++", TokenTypes.PRE_INCREMENT), nullOrInt);
        createIncrDecrOverload(pair("++", TokenTypes.POST_INCREMENT), nullOrInt);

        //TODO rstoll TINS-332 introduce object pseudo type
        IUnionTypeSymbol scalarOrNullOrObject = symbolFactory.createUnionTypeSymbol();
        scalarOrNullOrObject.addTypeSymbol(std.scalarTypeSymbol);
        scalarOrNullOrObject.addTypeSymbol(std.nullTypeSymbol);

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] unaryPlusMinusOperators = new Pair[]{
                pair("-", TokenTypes.UNARY_MINUS),
                pair("+", TokenTypes.UNARY_PLUS)
        };
        for (Pair<String, Integer> operator : unaryPlusMinusOperators) {
            //T -> T \ T <: (scalar | nullType | object)
            IOverloadBindings overloadBindings = createUnaryTBindings();
            overloadBindings.addUpperTypeBound("T", scalarOrNullOrObject);
            function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.unaryParameterId);
            addToOperators(operator.second, function);
        }
    }

    private void createIncrDecrOverload(Pair<String, Integer> operator, ITypeSymbol typeSymbol) {
        IFunctionType function;
        IOverloadBindings overloadBindings = createUnaryTBindings();
        overloadBindings.addLowerTypeBound("T", typeSymbol);
        overloadBindings.addUpperTypeBound("T", typeSymbol);
        function = symbolFactory.createFunctionType(operator.first, overloadBindings, std.unaryParameterId);
        addToOperators(operator.second, function);
    }

    private void defineDotOperator() {
        //string x string -> string
        addToBinaryOperators(
                pair(".", TokenTypes.Dot), std.stringTypeSymbol, std.stringTypeSymbol, std.stringTypeSymbol);
        //{as string} x {as string} -> string
        addToBinaryOperators(
                pair(".", TokenTypes.Dot), std.asStringTypeSymbol, std.asStringTypeSymbol, std.stringTypeSymbol);

        //Tlhs x string -> Tlhs \ string <: Tlhs <: string
        IOverloadBindings overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.stringTypeSymbol);
        overloadBindings.addUpperTypeBound(T_LHS, std.stringTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RHS, std.stringTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        IFunctionType function = symbolFactory.createFunctionType(".=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.DotAssign, function);

        //Tlhs x {as string} -> Tlhs \ string <: Tlhs <: {as string}
        overloadBindings = createAssignOverloadBindings();
        overloadBindings.addLowerTypeBound(T_LHS, std.stringTypeSymbol);
        overloadBindings.addUpperTypeBound(T_LHS, std.asStringTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RHS, std.asStringTypeSymbol);
        overloadBindings.fixType(VAR_RHS);
        function = symbolFactory.createFunctionType(".=", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.DotAssign, function);
    }

    private void defineInstanceOfOperator() {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: object x mixed -> bool
        //mixed x (object | string) -> bool
        Pair<String, Integer> instanceOf = pair("instanceof", TokenTypes.Instanceof);
        addToBinaryOperators(instanceOf, std.mixedTypeSymbol, std.mixedTypeSymbol, std.boolTypeSymbol);
    }

    private void defineCloneAndNewOperator() {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: T -> T \ T <: object
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

        //TODO TINS-481 - rewrite casts in AST to function calls - remove the cast operator entirely

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //T1 x T2 -> T1 \ T2 <: ~{as T1}
        //simplified version for now: T1 x T2 -> T1 or expanded: Tlhs x Trhs -> Treturn \ Treturn :> Tlhs
        collection = createVariableBinaryBindings();
        collection.addLowerRefBound(T_RETURN, reference(T_LHS));
        function = symbolFactory.createFunctionType("cast", collection, std.binaryParameterIds);
        addToOperators(TokenTypes.CAST, function);
    }

    private void defineControlFlowOperators() {
        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("if", TokenTypes.If), std.boolTypeSymbol, std.mixedTypeSymbol);
        //{as bool} -> void
        addToUnaryOperators(pair("if", TokenTypes.If), std.asBoolTypeSymbol, std.mixedTypeSymbol);

        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("while", TokenTypes.While), std.boolTypeSymbol, std.mixedTypeSymbol);
        //{as bool} -> void
        addToUnaryOperators(pair("while", TokenTypes.While), std.asBoolTypeSymbol, std.mixedTypeSymbol);

        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("do", TokenTypes.Do), std.boolTypeSymbol, std.mixedTypeSymbol);
        //{as bool} -> void
        addToUnaryOperators(pair("do", TokenTypes.Do), std.asBoolTypeSymbol, std.mixedTypeSymbol);

        //TODO rstoll TINS-391 - Introduce void as own type
        //bool -> mixed
        addToUnaryOperators(pair("for", TokenTypes.For), std.boolTypeSymbol, std.mixedTypeSymbol);
        //~{as bool} -> void
        addToUnaryOperators(pair("for", TokenTypes.For), std.asBoolTypeSymbol, std.mixedTypeSymbol);

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

        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(varArr, fixReference(tArr));
        overloadBindings.addVariable(varValue, fixReference(tValue));
        overloadBindings.addVariable(varKey, fixReference(tKey));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, fixReference(T_RETURN));

        overloadBindings.addLowerTypeBound(tArr, std.arrayTypeSymbol);
        overloadBindings.addUpperTypeBound(tArr, std.arrayTypeSymbol);
        overloadBindings.addLowerTypeBound(tValue, std.mixedTypeSymbol);
        overloadBindings.addUpperTypeBound(tValue, std.mixedTypeSymbol);
        overloadBindings.addLowerTypeBound(tKey, intOrString);
        overloadBindings.addUpperTypeBound(tKey, intOrString);
        overloadBindings.addLowerTypeBound(T_RETURN, std.mixedTypeSymbol);
        overloadBindings.addUpperTypeBound(T_RETURN, std.mixedTypeSymbol);
        IFunctionType function
                = symbolFactory.createFunctionType("foreach", overloadBindings, Arrays.asList(arr, value, key));
        addToOperators(TokenTypes.Foreach, function);

        //TODO rstoll TINS-391 - Introduce void as own type
        //scalar -> mixed
        addToUnaryOperators(pair("switch", TokenTypes.Switch), std.scalarTypeSymbol, std.mixedTypeSymbol);

        //TODO rstoll TINS-394 introduce nothing as own type
        // Exception -> mixed
        ITypeSymbol exception = (ITypeSymbol) builtInSymbols.get("\\Exception");
        addToUnaryOperators(pair("throw", TokenTypes.Throw), exception, std.mixedTypeSymbol);

        //Tlhs x Trhs -> Trhs \ Trhs :> Tlhs
        overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(VAR_LHS, reference(T_LHS));
        overloadBindings.addVariable(VAR_RHS, reference(T_RHS));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_RHS));

        overloadBindings.addLowerRefBound(T_RHS, reference(T_LHS));
        function = symbolFactory.createFunctionType("catch", overloadBindings, std.binaryParameterIds);
        addToOperators(TokenTypes.Catch, function);
    }

    private void defineGlobalFunctions() {
        //TODO rstoll TINS-391 - Introduce void as own type
        //string -> mixed
        addToUnaryOperators(pair("echo", TokenTypes.Echo), std.stringTypeSymbol, std.mixedTypeSymbol);
        //{as string} -> void
        addToUnaryOperators(pair("echo", TokenTypes.Echo), std.asStringTypeSymbol, std.mixedTypeSymbol);

        //TODO rstoll TINS-391 - Introduce void as own type
        //int -> void
        addToUnaryOperators(pair("exit", TokenTypes.Exit), std.intTypeSymbol, std.mixedTypeSymbol);
        //TODO rstoll TINS-391 - Introduce void as own type
        //string -> void
        addToUnaryOperators(pair("exit", TokenTypes.Exit), std.stringTypeSymbol, std.mixedTypeSymbol);
        //{as string} -> void
        addToUnaryOperators(pair("exit", TokenTypes.Exit), std.asStringTypeSymbol, std.mixedTypeSymbol);
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

