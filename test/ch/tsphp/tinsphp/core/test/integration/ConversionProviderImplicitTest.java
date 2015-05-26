/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.core.Core;
import ch.tsphp.tinsphp.core.IConversionsProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.AConversionProviderTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class ConversionProviderImplicitTest extends AConversionProviderTest
{

    public ConversionProviderImplicitTest(String fromType, String toType, boolean result) {
        super(fromType, toType, result);
    }

    @Test
    public void test() {
        run();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {PrimitiveTypeNames.INT, PrimitiveTypeNames.FLOAT, true},
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.INT, false},
        });
    }

    @Override
    protected ICore createCore(IConversionsProvider provider) {
        return new Core(
                null,
                provider.getImplicitConversions(),
                new HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>>(),
                null);
    }
}
