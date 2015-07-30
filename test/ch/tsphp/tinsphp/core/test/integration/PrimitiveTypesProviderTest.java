/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.core.ITypeSymbolProvider;
import ch.tsphp.tinsphp.core.PrimitiveTypesProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.ATest;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;

public class PrimitiveTypesProviderTest extends ATest
{

    @Test
    public void getTypes_Standard_ArrayHasKeyTypeIntOrString() {
        //no arrange necessary

        ITypeSymbolProvider typeSymbolProvider = createPrimitiveTypesProvider(symbolFactory);
        Map<String, ITypeSymbol> result = typeSymbolProvider.getTypes();

        assertThat(result, hasKey(PrimitiveTypeNames.ARRAY));
        IArrayTypeSymbol arrayTypeSymbol = (IArrayTypeSymbol) result.get(PrimitiveTypeNames.ARRAY);
        assertThat(arrayTypeSymbol.getKeyTypeSymbol().getAbsoluteName(), is("(int | string)"));
    }

    protected PrimitiveTypesProvider createPrimitiveTypesProvider(ISymbolFactory symbolFactory) {
        return new PrimitiveTypesProvider(symbolFactory);
    }
}
