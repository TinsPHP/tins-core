/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.INullTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.Core;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreTest
{
    @Test
    public void getNullTypeSymbol_Standard_ReturnNullTypeSymbolFromSymbolFactory() {
        ISymbolFactory symbolFactory = mock(ISymbolFactory.class);
        INullTypeSymbol symbol = mock(INullTypeSymbol.class);
        when(symbolFactory.createNullTypeSymbol()).thenReturn(symbol);

        ICore core = createCore(symbolFactory);
        INullTypeSymbol result = core.getNullTypeSymbol();

        assertThat(result, is(symbol));
    }

    private ICore createCore(ISymbolFactory theSymbolFactory) {
        return createCore(theSymbolFactory, mock(IAstHelper.class));
    }

    protected ICore createCore(
            ISymbolFactory theSymbolFactory,
            IAstHelper theAstHelper) {
        return new Core(theSymbolFactory, theAstHelper);
    }
}
