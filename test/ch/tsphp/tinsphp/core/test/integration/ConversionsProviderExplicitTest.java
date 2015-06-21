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
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.core.test.integration.testutils.AConversionsProviderTest;
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
public class ConversionsProviderExplicitTest extends AConversionsProviderTest
{

    public ConversionsProviderExplicitTest(String fromType, String toType, ERelation result) {
        super(fromType, toType, result);
    }

    @Test
    public void test() {
        run();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.ARRAY, ERelation.HAS_RELATION},
                //bool
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                //int
                {PrimitiveTypeNames.INT, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.INT, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                //float
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.FLOAT, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                //string
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.FLOAT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.STRING, PrimitiveTypeNames.NUM, ERelation.HAS_RELATION},
                //resource
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.INT, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                //array
                {PrimitiveTypeNames.ARRAY, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.ARRAY, PrimitiveTypeNames.STRING, ERelation.HAS_RELATION},
                //TODO rstoll TINS-332 introduce object pseudo type - change to object
                {PrimitiveTypeNames.MIXED, PrimitiveTypeNames.BOOL, ERelation.HAS_RELATION},
                {PrimitiveTypeNames.MIXED, PrimitiveTypeNames.ARRAY, ERelation.HAS_RELATION},

                //explicit conversions to float are no longer supported
                {PrimitiveTypeNames.NULL_TYPE, PrimitiveTypeNames.FLOAT, ERelation.HAS_NO_RELATION},
                {PrimitiveTypeNames.FALSE_TYPE, PrimitiveTypeNames.FLOAT, ERelation.HAS_NO_RELATION},
                {PrimitiveTypeNames.TRUE_TYPE, PrimitiveTypeNames.FLOAT, ERelation.HAS_NO_RELATION},
                {PrimitiveTypeNames.BOOL, PrimitiveTypeNames.FLOAT, ERelation.HAS_NO_RELATION},
                {PrimitiveTypeNames.RESOURCE, PrimitiveTypeNames.FLOAT, ERelation.HAS_NO_RELATION},

                {PrimitiveTypeNames.INT, PrimitiveTypeNames.FLOAT, ERelation.HAS_NO_RELATION},
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
