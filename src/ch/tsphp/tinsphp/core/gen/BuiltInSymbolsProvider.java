/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.gen;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.core.AProvider;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import ch.tsphp.tinsphp.core.StandardConstraintAndVariables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RETURN;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RHS;

public class BuiltInSymbolsProvider extends AProvider implements ISymbolProvider
{

    private final IGeneratorHelper generatorHelper;
    private Map<String, ISymbol> builtInSymbols;

    public BuiltInSymbolsProvider(
            IGeneratorHelper theGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            ITypeHelper theTypeHelper,
            StandardConstraintAndVariables standardConstraintAndVariables) {
        super(theSymbolFactory, theTypeHelper, standardConstraintAndVariables);
        generatorHelper = theGeneratorHelper;
    }

    @Override
    public Map<String, ISymbol> getSymbols() {
        if (builtInSymbols == null) {
            builtInSymbols = createSymbols();
        }

        return builtInSymbols;
    }

    private Map<String, ISymbol> createSymbols() {
        Map<String, ISymbol> symbols = new HashMap<>();
        IUnionTypeSymbol unionTypeSymbol;
        IFunctionType function;
        IOverloadBindings collection;
        IVariableSymbol constant;
        IMinimalMethodSymbol methodSymbol;

        //string x string -> (int | false)
        collection = createFixBinaryBindings();
        collection.addUpperTypeBound(T_LHS, std.stringTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.stringTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intOrFalse);
        function = symbolFactory.createFunctionType("strpos", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol = symbolFactory.createMinimalMethodSymbol("strpos");
        methodSymbol.addOverload(function);
        symbols.put("\\strpos()", methodSymbol);

        constant = generatorHelper.createConstant("E_ALL#", std.intTypeSymbol);
        symbols.put("\\E_ALL#", constant);

        IClassTypeSymbol _exception = generatorHelper.createClass("Exception");
        unionTypeSymbol = generatorHelper.createUnionTypeSymbolFromPrimitives(
                PrimitiveTypeNames.STRING,
                PrimitiveTypeNames.NULL_TYPE);

        generatorHelper.defineMethod(_exception, "getMessage()", unionTypeSymbol);
        symbols.put("\\Exception", _exception);

        IClassTypeSymbol _errorException = generatorHelper.createClass("ErrorException");
        _errorException.setParent(_exception);
        _errorException.addParentTypeSymbol(_exception);
        symbols.put("\\ErrorException", _errorException);
        return symbols;
    }
}

