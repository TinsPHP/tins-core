/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.gen;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.BoundException;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.core.AProvider;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.constraints.TypeVariableCollection;

import java.util.HashMap;
import java.util.Map;

public class BuiltInSymbolsProvider extends AProvider implements ISymbolProvider
{

    private final IGeneratorHelper generatorHelper;
    private Map<String, ISymbol> builtInSymbols;

    public BuiltInSymbolsProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            Map<String, ITypeSymbol> thePrimitiveType) {
        super(theSymbolFactory, theOverloadResolver, thePrimitiveType);
        generatorHelper = theGeneratorHelper;
    }

    @Override
    public Map<String, ISymbol> getSymbols() {
        if (builtInSymbols == null) {
            try {
                builtInSymbols = createSymbols();
            } catch (BoundException ex) {
                //should not happen, turn it into a runtime exception
                throw new RuntimeException(ex);
            }
        }

        return builtInSymbols;
    }

    private Map<String, ISymbol> createSymbols() throws BoundException {
        Map<String, ISymbol> symbols = new HashMap<>();
        IUnionTypeSymbol unionTypeSymbol;
        IFunctionTypeSymbol function;
        IVariableSymbol constant;

        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addUpperBound(T_LHS, stringTypeConstraint);
        collection.addLowerBound(T_RETURN, intOrFalseTypeConstraint);
        function = symbolFactory.createFunctionTypeSymbol(
                "strpos", collection, binaryParameterIds, T_RETURN);

        symbols.put("\\strpos()", function);

        constant = generatorHelper.createConstant("E_ALL#", intTypeSymbol);
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
        symbols.put("\\ErrorException", _errorException);
        return symbols;
    }
}

