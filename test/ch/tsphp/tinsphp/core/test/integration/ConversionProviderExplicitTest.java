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
public class ConversionProviderExplicitTest extends AConversionProviderTest
{

    public ConversionProviderExplicitTest(String fromType, String toType, boolean result) {
        super(fromType, toType, result);
    }

    @Test
    public void test() {
        run();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.FLOAT, true},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.STRING, true},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.ARRAY, true},
                //bool
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.FLOAT, true},
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.STRING, true},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.FLOAT, true},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.STRING, true},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.FLOAT, true},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.STRING, true},
                //int
                {PrimitiveTypeNames.INT, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.INT, PrimitiveTypeNames.STRING, true},
                //float
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.STRING, true},
                //string
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.FLOAT, true},
                //resource
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.FLOAT, true},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.STRING, true},
                //array
                {PrimitiveTypeNames.ARRAY, PrimitiveTypeNames.BOOL, true},
                //TODO rstoll TINS-332 introduce object pseudo type - change to object
                {PrimitiveTypeNames.MIXED, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.MIXED, PrimitiveTypeNames.ARRAY, true},

                {PrimitiveTypeNames.INT, PrimitiveTypeNames.FLOAT, false},
        });
    }

    @Override
    protected ICore createCore(IConversionsProvider provider) {
        return new Core(
                null,
                new HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>>(),
                provider.getExplicitConversions(),
                null);
    }
}
