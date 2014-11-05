/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.scopes.IGlobalNamespaceScope;
import ch.tsphp.tinsphp.common.symbols.INullTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;

public class Core implements ICore
{
    private final ISymbolFactory symbolFactory;
    private final IAstHelper astHelper;
    private final IGlobalNamespaceScope globalDefaultNamespace;
    private INullTypeSymbol nullTypeSymbol;

    public Core(ISymbolFactory theSymbolFactory, IAstHelper theAstHelper,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {

        symbolFactory = theSymbolFactory;
        astHelper = theAstHelper;
        globalDefaultNamespace = theGlobalDefaultNamespace;

        defineBuiltInTypes();
    }

    private void defineBuiltInTypes() {
        nullTypeSymbol = symbolFactory.createNullTypeSymbol();
        globalDefaultNamespace.define(nullTypeSymbol);
    }

    @Override
    public INullTypeSymbol getNullTypeSymbol() {
        return nullTypeSymbol;
    }
}
