/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.AOperatorProviderTest;
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
public class OperatorProviderTernaryConstraintsTest extends AOperatorProviderTest
{
    private String operatorName;
    private int operatorType;

    public OperatorProviderTernaryConstraintsTest(String theOperatorName, int theOperatorType) {
        operatorName = theOperatorName;
        operatorType = theOperatorType;
    }

    @Test
    public void test() throws RecognitionException {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, IMinimalMethodSymbol> result = provider.getOperators();

        List<IFunctionType> overloads = result.get(operatorType).getOverloads();
        Assert.assertEquals(operatorName + " failed, no overload defined", false, overloads.isEmpty());
        for (IFunctionType overload : overloads) {
            Assert.assertEquals(operatorName + " failed, number of parameters wrong",
                    3, overload.getParameters().size());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"?", TokenTypes.QuestionMark},
                {"foreach", TokenTypes.Foreach},
        });
    }
}
