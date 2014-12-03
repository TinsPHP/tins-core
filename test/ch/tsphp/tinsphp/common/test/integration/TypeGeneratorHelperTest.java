/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.AstHelper;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.ITypeGeneratorHelper;
import ch.tsphp.tinsphp.core.TypeGeneratorHelper;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TypeGeneratorHelperTest
{
    private ITypeSymbol mixed;

    @Test
    public void createClass_Standard_NameIsPassedName() {
        String name = "foo";

        ITypeGeneratorHelper helper = createTypeGenerator();
        IClassTypeSymbol result = helper.createClass(name);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createClass_Standard_ModifiersOnlyContainsNullable() {
        String name = "foo";

        ITypeGeneratorHelper helper = createTypeGenerator();
        IClassTypeSymbol result = helper.createClass(name);

        assertThat(result.getModifiers(), containsInAnyOrder(TokenTypes.QuestionMark));
    }

    @Test
    public void createClass_Standard_EnclosingScopeIsNull() {
        String name = "foo";

        ITypeGeneratorHelper helper = createTypeGenerator();
        IClassTypeSymbol result = helper.createClass(name);

        assertThat(result.getEnclosingScope(), is(nullValue()));
    }

    @Test
    public void defineMethodWithoutParameters_Standard_NameIsPassedName() {
        String name = "foo";

        ITypeGeneratorHelper helper = createTypeGenerator();
        IMethodSymbol result = helper.defineMethodWithoutParameters(
                mock(IClassTypeSymbol.class), name, mock(ITypeSymbol.class));

        assertThat(result.getName(), is(name));
    }

    @Test
    public void defineMethodWithoutParameters_Standard_ReturnTypeIsPassedTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        ITypeGeneratorHelper helper = createTypeGenerator();
        IMethodSymbol result = helper.defineMethodWithoutParameters(
                mock(IClassTypeSymbol.class), "foo",typeSymbol);

        assertThat(result.getType(), is(typeSymbol));
    }

    @Test
    public void defineMethodWithoutParameters_Standard_DefinesSymbolInGivenClass() {
        IClassTypeSymbol classTypeSymbol = mock(IClassTypeSymbol.class);

        ITypeGeneratorHelper helper = createTypeGenerator();
        helper.defineMethodWithoutParameters(classTypeSymbol, "", mock(ITypeSymbol.class));

        verify(classTypeSymbol).define(any(ISymbol.class));
    }



    private ITypeGeneratorHelper createTypeGenerator() {
        IAstHelper astHelper = createAstHelper();
        IScopeHelper scopeHelper = createScopeHelper();
        IModifierHelper modifierHelper = createModifierHelper();
        ISymbolFactory symbolFactory = createSymbolFactory(scopeHelper, modifierHelper);
        mixed = mock(ITypeSymbol.class);
        when(mixed.getName()).thenReturn("mixed");
        symbolFactory.setMixedTypeSymbol(mixed);
        return createTypeGenerator(astHelper, symbolFactory);
    }

    private IModifierHelper createModifierHelper() {
        return new ModifierHelper();
    }

    private IScopeHelper createScopeHelper() {
        return mock(IScopeHelper.class);
    }

    private ISymbolFactory createSymbolFactory(IScopeHelper scopeHelper, IModifierHelper modifierHelper) {
        return new SymbolFactory(scopeHelper, modifierHelper);
    }

    protected IAstHelper createAstHelper() {
        return new AstHelper(new TSPHPAstAdaptor());
    }

    protected ITypeGeneratorHelper createTypeGenerator(IAstHelper astHelper, ISymbolFactory symbolFactory) {
        return new TypeGeneratorHelper(astHelper, symbolFactory);
    }

}
