/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.common.test.integration.testutils.ATest;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.OperatorProvider;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@RunWith(Parameterized.class)
public class OperatorProviderBinaryConstraintsTest extends ATest
{
    private String operatorName;
    private int operatorType;

    public OperatorProviderBinaryConstraintsTest(String theOperatorName, int theOperatorType) {
        operatorName = theOperatorName;
        operatorType = theOperatorType;
    }

    @Test
    public void test() throws RecognitionException {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, IOverloadSymbol> result = provider.getOperators();

        List<IFunctionTypeSymbol> overloads = result.get(operatorType).getOverloads();
        Assert.assertEquals(operatorName + " failed, no overload defined", false, overloads.isEmpty());
        for (IFunctionTypeSymbol overload : overloads) {
            Assert.assertEquals(operatorName + " failed, number of parameters wrong",
                    2, overload.getParametersConstraints().size());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"or", TokenTypes.LogicOrWeak},
                {"xor", TokenTypes.LogicXorWeak},
                {"and", TokenTypes.LogicAndWeak},
                {"=", TokenTypes.Assign},
                {"+=", TokenTypes.PlusAssign},
                {"-=", TokenTypes.MinusAssign},
                {"*=", TokenTypes.MultiplyAssign},
                {"/=", TokenTypes.DivideAssign},
                {"%=", TokenTypes.ModuloAssign},
                {"&=", TokenTypes.BitwiseAndAssign},
                {"^=", TokenTypes.BitwiseXorAssign},
                {"|=", TokenTypes.BitwiseOrAssign},
                {".=", TokenTypes.DotAssign},
                {"<<=", TokenTypes.ShiftLeftAssign},
                {">>=", TokenTypes.ShiftRightAssign},
                {"||", TokenTypes.LogicOr},
                {"&&", TokenTypes.LogicAnd},
                {"|", TokenTypes.BitwiseOr},
                {"^", TokenTypes.BitwiseXor},
                {"&", TokenTypes.BitwiseAnd},
                {"==", TokenTypes.Equal},
                {"===", TokenTypes.Identical},
                {"!=", TokenTypes.NotEqual},
                {"!==", TokenTypes.NotIdentical},
                {"<", TokenTypes.LessThan},
                {"<=", TokenTypes.LessEqualThan},
                {">", TokenTypes.GreaterThan},
                {">=", TokenTypes.GreaterEqualThan},
                {"<<", TokenTypes.ShiftLeft},
                {">>", TokenTypes.ShiftRight},
                {"+", TokenTypes.Plus},
                {"-", TokenTypes.Minus},
                {".", TokenTypes.Dot},
                {"*", TokenTypes.Multiply},
                {"/", TokenTypes.Divide},
                {"%", TokenTypes.Modulo},
                {"instanceof", TokenTypes.Instanceof}
        });
    }

    private IOperatorsProvider createOperatorProvider() {
        return createOperatorProvider(primitiveTypes);
    }

    protected IOperatorsProvider createOperatorProvider(Map<String, ITypeSymbol> thePrimitiveType) {
        return new OperatorProvider(symbolFactory, thePrimitiveType);
    }
}
