/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.core.test.integration.testutils.AConversionsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ConversionsProviderExplicitTest extends AConversionsProvider
{

    public ConversionsProviderExplicitTest(String fromType, String toType, boolean result) {
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
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.STRING, true},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.ARRAY, true},
                //bool
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.STRING, true},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.INT, true},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.STRING, true},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.INT, true},
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
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.STRING, true},
                //array
                {PrimitiveTypeNames.ARRAY, PrimitiveTypeNames.BOOL, true},
                //TODO rstoll TINS-332 introduce object pseudo type - change to object
                {PrimitiveTypeNames.MIXED, PrimitiveTypeNames.BOOL, true},
                {PrimitiveTypeNames.MIXED, PrimitiveTypeNames.ARRAY, true},

                //string to int is no longer supported
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.INT, false},

                //explicit conversions to float are no longer supported
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.FLOAT, false},
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.FLOAT, false},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.FLOAT, false},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.FLOAT, false},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.FLOAT, false},

                {PrimitiveTypeNames.INT, PrimitiveTypeNames.FLOAT, false},
        });
    }

    @Override
    protected IConversionsProvider createConversionsProvider(Map<String, ITypeSymbol> types) {
        IConversionsProvider conversionsProvider1 = super.createConversionsProvider(types);
        IConversionsProvider conversionsProvider = mock(IConversionsProvider.class);
        when(conversionsProvider.getExplicitConversions()).thenReturn(conversionsProvider1.getExplicitConversions());
        when(conversionsProvider.getImplicitConversions()).thenReturn(
                new HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>>());
        return conversionsProvider;
    }
}
