/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.resolving.ISymbolResolver;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.Core;
import ch.tsphp.tinsphp.core.test.integration.testutils.ATest;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;

public class CoreTest extends ATest
{
    @Test
    public void getCoreSymbolResolver_SecondCall_ReturnsSameInstanceAsFirstCall() {
        //no arrange necessary

        ICore core = createCore();
        ISymbolResolver firstCall = core.getCoreSymbolResolver();
        ISymbolResolver result = core.getCoreSymbolResolver();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getOperators_Standard_ReturnsAllOperators() {
        //no arrange necessary

        ICore core = createCore();
        Map<Integer, IOverloadSymbol> result = core.getOperators();

        assertThat(result.size(), is(50));
    }

    @Test
    public void getPrimitiveTypes_Standard_ReturnAllTypes() {
        //no arrange necessary

        ICore core = createCore();
        Map<String, ITypeSymbol> result = core.getPrimitiveTypes();

        assertThat(result, hasKey(PrimitiveTypeNames.NULL));
        assertThat(result, hasKey(PrimitiveTypeNames.TRUE));
        assertThat(result, hasKey(PrimitiveTypeNames.FALSE));
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

    private ICore createCore() {
        return createCore(symbolFactory, overloadResolver, astHelper);
    }

    protected ICore createCore(
            ISymbolFactory theSymbolFactory, IOverloadResolver theOverloadResolver, IAstHelper theAstHelper) {
        return new Core(theSymbolFactory, theOverloadResolver, theAstHelper);
    }
}
