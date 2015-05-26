/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration.testutils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.config.ISymbolsInitialiser;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.core.ConversionsProvider;
import ch.tsphp.tinsphp.core.IConversionsProvider;
import ch.tsphp.tinsphp.core.ITypeSymbolProvider;
import ch.tsphp.tinsphp.core.PrimitiveTypesProvider;
import ch.tsphp.tinsphp.symbols.config.HardCodedSymbolsInitialiser;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.util.Map;

import static org.hamcrest.CoreMatchers.describedAs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public abstract class AConversionProviderTest
{
    private static ISymbolsInitialiser symbolsInitialiser;

    @BeforeClass
    public static void init() {
        symbolsInitialiser = new HardCodedSymbolsInitialiser();
    }

    private final String from;
    private final String to;
    private final boolean expectedResult;

    public AConversionProviderTest(String fromType, String toType, boolean result) {
        from = fromType;
        to = toType;
        expectedResult = result;
    }


    public void run() {
        ISymbolFactory symbolFactory = symbolsInitialiser.getSymbolFactory();
        ITypeHelper typeHelper = symbolsInitialiser.getTypeHelper();
        ITypeSymbolProvider primitiveTypesProvider = new PrimitiveTypesProvider(symbolFactory);
        Map<String, ITypeSymbol> types = primitiveTypesProvider.getTypes();
        ITypeSymbol actual = types.get(from);
        IConvertibleTypeSymbol formal = symbolFactory.createConvertibleTypeSymbol();
        ITypeSymbol toType = types.get(to);
        formal.addLowerTypeBound(toType);
        formal.addUpperTypeBound(toType);

        IConversionsProvider provider = createConversionsProvider(types);
        typeHelper.setCore(createCore(provider));
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, describedAs(from + " <: {as " + to + "} to be <" + String.valueOf(expectedResult) + ">",
                is(expectedResult)));
    }

    protected abstract ICore createCore(IConversionsProvider provider);

    protected ConversionsProvider createConversionsProvider(Map<String, ITypeSymbol> types) {
        return new ConversionsProvider(types);
    }

}
