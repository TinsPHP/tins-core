/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.gen;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.HashMap;
import java.util.Map;

public class BuiltInSymbolProvider implements ISymbolProvider
{

    private final IGeneratorHelper generatorHelper;
    private final ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;

    public BuiltInSymbolProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        generatorHelper = theGeneratorHelper;
        symbolFactory = theSymbolFactory;
        primitiveTypes = thePrimitiveType;
    }


    @Override
    public Map<String, ISymbol> getSymbols() {
        Map<String, ISymbol> symbols = new HashMap<>();
        IUnionTypeSymbol unionTypeSymbol;
        IMethodSymbol methodSymbol;
        IVariableSymbol constant;

        unionTypeSymbol = generatorHelper.createUnionTypeSymbolFromPrimitives(
                PrimitiveTypeNames.INT,
                PrimitiveTypeNames.FALSE);
        methodSymbol = generatorHelper.createFunction("strpos", unionTypeSymbol);
        symbols.put("\\strpos()", methodSymbol);

        constant = generatorHelper.createConstant("E_ALL#", primitiveTypes.get(PrimitiveTypeNames.INT));
        symbols.put("\\E_ALL#", constant);

        IClassTypeSymbol _exception = generatorHelper.createClass("Exception");
        unionTypeSymbol = generatorHelper.createUnionTypeSymbolFromPrimitives(
                PrimitiveTypeNames.STRING,
                PrimitiveTypeNames.NULL);

        generatorHelper.defineMethod(_exception, "getMessage()", unionTypeSymbol);
        symbols.put("\\Exception", _exception);

        IClassTypeSymbol _errorException = generatorHelper.createClass("ErrorException");
        _errorException.setParent(_exception);
        _errorException.addParentTypeSymbol(_exception);
        symbols.put("\\ErrorException", _exception);

        return symbols;
    }
}

