/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration.testutils;

import ch.tsphp.common.AstHelper;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.core.GeneratorHelper;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.PrimitiveTypesProvider;
import ch.tsphp.tinsphp.core.StandardConstraintAndVariables;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Ignore;

import java.util.Map;

import static org.mockito.Mockito.mock;

@Ignore
public abstract class ATest
{
    protected IAstHelper astHelper;
    protected IScopeHelper scopeHelper;
    protected IModifierHelper modifierHelper;
    protected IOverloadResolver overloadResolver;
    protected ISymbolFactory symbolFactory;
    protected Map<String, ITypeSymbol> primitiveTypes;
    protected StandardConstraintAndVariables std;

    public ATest() {
        astHelper = createAstHelper();
        scopeHelper = createScopeHelper();
        modifierHelper = createModifierHelper();
        overloadResolver = createOverloadResolver();
        symbolFactory = createSymbolFactory(scopeHelper, modifierHelper, overloadResolver);
        primitiveTypes = getPrimitiveTypes(symbolFactory);
        std = createStandardConstraintAndVariables();
    }

    private StandardConstraintAndVariables createStandardConstraintAndVariables() {
        return new StandardConstraintAndVariables(symbolFactory, primitiveTypes);
    }

    private Map<String, ITypeSymbol> getPrimitiveTypes(ISymbolFactory theSymbolFactory) {
        return new PrimitiveTypesProvider(theSymbolFactory).getTypes();
    }

    protected IScopeHelper createScopeHelper() {
        return mock(IScopeHelper.class);
    }

    protected IModifierHelper createModifierHelper() {
        return new ModifierHelper();
    }

    protected IOverloadResolver createOverloadResolver() {
        return new OverloadResolver();
    }

    protected ISymbolFactory createSymbolFactory(
            IScopeHelper scopeHelper, IModifierHelper modifierHelper, IOverloadResolver overloadResolver) {
        return new SymbolFactory(scopeHelper, modifierHelper, overloadResolver);
    }

    protected IAstHelper createAstHelper() {
        return new AstHelper(new TSPHPAstAdaptor());
    }

    protected IGeneratorHelper createGenerator(
            IAstHelper astHelper, ISymbolFactory symbolFactory, Map<String, ITypeSymbol> primitiveTypes) {
        return new GeneratorHelper(astHelper, symbolFactory, primitiveTypes);
    }

}
