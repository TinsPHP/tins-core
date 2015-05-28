/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.core.test.integration.testutils.ATest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;

public class ConversionsProviderTest extends ATest
{
    @Test
    public void getExplicitConversions_Standard_Returns8() {
        //no arrange necessary

        IConversionsProvider provider = createConversionsProvider();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> result = provider.getExplicitConversions();

        assertThat(result.size(), is(9));
    }

    @Test
    public void getImplicitConversions_Standard_Returns1() {
        //no arrange necessary

        IConversionsProvider provider = createConversionsProvider();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> result = provider.getImplicitConversions();

        assertThat(result.size(), is(1));
    }


    @Test
    public void getExplicitConversions_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        IConversionsProvider provider = createConversionsProvider();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> result1 = provider.getExplicitConversions();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> backup = new HashMap<>(result1);
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> result2 = provider.getExplicitConversions();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    @Test
    public void getImplicitConversions_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        IConversionsProvider provider = createConversionsProvider();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> result1 = provider.getImplicitConversions();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> backup = new HashMap<>(result1);
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> result2 = provider.getImplicitConversions();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    private IConversionsProvider createConversionsProvider() {
        return createConversionsProvider(primitiveTypes);
    }
}
