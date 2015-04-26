/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.system;

import ch.tsphp.common.AstHelper;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.config.ICoreInitialiser;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.core.config.HardCodedCoreInitialiser;
import ch.tsphp.tinsphp.core.test.integration.testutils.ATest;
import ch.tsphp.tinsphp.symbols.config.HardCodedSymbolsInitialiser;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;

public class CoreTest extends ATest
{

    @Test
    public void getOperators_Standard_ReturnsAllOperators() {
        //no arrange necessary

        ICoreInitialiser initialiser = createInitialiser();
        ICore core = initialiser.getCore();
        Map<Integer, IMinimalMethodSymbol> result = core.getOperators();

        assertThat(result.size(), is(60));
    }

    @Test
    public void getPrimitiveTypes_Standard_ReturnAllTypes() {
        //no arrange necessary

        ICoreInitialiser initialiser = createInitialiser();
        ICore core = initialiser.getCore();
        Map<String, ITypeSymbol> result = core.getPrimitiveTypes();

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

    protected ICoreInitialiser createInitialiser() {
        return new HardCodedCoreInitialiser(new AstHelper(new TSPHPAstAdaptor()), new HardCodedSymbolsInitialiser());
    }
}
