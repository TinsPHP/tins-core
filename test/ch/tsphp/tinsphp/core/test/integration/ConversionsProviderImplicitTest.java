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
public class ConversionsProviderImplicitTest extends AConversionsProvider
{

    public ConversionsProviderImplicitTest(String fromType, String toType, boolean result) {
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
    protected IConversionsProvider createConversionsProvider(Map<String, ITypeSymbol> types) {
        IConversionsProvider conversionsProvider1 = super.createConversionsProvider(types);
        IConversionsProvider conversionsProvider = mock(IConversionsProvider.class);
        when(conversionsProvider.getExplicitConversions()).thenReturn(
                new HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>>());
        when(conversionsProvider.getImplicitConversions()).thenReturn(conversionsProvider1.getImplicitConversions());
        return conversionsProvider;
    }
}
