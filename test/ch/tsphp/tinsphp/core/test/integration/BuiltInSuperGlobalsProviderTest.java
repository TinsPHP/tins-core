/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.BuiltInSuperGlobalSymbolsProvider;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.ATest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.collection.IsMapContaining.hasKey;

public class BuiltInSuperGlobalsProviderTest extends ATest
{
    @Test
    public void getSymbols_Standard_VerifyAllCreated() {

        ISymbolProvider provider = createBuiltInSuperGlobalSymbolsProvider();
        Map<String, ISymbol> result = provider.getSymbols();

        assertThat(result, hasKey("$_GET"));
    }

    @Test
    public void getSymbols_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        ISymbolProvider provider = createBuiltInSuperGlobalSymbolsProvider();
        Map<String, ISymbol> result1 = provider.getSymbols();
        Map<String, ISymbol> backup = new HashMap<>(result1);
        Map<String, ISymbol> result2 = provider.getSymbols();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    private ISymbolProvider createBuiltInSuperGlobalSymbolsProvider() {
        return createBuiltInSuperGlobalSymbolsProvider(
                createGenerator(astHelper, symbolFactory, primitiveTypes),
                symbolFactory,
                primitiveTypes);
    }

    protected ISymbolProvider createBuiltInSuperGlobalSymbolsProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        return new BuiltInSuperGlobalSymbolsProvider(theGeneratorHelper, theSymbolFactory, thePrimitiveType);
    }
}
