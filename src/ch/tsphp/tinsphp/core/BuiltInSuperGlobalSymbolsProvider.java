/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.HashMap;
import java.util.Map;

public class BuiltInSuperGlobalSymbolsProvider implements ISymbolProvider
{

    private final IGeneratorHelper generatorHelper;
    private final ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<String, ISymbol> builtInSuperGlobals;

    public BuiltInSuperGlobalSymbolsProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        generatorHelper = theGeneratorHelper;
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

        //CHECKSTYLE:OFF:LocalVariableName
        IArrayTypeSymbol $_GET = symbolFactory.createArrayTypeSymbol(
                "$_GET", primitiveTypes.get(PrimitiveTypeNames.STRING), primitiveTypes.get(PrimitiveTypeNames.MIXED));
        superGlobals.put("$_GET", $_GET);
        //CHECKSTYLE:ON:LocalVariableName

        return superGlobals;
    }
}
