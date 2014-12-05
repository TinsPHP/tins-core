/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.BuiltInSuperGlobalsProvider;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;

public class BuiltInSuperGlobalsProviderTest extends ATest
{
    @Test
    public void getSymbols_Standard_VerifyAllCreated() {

        ISymbolProvider provider = createBuiltInSuperGlobalsProvider();
        Map<String, ISymbol> result = provider.getSymbols();

        assertThat(result, hasKey("$_GET"));
    }

    private ISymbolProvider createBuiltInSuperGlobalsProvider() {
        return createBuiltInSuperGlobalsProvider(
                createGenerator(astHelper, symbolFactory, primitiveTypes),
                symbolFactory,
                primitiveTypes);
    }

    protected ISymbolProvider createBuiltInSuperGlobalsProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        return new BuiltInSuperGlobalsProvider(theGeneratorHelper, theSymbolFactory, thePrimitiveType);
    }
}
