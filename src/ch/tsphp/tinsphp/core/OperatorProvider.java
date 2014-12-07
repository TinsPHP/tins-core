/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorProvider implements IOperatorsProvider
{
    private final IGeneratorHelper generatorHelper;
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<Integer, List<IMethodSymbol>> builtInOperators;

    public OperatorProvider(
            IGeneratorHelper theGeneratorHelper,
            Map<String, ITypeSymbol> thePrimitiveType) {
        generatorHelper = theGeneratorHelper;
        primitiveTypes = thePrimitiveType;
    }

    @Override
    public Map<Integer, List<IMethodSymbol>> getOperators() {
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
        int[] operatorTypes = new int[]{
                //binary operators
                TokenTypes.LogicOrWeak, TokenTypes.LogicXorWeak,
                TokenTypes.LogicAndWeak,
                TokenTypes.Assign, TokenTypes.PlusAssign, TokenTypes.MinusAssign,
                TokenTypes.MultiplyAssign, TokenTypes.DivideAssign,
                TokenTypes.BitwiseAndAssign, TokenTypes.BitwiseOrAssign,
                TokenTypes.BitwiseXorAssign,
                TokenTypes.ModuloAssign, TokenTypes.DotAssign,
                TokenTypes.ShiftLeftAssign, TokenTypes.ShiftRightAssign,
                TokenTypes.LogicOr, TokenTypes.LogicAnd,
                TokenTypes.BitwiseOr, TokenTypes.BitwiseAnd, TokenTypes.BitwiseXor,
                TokenTypes.Equal, TokenTypes.Identical, TokenTypes.NotEqual,
                TokenTypes.NotIdentical,
                TokenTypes.LessThan, TokenTypes.LessEqualThan,
                TokenTypes.GreaterThan, TokenTypes.GreaterEqualThan,
                TokenTypes.ShiftLeft, TokenTypes.ShiftRight,
                TokenTypes.Plus, TokenTypes.Minus, TokenTypes.Multiply,
                TokenTypes.Divide, TokenTypes.Modulo, TokenTypes.Dot,
                TokenTypes.CAST,
                //unary operators
                TokenTypes.PRE_INCREMENT, TokenTypes.PRE_DECREMENT,
                TokenTypes.At, TokenTypes.BitwiseNot, TokenTypes.LogicNot,
                TokenTypes.UNARY_MINUS, TokenTypes.UNARY_PLUS,
                TokenTypes.POST_INCREMENT, TokenTypes.POST_DECREMENT
        };
        for (int operatorType : operatorTypes) {
            builtInOperators.put(operatorType, new ArrayList<IMethodSymbol>());
        }
    }

    private void defineLogicOperators() {
        ITypeSymbol boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);

        Object[][] operators = new Object[][]{
                {"or", TokenTypes.LogicOrWeak},
                {"xor", TokenTypes.LogicXorWeak},
                {"and", TokenTypes.LogicAndWeak},
                {"&&", TokenTypes.LogicAnd},
                {"||", TokenTypes.LogicOr}
        };
        for (Object[] operator : operators) {
            addToAutoConvertingBinaryOperators(operator, boolTypeSymbol, boolTypeSymbol, boolTypeSymbol);
        }
        addToAutoConvertingUnaryOperators(new Object[]{"!", TokenTypes.LogicNot}, boolTypeSymbol, boolTypeSymbol);
    }

    private void defineBitLevelOperators() {
        ITypeSymbol intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);

        Object[][] operators = new Object[][]{
                {"|", TokenTypes.BitwiseOr},
                {"^", TokenTypes.BitwiseXor},
                {"&", TokenTypes.BitwiseAnd},
                {"<<", TokenTypes.ShiftLeft},
                {">>", TokenTypes.ShiftRight}
        };
        for (Object[] operator : operators) {
            addToAutoConvertingBinaryOperators(operator, intTypeSymbol, intTypeSymbol, intTypeSymbol);
        }

        operators = new Object[][]{
                {"|", TokenTypes.BitwiseOr},
                {"^", TokenTypes.BitwiseXor},
                {"&", TokenTypes.BitwiseAnd}
        };
        for (Object[] operator : operators) {
            addToBinaryOperators(operator, stringTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        }
        addToAutoConvertingUnaryOperators(new Object[]{"~", TokenTypes.BitwiseNot}, intTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", TokenTypes.BitwiseNot}, stringTypeSymbol, stringTypeSymbol);
    }

    private void defineRelationalOperators() {
        ITypeSymbol boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);

        Object[][] operators = new Object[][]{
                {"<", TokenTypes.LessThan},
                {"<=", TokenTypes.LessEqualThan},
                {">", TokenTypes.GreaterThan},
                {">=", TokenTypes.GreaterEqualThan}
        };
        for (Object[] operator : operators) {
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

        Object[][] operators = new Object[][]{
                {"+", TokenTypes.Plus},
                {"-", TokenTypes.Minus},
                {"*", TokenTypes.Multiply},
                {"/", TokenTypes.Divide},
                {"%", TokenTypes.Modulo}
        };
        for (Object[] operator : operators) {
            addToBinaryOperators(operator, scalarTypeSymbol, scalarTypeSymbol, numTypeSymbol);
            addToBinaryOperators(operator, intTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperators(operator, floatTypeSymbol, floatTypeSymbol, floatTypeSymbol);
        }

        operators = new Object[][]{
                {"++", TokenTypes.PRE_INCREMENT},
                {"++", TokenTypes.POST_INCREMENT},
                {"--", TokenTypes.PRE_DECREMENT},
                {"--", TokenTypes.POST_DECREMENT},
        };
        for (Object[] operator : operators) {
            addToUnaryOperators(operator, boolTypeSymbol, boolTypeSymbol);
            addToUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, floatTypeSymbol, floatTypeSymbol);

        }

        operators = new Object[][]{
                {"-", TokenTypes.UNARY_MINUS},
                {"+", TokenTypes.UNARY_PLUS}
        };
        for (Object[] operator : operators) {
            addToAutoConvertingUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, floatTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, scalarTypeSymbol, numTypeSymbol);
        }

        addToBinaryOperators(new Object[]{"+", TokenTypes.Plus}, arrayTypeSymbol, arrayTypeSymbol, arrayTypeSymbol);
    }

    private void defineDotOperator() {
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);
        addToAutoConvertingUnaryOperators(new Object[]{".", TokenTypes.Dot}, stringTypeSymbol, stringTypeSymbol);
    }

    private void addToAutoConvertingBinaryOperators(
            Object[] operator, ITypeSymbol leftParameterType, ITypeSymbol rightParameterType, ITypeSymbol returnType) {
        IVariableSymbol[] params = addToBinaryOperators(operator, leftParameterType, rightParameterType, returnType);
        TypeHelper.addAlwaysCastingModifier(params[0]);
        TypeHelper.addAlwaysCastingModifier(params[1]);
    }

    private IVariableSymbol[] addToBinaryOperators(
            Object[] operator, ITypeSymbol leftParameterType, ITypeSymbol rightParameterType, ITypeSymbol returnType) {
        IMethodSymbol function = generatorHelper.createFunction((String) operator[0], returnType);
        IVariableSymbol[] params = new IVariableSymbol[]{
                generatorHelper.createParameter(function, "LHS", leftParameterType),
                generatorHelper.createParameter(function, "RHS", rightParameterType)
        };
        addToOperators((int) operator[1], function);
        return params;
    }

    private void addToAutoConvertingUnaryOperators(
            Object[] operator, ITypeSymbol formalParameterType, ITypeSymbol returnType) {
        TypeHelper.addAlwaysCastingModifier(addToUnaryOperators(operator, formalParameterType, returnType));
    }

    private IVariableSymbol addToUnaryOperators(
            Object[] operator, ITypeSymbol formalParameterType, ITypeSymbol returnType) {
        IMethodSymbol function = generatorHelper.createFunction((String) operator[0], returnType);
        IVariableSymbol expr = generatorHelper.createParameter(function, "expr", formalParameterType);
        addToOperators((int) operator[1], function);
        return expr;
    }

    private void addToOperators(int operatorType, IMethodSymbol methodSymbol) {
        List<IMethodSymbol> methods = builtInOperators.get(operatorType);
        methods.add(methodSymbol);
    }

}



