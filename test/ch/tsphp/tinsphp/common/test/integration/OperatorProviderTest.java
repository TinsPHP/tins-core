/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.test.integration.testutils.ATest;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.OperatorProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;

public class OperatorProviderTest extends ATest
{
    @Test
    public void getOperators_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, List<IMethodSymbol>> result1 = provider.getOperators();
        Map<Integer, List<IMethodSymbol>> backup = new HashMap<>(result1);
        Map<Integer, List<IMethodSymbol>> result2 = provider.getOperators();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    private IOperatorsProvider createOperatorProvider() {
        return createOperatorProvider(
                createGenerator(astHelper, symbolFactory, primitiveTypes),
                primitiveTypes);
    }

    protected IOperatorsProvider createOperatorProvider(
            IGeneratorHelper theGeneratorHelper,
            Map<String, ITypeSymbol> thePrimitiveType) {
        return new OperatorProvider(theGeneratorHelper, thePrimitiveType);
    }
}
