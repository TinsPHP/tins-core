/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.unit;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.core.ITypeSymbolProvider;
import ch.tsphp.tinsphp.core.PrimitiveTypesProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrimitiveTypesProviderTest
{

    @Test
    public void getTypes_Standard_ContainsAllTypesAccordingToPrimitiveTypeNames() {
        //no arrange necessary

        ITypeSymbolProvider typeSymbolProvider = createPrimitiveTypesProvider(mock(ISymbolFactory.class));
        Map<String, ITypeSymbol> result = typeSymbolProvider.getTypes();

        assertThat(result, hasKey(PrimitiveTypeNames.NOTHING));
        assertThat(result, hasKey(PrimitiveTypeNames.NULL_TYPE));
        assertThat(result, hasKey(PrimitiveTypeNames.TRUE_TYPE));
        assertThat(result, hasKey(PrimitiveTypeNames.FALSE_TYPE));
        assertThat(result, hasKey(PrimitiveTypeNames.BOOL));
        assertThat(result, hasKey(PrimitiveTypeNames.INT));
        assertThat(result, hasKey(PrimitiveTypeNames.FLOAT));
        assertThat(result, hasKey(PrimitiveTypeNames.NUM));
        assertThat(result, hasKey(PrimitiveTypeNames.STRING));
        assertThat(result, hasKey(PrimitiveTypeNames.SCALAR));
        assertThat(result, hasKey(PrimitiveTypeNames.ARRAY));
        assertThat(result, hasKey(PrimitiveTypeNames.RESOURCE));
        assertThat(result, hasKey(PrimitiveTypeNames.MIXED));
    }

    @Test
    public void getTypes_Standard_HasSetMixedTypeSymbolForSymbolFactory() {
        ISymbolFactory symbolFactory = mock(ISymbolFactory.class);
        IPseudoTypeSymbol typeSymbol = mock(IPseudoTypeSymbol.class);
        when(symbolFactory.createPseudoTypeSymbol("mixed")).thenReturn(typeSymbol);

        ITypeSymbolProvider typeSymbolProvider = createPrimitiveTypesProvider(symbolFactory);
        Map<String, ITypeSymbol> result = typeSymbolProvider.getTypes();

        verify(symbolFactory).setMixedTypeSymbol(typeSymbol);
        assertThat(result.get(PrimitiveTypeNames.MIXED), is((ISymbol) typeSymbol));
    }

    @Test
    public void getTypes_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        ITypeSymbolProvider typeSymbolProvider = createPrimitiveTypesProvider(mock(ISymbolFactory.class));
        Map<String, ITypeSymbol> result1 = typeSymbolProvider.getTypes();
        Map<String, ITypeSymbol> backup = new HashMap<>(result1);
        Map<String, ITypeSymbol> result2 = typeSymbolProvider.getTypes();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
    }

    protected PrimitiveTypesProvider createPrimitiveTypesProvider(ISymbolFactory symbolFactory) {
        return new PrimitiveTypesProvider(symbolFactory);
    }

}
