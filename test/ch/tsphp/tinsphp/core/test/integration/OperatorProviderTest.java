/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.AOperatorProviderTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;

public class OperatorProviderTest extends AOperatorProviderTest
{
    @Test
    public void getOperators_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, IMinimalMethodSymbol> result1 = provider.getOperators();
        Map<Integer, IMinimalMethodSymbol> backup = new HashMap<>(result1);
        Map<Integer, IMinimalMethodSymbol> result2 = provider.getOperators();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }
}
