/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.gen;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
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

import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_EXPR;
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
        IBindingCollection collection;
        IVariableSymbol constant;
        IMinimalMethodSymbol methodSymbol;

        //string x string -> (int | false)
        methodSymbol = symbolFactory.createMinimalMethodSymbol("strpos");
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.stringTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.stringTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intOrFalse);
        function = symbolFactory.createFunctionType("strpos", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\strpos()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("strpos");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.intTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.floatTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.floatTypeSymbol);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.asIntTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\abs()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("is_array");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("is_array", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\is_array()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("is_bool");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("is_bool", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\is_bool()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("is_float");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("is_float", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\is_float()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("is_int");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("is_int", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\is_int()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("is_string");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("is_string", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\is_string()", methodSymbol);

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

