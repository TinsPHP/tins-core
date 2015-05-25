/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import ch.tsphp.tinsphp.core.StandardConstraintAndVariables;
import ch.tsphp.tinsphp.core.gen.BuiltInSymbolsProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.ATest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;

public class BuiltInSymbolsProviderTest extends ATest
{

    @Test
    public void getSymbols_SecondCall_DoesNotNeedToRecompute() {
        //no arrange necessary

        ISymbolProvider provider = createBuiltInSymbolsProvider();
        Map<String, ISymbol> result1 = provider.getSymbols();
        Map<String, ISymbol> backup = new HashMap<>(result1);
        Map<String, ISymbol> result2 = provider.getSymbols();

        assertThat(result1, is(result2));
        assertThat(result2.entrySet(), everyItem(isIn(backup.entrySet())));
        assertThat(result2.size(), is(backup.size()));
    }

    private BuiltInSymbolsProvider createBuiltInSymbolsProvider() {
        return createBuiltInSymbolsProvider(
                createGenerator(astHelper, symbolFactory, primitiveTypes),
                symbolFactory,
                typeHelper,
                std
        );
    }

    protected BuiltInSymbolsProvider createBuiltInSymbolsProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            ITypeHelper theTypeHelper,
            StandardConstraintAndVariables standardConstraintAndVariables) {
        return new BuiltInSymbolsProvider(
                theGeneratorHelper, theSymbolFactory, theTypeHelper, standardConstraintAndVariables);
    }
}
