/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration.testutils;

import ch.tsphp.common.AstHelper;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.config.ISymbolsInitialiser;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.core.ConversionsProvider;
import ch.tsphp.tinsphp.core.GeneratorHelper;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.PrimitiveTypesProvider;
import ch.tsphp.tinsphp.core.StandardConstraintAndVariables;
import ch.tsphp.tinsphp.core.gen.BuiltInSymbolsProvider;
import ch.tsphp.tinsphp.symbols.config.HardCodedSymbolsInitialiser;
import org.junit.Ignore;

import java.util.Map;

@Ignore
public abstract class ATest
{
    protected IAstHelper astHelper;
    protected IScopeHelper scopeHelper;
    protected IModifierHelper modifierHelper;
    protected ITypeHelper typeHelper;
    protected ISymbolFactory symbolFactory;
    protected Map<String, ITypeSymbol> primitiveTypes;
    protected StandardConstraintAndVariables std;
    protected Map<String, ISymbol> builtInSymbols;
    protected IConversionsProvider conversionsProvider;

    public ATest() {

        ISymbolsInitialiser symbolsInitialiser = createSymbolsInitialiser();

        astHelper = createAstHelper();
        scopeHelper = symbolsInitialiser.getScopeHelper();
        modifierHelper = symbolsInitialiser.getModifierHelper();
        typeHelper = symbolsInitialiser.getTypeHelper();
        symbolFactory = symbolsInitialiser.getSymbolFactory();
        primitiveTypes = getPrimitiveTypes(symbolFactory);
        std = createStandardConstraintAndVariables();
        BuiltInSymbolsProvider provider = new BuiltInSymbolsProvider(
                new GeneratorHelper(astHelper, symbolFactory, primitiveTypes),
                symbolFactory,
                typeHelper,
                std);
        builtInSymbols = provider.getSymbols();

        conversionsProvider = createConversionsProvider(primitiveTypes);
        typeHelper.setConversionsProvider(conversionsProvider);
    }

    private HardCodedSymbolsInitialiser createSymbolsInitialiser() {
        return new HardCodedSymbolsInitialiser();
    }

    private StandardConstraintAndVariables createStandardConstraintAndVariables() {
        return new StandardConstraintAndVariables(symbolFactory, primitiveTypes);
    }

    private Map<String, ITypeSymbol> getPrimitiveTypes(ISymbolFactory theSymbolFactory) {
        return new PrimitiveTypesProvider(theSymbolFactory).getTypes();
    }

    protected IAstHelper createAstHelper() {
        return new AstHelper(new TSPHPAstAdaptor());
    }

    protected IGeneratorHelper createGenerator(
            IAstHelper astHelper, ISymbolFactory symbolFactory, Map<String, ITypeSymbol> primitiveTypes) {
        return new GeneratorHelper(astHelper, symbolFactory, primitiveTypes);
    }

    protected IConversionsProvider createConversionsProvider(Map<String, ITypeSymbol> primitiveTypes) {
        return new ConversionsProvider(primitiveTypes);
    }
}
