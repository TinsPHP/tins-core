/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.scopes.IConditionalScope;
import ch.tsphp.tinsphp.common.scopes.INamespaceScope;
import ch.tsphp.tinsphp.common.symbols.resolver.ISymbolResolver;
import ch.tsphp.tinsphp.core.CoreSymbolResolver;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CoreSymbolResolverTest
{
    @Test
    public void resolveIdentifierFromItsScope_NonExistingNotInNamespace_DoesNotResolveAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verifyZeroInteractions(scope);
        try {
            verify(predefinedSymbols).get(anyString());
            fail("should not call predefinedSymbols.get()");
        } catch (MockitoAssertionError ex) {
            //that's ok should throw
        }
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsScope_ExistingInNamespace_UsesPredefinedSymbolsAndReturnsSymbol() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put(scopeName + identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verify(predefinedSymbols).get(scopeName + identifier);
        assertThat(result, is(symbol));
    }


    @Test
    public void resolveIdentifierFromItsScope_ExistingInNamespaceWrongCase_UsesPredefinedSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put(scopeName + "dummy", symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verify(predefinedSymbols).get(scopeName + identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsScope_NonExistingInNamespace_UsesPredefinedSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verify(predefinedSymbols).get(scopeName + identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsScope_ExistingVariable_UsesSuperGlobalsAndReturnsVariable() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getType()).thenReturn(TokenTypes.VariableId);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> superGlobals = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        superGlobals.put(identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlySuperGlobals(superGlobals);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verify(superGlobals).get(identifier);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveIdentifierFromItsScope_ExistingVariableWrongCase_UsesSuperGlobalsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getType()).thenReturn(TokenTypes.VariableId);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> superGlobals = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        superGlobals.put("dummy", symbol);

        ISymbolResolver resolver = createSymbolResolverOnlySuperGlobals(superGlobals);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verify(superGlobals).get(identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsScope_NonExistingVariable_UsesSuperGlobalsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getType()).thenReturn(TokenTypes.VariableId);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> superGlobals = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlySuperGlobals(superGlobals);
        ISymbol result = resolver.resolveIdentifierFromItsScope(ast);

        verify(ast).getScope();
        verify(superGlobals).get(identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsScopeCaseInsensitive_NonExistingNotInNamespace_DoesNotResolveAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verifyZeroInteractions(scope);
        try {
            verify(predefinedSymbols).get(anyString());
            fail("should not call predefinedSymbols.get()");
        } catch (MockitoAssertionError ex) {
            //that's ok should throw
        }
        assertThat(result, is(nullValue()));
    }

    @Test
    public void
    resolveIdentifierFromItsScopeCaseInsensitive_ExistingInNamespace_UsesPredefinedSymbolsAndReturnsSymbol() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put(scopeName + identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verify(scope).getScopeName();
        verify(ast).getText();
        assertThat(result, is(symbol));
    }


    @Test
    public void
    resolveIdentifierFromItsScopeCaseInsensitive_ExistingInNamespaceWrongCase_UsesPredefinedSymbolsAndReturnsSymbol() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put(scopeName + "dummy", symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verify(scope).getScopeName();
        verify(ast).getText();
        assertThat(result, is(symbol));
    }

    @Test
    public void
    resolveIdentifierFromItsScopeCaseInsensitive_NonExistingInNamespace_UsesPredefinedSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verify(scope).getScopeName();
        verify(ast).getText();
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsScopeCaseInsensitive_ExistingVariable_UsesSuperGlobalsAndReturnsVariable() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getType()).thenReturn(TokenTypes.VariableId);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> superGlobals = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        superGlobals.put(identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlySuperGlobals(superGlobals);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verify(ast).getText();
        assertThat(result, is(symbol));
    }

    @Test
    public void
    resolveIdentifierFromItsScopeCaseInsensitive_ExistingVariableWrongCase_UsesSuperGlobalsAndReturnsSymbol() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getType()).thenReturn(TokenTypes.VariableId);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> superGlobals = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        superGlobals.put("dummy", symbol);

        ISymbolResolver resolver = createSymbolResolverOnlySuperGlobals(superGlobals);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verify(ast).getText();
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveIdentifierFromItsScopeCaseInsensitive_NonExistingVariable_UsesSuperGlobalsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getType()).thenReturn(TokenTypes.VariableId);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        IScope scope = mock(IScope.class);
        when(ast.getScope()).thenReturn(scope);
        Map<String, ISymbol> superGlobals = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlySuperGlobals(superGlobals);
        ISymbol result = resolver.resolveIdentifierFromItsScopeCaseInsensitive(ast);

        verify(ast).getScope();
        verify(ast).getText();
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromFallback_NonExisting_DelegatesToSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromFallback(ast);

        verify(predefinedSymbols).get("\\" + identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromFallback_Existing_DelegatesToSymbolsAndReturnsSymbols() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put("\\" + identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromFallback(ast);

        verify(predefinedSymbols).get("\\" + identifier);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveAbsoluteIdentifier_NonExisting_DelegatesToSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "\\Dummy";
        when(ast.getText()).thenReturn(identifier);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveAbsoluteIdentifier(ast);

        verify(predefinedSymbols).get(identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveAbsoluteIdentifier_Existing_DelegatesToSymbolsAndReturnsSymbol() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "\\Dummy";
        when(ast.getText()).thenReturn(identifier);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put(identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveAbsoluteIdentifier(ast);

        verify(predefinedSymbols).get(identifier);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveIdentifierFromItsNamespaceScope_NonExisting_DelegatesToSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());
        ISymbol symbol = mock(ISymbol.class);
        predefinedSymbols.put(scopeName+identifier, symbol);

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsNamespaceScope(ast);

        verify(predefinedSymbols).get(scopeName + identifier);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveIdentifierFromItsNamespaceScope_Existing_DelegatesToSymbolsAndReturnsSymbol() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(ast.getScope()).thenReturn(scope);
        String scopeName = "\\ch\\tsphp\\";
        when(scope.getScopeName()).thenReturn(scopeName);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsNamespaceScope(ast);

        verify(predefinedSymbols).get(scopeName + identifier);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsNamespaceScope_ScopeIsNull_DoesNotDelegateToSymbolsAndReturnsNull() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String identifier = "Dummy";
        when(ast.getText()).thenReturn(identifier);
        when(ast.getScope()).thenReturn(null);
        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsNamespaceScope(ast);

        try {
            verify(predefinedSymbols).get(anyString());
            fail("should not call predefinedSymbols.get()");
        } catch (MockitoAssertionError ex) {
            //that's good
        }
        assertThat(result, is(nullValue()));
    }

    @Test
    public void resolveIdentifierFromItsNamespaceScope_InConditionalScope_GetEnclosingNamespaceScope() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        IConditionalScope conditionalScope = mock(IConditionalScope.class);
        when(ast.getScope()).thenReturn(conditionalScope);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(conditionalScope.getEnclosingScope()).thenReturn(scope);

        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsNamespaceScope(ast);

        verify(conditionalScope).getEnclosingScope();
        assertThat(result, is(nullValue()));
    }

    @Test
    public void
    resolveIdentifierFromItsNamespaceScope_InConditionalScopeInConditionalScope_GetEnclosingNamespaceScope() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        IConditionalScope conditionalScope1 = mock(IConditionalScope.class);
        when(ast.getScope()).thenReturn(conditionalScope1);
        IConditionalScope conditionalScope2 = mock(IConditionalScope.class);
        when(conditionalScope1.getEnclosingScope()).thenReturn(conditionalScope2);
        INamespaceScope scope = mock(INamespaceScope.class);
        when(conditionalScope2.getEnclosingScope()).thenReturn(scope);

        Map<String, ISymbol> predefinedSymbols = spy(new HashMap<String, ISymbol>());

        ISymbolResolver resolver = createSymbolResolverOnlyPredefined(predefinedSymbols);
        ISymbol result = resolver.resolveIdentifierFromItsNamespaceScope(ast);

        verify(conditionalScope1).getEnclosingScope();
        verify(conditionalScope2).getEnclosingScope();
        assertThat(result, is(nullValue()));
    }

    private ISymbolResolver createSymbolResolverOnlyPredefined(Map<String, ISymbol> predefinedSymbols) {
        return createSymbolResolver(predefinedSymbols, new HashMap<String, ISymbol>());
    }

    private ISymbolResolver createSymbolResolverOnlySuperGlobals(Map<String, ISymbol> predefinedSuperGlobals) {
        return createSymbolResolver(new HashMap<String, ISymbol>(), predefinedSuperGlobals);
    }

    protected ISymbolResolver createSymbolResolver(
            Map<String, ISymbol> predefinedSymbols, Map<String, ISymbol> predefinedSuperGlobals) {
        return new CoreSymbolResolver(predefinedSymbols, predefinedSuperGlobals);
    }
}
