/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.HashMap;
import java.util.Map;

public class BuiltInSuperGlobalSymbolsProvider implements ISymbolProvider
{

    private final IAstHelper astHelper;
    private final ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<String, ISymbol> builtInSuperGlobals;

    public BuiltInSuperGlobalSymbolsProvider(
            IAstHelper theAstHelper,
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        astHelper = theAstHelper;
        symbolFactory = theSymbolFactory;
        primitiveTypes = thePrimitiveType;
    }

    @Override
    public Map<String, ISymbol> getSymbols() {
        if (builtInSuperGlobals == null) {
            builtInSuperGlobals = createSymbols();
        }

        return builtInSuperGlobals;
    }

    private Map<String, ISymbol> createSymbols() {
        Map<String, ISymbol> superGlobals = new HashMap<>();

        ITSPHPAst variableId = astHelper.createAst(TokenTypes.VariableId, "$_GET");
        IMinimalVariableSymbol getVariable = symbolFactory.createMinimalVariableSymbol(variableId, "$_GET");
        getVariable.setType(primitiveTypes.get(PrimitiveTypeNames.ARRAY));
        superGlobals.put("$_GET", getVariable);

        return superGlobals;
    }
}
