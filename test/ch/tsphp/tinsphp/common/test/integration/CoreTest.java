/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.Core;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;

public class CoreTest extends ATest
{

    @Test
    public void getPrimitiveTypes_Standard_ReturnNullTypeSymbolFromSymbolFactory() {
        //

        ICore core = createCore(symbolFactory, astHelper);
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

    protected ICore createCore(ISymbolFactory theSymbolFactory, IAstHelper theAstHelper) {
        return new Core(theSymbolFactory, theAstHelper);
    }
}
