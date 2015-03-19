/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;

public class OperatorProvider implements IOperatorsProvider
{
    private final ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<Integer, IOverloadSymbol> builtInOperators;

    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        symbolFactory = theSymbolFactory;
        primitiveTypes = thePrimitiveType;
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
        defineBitLevelOperators();
        defineRelationalOperators();
        defineArithmeticOperators();
        defineDotOperator();
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
                //unary operators
                pair("++", TokenTypes.PRE_INCREMENT), pair("--", TokenTypes.PRE_DECREMENT),
                pair("@", TokenTypes.At), pair("~", TokenTypes.BitwiseNot), pair("!", TokenTypes.LogicNot),
                pair("-", TokenTypes.UNARY_MINUS), pair("+", TokenTypes.UNARY_PLUS),
                pair("++", TokenTypes.POST_INCREMENT), pair("--", TokenTypes.POST_DECREMENT)
        };

        for (Pair<String, Integer> operatorType : operatorTypes) {
            builtInOperators.put(operatorType.second, symbolFactory.createOverloadSymbol(operatorType.first));
        }
    }

    private void defineLogicOperators() {
        ITypeSymbol boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("or", TokenTypes.LogicOrWeak),
                pair("xor", TokenTypes.LogicXorWeak),
                pair("and", TokenTypes.LogicAndWeak),
                pair("&&", TokenTypes.LogicAnd),
                pair("||", TokenTypes.LogicOr)
        };
        for (Pair<String, Integer> operator : operators) {
            addToAutoConvertingBinaryOperators(operator, boolTypeSymbol, boolTypeSymbol, boolTypeSymbol);
        }
        addToAutoConvertingUnaryOperators(new Pair<>("!", TokenTypes.LogicNot), boolTypeSymbol, boolTypeSymbol);
    }

    private void defineBitLevelOperators() {
        ITypeSymbol intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd),
                pair("<<", TokenTypes.ShiftLeft),
                pair(">>", TokenTypes.ShiftRight)
        };
        for (Pair<String, Integer> operator : operators) {
            addToAutoConvertingBinaryOperators(operator, intTypeSymbol, intTypeSymbol, intTypeSymbol);
        }
        operators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd)
        };
        for (Pair<String, Integer> operator : operators) {
            addToBinaryOperators(operator, stringTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        }
        addToAutoConvertingUnaryOperators(new Pair<>("~", TokenTypes.BitwiseNot), intTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Pair<>("~", TokenTypes.BitwiseNot), stringTypeSymbol, stringTypeSymbol);
    }

    private void defineRelationalOperators() {
        ITypeSymbol boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("<", TokenTypes.LessThan),
                pair("<=", TokenTypes.LessEqualThan),
                pair(">", TokenTypes.GreaterThan),
                pair(">=", TokenTypes.GreaterEqualThan)
        };
        for (Pair<String, Integer> operator : operators) {
            addToAutoConvertingUnaryOperators(operator, boolTypeSymbol, boolTypeSymbol);
        }
    }

    private void defineArithmeticOperators() {
        ITypeSymbol scalarTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.SCALAR);
        ITypeSymbol numTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.NUM);
        ITypeSymbol boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);
        ITypeSymbol intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        ITypeSymbol floatTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.FLOAT);
        ITypeSymbol arrayTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.ARRAY);

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply),
                pair("/", TokenTypes.Divide),
                pair("%", TokenTypes.Modulo)
        };
        for (Pair<String, Integer> operator : operators) {
            addToBinaryOperators(operator, scalarTypeSymbol, scalarTypeSymbol, numTypeSymbol);
            addToBinaryOperators(operator, intTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperators(operator, floatTypeSymbol, floatTypeSymbol, floatTypeSymbol);
        }

        operators = new Pair[]{
                pair("++", TokenTypes.PRE_INCREMENT),
                pair("++", TokenTypes.POST_INCREMENT),
                pair("--", TokenTypes.PRE_DECREMENT),
                pair("--", TokenTypes.POST_DECREMENT),
        };
        for (Pair<String, Integer> operator : operators) {
            addToUnaryOperators(operator, boolTypeSymbol, boolTypeSymbol);
            addToUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, floatTypeSymbol, floatTypeSymbol);

        }

        operators = new Pair[]{
                pair("-", TokenTypes.UNARY_MINUS),
                pair("+", TokenTypes.UNARY_PLUS)
        };
        for (Pair<String, Integer> operator : operators) {
            addToAutoConvertingUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, floatTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, scalarTypeSymbol, numTypeSymbol);
        }

        addToBinaryOperators(new Pair<>("+", TokenTypes.Plus), arrayTypeSymbol, arrayTypeSymbol, arrayTypeSymbol);
    }

    private void defineDotOperator() {
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);
        addToAutoConvertingUnaryOperators(new Pair<>(".", TokenTypes.Dot), stringTypeSymbol, stringTypeSymbol);
    }

    private void addToAutoConvertingBinaryOperators(
            Pair<String, Integer> operator, ITypeSymbol leftParameterType, ITypeSymbol rightParameterType,
            ITypeSymbol returnType) {
        addToBinaryOperators(operator, leftParameterType, rightParameterType, returnType);
        //TODO TINS-330 define operator overloads
        //there are more constraints to add ~{as bool} for instance
    }

    private IFunctionTypeSymbol addToBinaryOperators(Pair<String, Integer> operator,
            ITypeSymbol leftParameterType, ITypeSymbol rightParameterType, ITypeSymbol returnType) {

        IFunctionTypeSymbol function = symbolFactory.createConstantFunctionTypeSymbol(
                operator.first, Arrays.asList("$lhs", "$rhs"), returnType);
        function.addParameterConstraint("$lhs", new TypeConstraint(leftParameterType));
        function.addParameterConstraint("$lhs", new TypeConstraint(rightParameterType));
        addToOperators(operator.second, function);
        return function;
    }

    private void addToAutoConvertingUnaryOperators(
            Pair<String, Integer> operator, ITypeSymbol formalParameterType, ITypeSymbol returnType) {
        addToUnaryOperators(operator, formalParameterType, returnType);
        //TODO TINS-330 define operator overloads
        //there are more constraints to add ~{as bool} for instance
    }

    private IFunctionTypeSymbol addToUnaryOperators(
            Pair<String, Integer> operator, ITypeSymbol formalParameterType, ITypeSymbol returnType) {

        IFunctionTypeSymbol function = symbolFactory.createConstantFunctionTypeSymbol(
                operator.first, Arrays.asList("expr"), returnType);

        function.addParameterConstraint("expr", new TypeConstraint(formalParameterType));
        addToOperators(operator.second, function);
        return function;
    }

    private void addToOperators(int operatorType, IFunctionTypeSymbol functionTypeSymbol) {
        IOverloadSymbol overloadSymbol = builtInOperators.get(operatorType);
        overloadSymbol.addOverload(functionTypeSymbol);
    }

}



