/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.scopes.INamespaceScope;
import ch.tsphp.tinsphp.common.symbols.resolver.ISymbolResolver;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Map;

public class CoreSymbolResolver implements ISymbolResolver
{
    private final Map<String, ISymbol> symbols;
    private final ILowerCaseStringMap<ISymbol> symbolsCaseInsensitive;
    private final Map<String, ISymbol> superGlobals;
    private final ILowerCaseStringMap<ISymbol> superGlobalsCaseInsensitive;

    public CoreSymbolResolver(Map<String, ISymbol> predefinedSymbols, Map<String, ISymbol> predefinedSuperGlobals) {
        symbols = predefinedSymbols;
        superGlobals = predefinedSuperGlobals;

        symbolsCaseInsensitive = new LowerCaseStringMap<>();
        symbolsCaseInsensitive.putAll(predefinedSymbols);
        superGlobalsCaseInsensitive = new LowerCaseStringMap<>();
        superGlobalsCaseInsensitive.putAll(predefinedSuperGlobals);
    }

    @Override
    public ISymbol resolveIdentifierFromItsScope(ITSPHPAst identifier) {
        ISymbol symbol = null;
        IScope scope = identifier.getScope();
        if (scope instanceof INamespaceScope) {
            String typeName = scope.getScopeName() + identifier.getText();
            symbol = symbols.get(typeName);
        } else if (identifier.getType() == TokenTypes.VariableId) {
            symbol = superGlobals.get(identifier.getText());
        }
        return symbol;
    }

    @Override
    public ISymbol resolveIdentifierFromItsScopeCaseInsensitive(ITSPHPAst identifier) {
        ISymbol symbol = null;
        IScope scope = identifier.getScope();
        if (scope instanceof INamespaceScope) {
            String typeName = scope.getScopeName() + identifier.getText();
            symbol = symbolsCaseInsensitive.get(typeName);
        } else if (identifier.getType() == TokenTypes.VariableId) {
            symbol = superGlobalsCaseInsensitive.get(identifier.getText());
        }
        return symbol;
    }

    @Override
    public ISymbol resolveIdentifierFromFallback(ITSPHPAst identifier) {
        return symbols.get("\\" + identifier.getText());
    }

    @Override
    public ISymbol resolveAbsoluteIdentifier(ITSPHPAst identifier) {
        return symbols.get(identifier.getText());
    }

    @Override
    public ISymbol resolveIdentifierFromItsNamespaceScope(ITSPHPAst identifier) {
        ISymbol symbol = null;
        INamespaceScope scope = getEnclosingNamespaceScope(identifier);
        if (scope != null) {
            symbol = symbols.get(scope.getScopeName() + identifier.getText());
        }
        return symbol;
    }

    //Warning! start code duplication - same as in ScopeHelper in inference engine component
    private INamespaceScope getEnclosingNamespaceScope(ITSPHPAst ast) {
        INamespaceScope namespaceScope = null;

        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof INamespaceScope)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            namespaceScope = (INamespaceScope) scope;
        }
        return namespaceScope;
    }
    //Warning! end code duplication - same as in ScopeHelper in inference engine component
}
