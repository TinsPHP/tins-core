/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
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
import java.util.Map;


@RunWith(Parameterized.class)
public class OperatorProviderBinaryOverloadTest extends ATest
{
    private String operatorName;
    private int operatorType;
    private int numberOfOverloads;

    public OperatorProviderBinaryOverloadTest(String theOperatorName, int theOperatorType, int theNumberOfOverloads) {
        operatorName = theOperatorName;
        operatorType = theOperatorType;
        numberOfOverloads = theNumberOfOverloads;
    }

    @Test
    public void test() throws RecognitionException {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, IOverloadSymbol> result = provider.getOperators();

        Assert.assertEquals(operatorName + " failed, number of overloads different",
                numberOfOverloads, result.get(operatorType).getOverloads().size());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"or", TokenTypes.LogicOrWeak, 4},
                {"xor", TokenTypes.LogicXorWeak, 5},
                {"and", TokenTypes.LogicAndWeak, 4},
                {"=", TokenTypes.Assign, 1},
                {"+=", TokenTypes.PlusAssign, 5},
                {"-=", TokenTypes.MinusAssign, 4},
                {"*=", TokenTypes.MultiplyAssign, 4},
                {"/=", TokenTypes.DivideAssign, 2},
                {"%=", TokenTypes.ModuloAssign, 1},
                {"&=", TokenTypes.BitwiseAndAssign, 2},
                {"^=", TokenTypes.BitwiseXorAssign, 2},
                {"|=", TokenTypes.BitwiseOrAssign, 2},
                {">>=", TokenTypes.ShiftLeftAssign, 1},
                {"<<=", TokenTypes.ShiftRightAssign, 1},
                {".=", TokenTypes.DotAssign, 1},
                {"?", TokenTypes.QuestionMark, 3},
                {"||", TokenTypes.LogicOr, 4},
                {"&&", TokenTypes.LogicAnd, 4},
                {"|", TokenTypes.BitwiseOr, 2},
                {"^", TokenTypes.BitwiseXor, 2},
                {"&", TokenTypes.BitwiseAnd, 2},
                {"==", TokenTypes.Equal, 1},
                {"===", TokenTypes.Identical, 1},
                {"!=", TokenTypes.NotEqual, 1},
                {"!==", TokenTypes.NotIdentical, 1},
                {"<", TokenTypes.LessThan, 1},
                {"<=", TokenTypes.LessEqualThan, 1},
                {">", TokenTypes.GreaterThan, 1},
                {">=", TokenTypes.GreaterEqualThan, 1},
                {"<<", TokenTypes.ShiftLeft, 1},
                {">>", TokenTypes.ShiftRight, 1},
                {"+", TokenTypes.Plus, 5},
                {"-", TokenTypes.Minus, 4},
                {".", TokenTypes.Dot, 1},
                {"*", TokenTypes.Multiply, 4},
                {"/", TokenTypes.Divide, 3},
                {"%", TokenTypes.Modulo, 1},
                {"instanceof", TokenTypes.Instanceof, 1},
                {"clone", TokenTypes.Clone, 1},
                {"new", TokenTypes.New, 1},
                {"++", TokenTypes.PRE_INCREMENT, 4},
                {"--", TokenTypes.PRE_DECREMENT, 4},
                {"@", TokenTypes.At, 1},
                {"~", TokenTypes.BitwiseNot, 2},
                {"!", TokenTypes.LogicNot, 3},
                {"-", TokenTypes.UNARY_MINUS, 4},
                {"+", TokenTypes.UNARY_PLUS, 4},
                {"++", TokenTypes.POST_INCREMENT, 4},
                {"--", TokenTypes.POST_DECREMENT, 4},
        });
    }

    private IOperatorsProvider createOperatorProvider() {
        return createOperatorProvider(primitiveTypes);
    }

    protected IOperatorsProvider createOperatorProvider(Map<String, ITypeSymbol> thePrimitiveType) {
        return new OperatorProvider(symbolFactory, thePrimitiveType);
    }
}
