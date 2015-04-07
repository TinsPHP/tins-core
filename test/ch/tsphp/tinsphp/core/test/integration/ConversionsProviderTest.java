/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.core.ConversionsProvider;
import ch.tsphp.tinsphp.core.IConversionsProvider;
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
    public void getExplicitConversions_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        IConversionsProvider provider = createConversionsProvider();
        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> result1 = provider.getExplicitConversions();
        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> backup = new HashMap<>(result1);
        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> result2 = provider.getExplicitConversions();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    @Test
    public void getImplicitConversions_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        IConversionsProvider provider = createConversionsProvider();
        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> result1 = provider.getImplicitConversions();
        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> backup = new HashMap<>(result1);
        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> result2 = provider.getImplicitConversions();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    protected IConversionsProvider createConversionsProvider() {
        return createConversionsProvider(primitiveTypes);
    }

    protected IConversionsProvider createConversionsProvider(Map<String, ITypeSymbol> primitiveTypes) {
        return new ConversionsProvider(primitiveTypes);
    }
}
