/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.gen;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ch.tsphp.tinsphp.common.TinsPHPConstants.RETURN_VARIABLE_NAME;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_EXPR;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RETURN;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_EXPR;

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
        IFunctionType function;
        IBindingCollection collection;
        IVariableSymbol constant;
        IMinimalMethodSymbol methodSymbol;

        methodSymbol = symbolFactory.createMinimalMethodSymbol("strpos");
        //int -> int
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.intTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        //float -> float
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.floatTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.floatTypeSymbol);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        //{as T} -> T \ T <: num
        collection = symbolFactory.createBindingCollection();
        collection.addVariable(VAR_EXPR, fixReference(T_EXPR));
        collection.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));
        collection.addUpperTypeBound(T_RETURN, std.numTypeSymbol);
        IConvertibleTypeSymbol asTreturn = symbolFactory.createConvertibleTypeSymbol();
        collection.bind(asTreturn, Arrays.asList(T_RETURN));
        collection.addUpperTypeBound(T_EXPR, asTreturn);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        Set<String> nonFixedTypeParameters = new HashSet<>(1);
        nonFixedTypeParameters.add(T_RETURN);
        function.manuallySimplified(nonFixedTypeParameters, 0, true);
        methodSymbol.addOverload(function);
        //array -> falseType
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.arrayTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.falseTypeSymbol);
        function = symbolFactory.createFunctionType("abs", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\abs()", methodSymbol);

        //TODO TINS-332 introduce object pseudo type
        //(object | array | nullType | scalar) -> int
        methodSymbol = symbolFactory.createMinimalMethodSymbol("count");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("count", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\count()", methodSymbol);

        //TODO should be an intrinsic function
        methodSymbol = symbolFactory.createMinimalMethodSymbol("empty");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("empty", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\empty()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("in_array");
        //mixed x array -> bool
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.mixedTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.arrayTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("in_array", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\in_array()", methodSymbol);

        //TODO should be an intrinsic function
        methodSymbol = symbolFactory.createMinimalMethodSymbol("isset");
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.mixedTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.boolTypeSymbol);
        function = symbolFactory.createFunctionType("isset", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\isset()", methodSymbol);

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

        //void -> string
        methodSymbol = symbolFactory.createMinimalMethodSymbol("microtime");
        collection = symbolFactory.createBindingCollection();
        collection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        collection.addLowerTypeBound(T_RETURN, std.floatTypeSymbol);
        function = symbolFactory.createFunctionType("microtime", collection, new ArrayList<IVariable>(0));
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\microtime()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("rand");
        //int x int -> int
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.intTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.intTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("rand", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        //{as int} x {as int} -> int
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.asIntTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.asIntTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("rand", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, true);
        methodSymbol.addOverload(function);
        symbols.put("\\rand()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("rtrim");
        //string -> string
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.stringTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.stringTypeSymbol);
        function = symbolFactory.createFunctionType("rtrim", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\rtrim()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("srand");
        //int -> nullType
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.intTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.nullTypeSymbol);
        function = symbolFactory.createFunctionType("srand", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        //{as int} -> nullType
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.asIntTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.nullTypeSymbol);
        function = symbolFactory.createFunctionType("srand", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, true);
        methodSymbol.addOverload(function);
        symbols.put("\\srand()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("str_replace");
        IUnionTypeSymbol arrayOrString = symbolFactory.createUnionTypeSymbol();
        arrayOrString.addTypeSymbol(std.arrayTypeSymbol);
        arrayOrString.addTypeSymbol(std.stringTypeSymbol);
        //(array | string) x (array | string) x (array | string) -> (array | string)
        collection = symbolFactory.createBindingCollection();
        collection.addVariable("$search", fixReference("T1"));
        collection.addVariable("$replace", fixReference("T2"));
        collection.addVariable("$subject", fixReference("T3"));
        collection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        collection.addUpperTypeBound("T1", arrayOrString);
        collection.addUpperTypeBound("T2", arrayOrString);
        collection.addUpperTypeBound("T3", arrayOrString);
        collection.addLowerTypeBound(T_RETURN, arrayOrString);
        IVariable search = symbolFactory.createVariable("$search");
        IVariable replace = symbolFactory.createVariable("$replace");
        IVariable subject = symbolFactory.createVariable("$subject");
        function = symbolFactory.createFunctionType("str_replace", collection, Arrays.asList(search, replace, subject));
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\str_replace()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("str_split");
        IUnionTypeSymbol arrayOrFalse = symbolFactory.createUnionTypeSymbol();
        arrayOrFalse.addTypeSymbol(std.arrayTypeSymbol);
        arrayOrFalse.addTypeSymbol(std.falseTypeSymbol);
        //string x int -> (falseType | array)
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.stringTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.intTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, arrayOrFalse);
        function = symbolFactory.createFunctionType("str_split", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        IUnionTypeSymbol arrayOrFalseOrNull = symbolFactory.createUnionTypeSymbol();
        arrayOrFalseOrNull.addTypeSymbol(std.arrayTypeSymbol);
        arrayOrFalseOrNull.addTypeSymbol(std.falseTypeSymbol);
        arrayOrFalseOrNull.addTypeSymbol(std.nullTypeSymbol);
        //{as string} x {as int} -> (falseType | array | nullType)
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.asStringTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.asIntTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, arrayOrFalseOrNull);
        function = symbolFactory.createFunctionType("str_split", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\str_split()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("strlen");
        //string -> int
        collection = createFixUnaryBindingCollection();
        collection.addUpperTypeBound(T_EXPR, std.stringTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intTypeSymbol);
        function = symbolFactory.createFunctionType("strlen", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        //{as string} -> (int | nullType)
        IUnionTypeSymbol intOrNull = symbolFactory.createUnionTypeSymbol();
        intOrNull.addTypeSymbol(std.intTypeSymbol);
        intOrNull.addTypeSymbol(std.nullTypeSymbol);
        collection.addUpperTypeBound(T_EXPR, std.asStringTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, intOrNull);
        function = symbolFactory.createFunctionType("strlen", collection, std.unaryParameterId);
        function.manuallySimplified(Collections.<String>emptySet(), 0, true);
        methodSymbol.addOverload(function);
        symbols.put("\\strlen()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("strpos");
        //string x string -> (int | false)
        collection = createFixBinaryBindingCollection();
        collection.addUpperTypeBound(T_LHS, std.stringTypeSymbol);
        collection.addUpperTypeBound(T_RHS, std.stringTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, std.intOrFalse);
        function = symbolFactory.createFunctionType("strpos", collection, std.binaryParameterIds);
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        symbols.put("\\strpos()", methodSymbol);

        methodSymbol = symbolFactory.createMinimalMethodSymbol("substr");
        IUnionTypeSymbol stringOrFalse = symbolFactory.createUnionTypeSymbol();
        stringOrFalse.addTypeSymbol(std.stringTypeSymbol);
        stringOrFalse.addTypeSymbol(std.falseTypeSymbol);
        //string x int x int -> (falseType | string)
        collection = symbolFactory.createBindingCollection();
        collection.addVariable("$string", fixReference("T1"));
        collection.addVariable("$start", fixReference("T2"));
        collection.addVariable("$length", fixReference("T3"));
        collection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        collection.addUpperTypeBound("T1", std.stringTypeSymbol);
        collection.addUpperTypeBound("T2", std.intTypeSymbol);
        collection.addUpperTypeBound("T3", std.intTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, stringOrFalse);
        IVariable var1 = symbolFactory.createVariable("$string");
        IVariable var2 = symbolFactory.createVariable("$start");
        IVariable var3 = symbolFactory.createVariable("$length");
        function = symbolFactory.createFunctionType("substr", collection, Arrays.asList(var1, var2, var3));
        function.manuallySimplified(Collections.<String>emptySet(), 0, false);
        methodSymbol.addOverload(function);
        //{as string} x {as int} x {as int} ->  (falseType | string)
        collection = symbolFactory.createBindingCollection();
        collection.addVariable("$string", fixReference("T1"));
        collection.addVariable("$start", fixReference("T2"));
        collection.addVariable("$length", fixReference("T3"));
        collection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        collection.addUpperTypeBound("T1", std.asStringTypeSymbol);
        collection.addUpperTypeBound("T2", std.asIntTypeSymbol);
        collection.addUpperTypeBound("T3", std.asIntTypeSymbol);
        collection.addLowerTypeBound(T_RETURN, stringOrFalse);
        function = symbolFactory.createFunctionType("substr", collection, Arrays.asList(var1, var2, var3));
        function.manuallySimplified(Collections.<String>emptySet(), 0, true);
        methodSymbol.addOverload(function);
        symbols.put("\\substr()", methodSymbol);

        constant = generatorHelper.createConstant("E_ALL#", std.intTypeSymbol);
        symbols.put("\\E_ALL#", constant);

        IClassTypeSymbol _exception = generatorHelper.createClass("Exception");
        arrayOrFalseOrNull = generatorHelper.createUnionTypeSymbolFromPrimitives(
                PrimitiveTypeNames.STRING,
                PrimitiveTypeNames.NULL_TYPE);

        generatorHelper.defineMethod(_exception, "getMessage()", arrayOrFalseOrNull);
        symbols.put("\\Exception", _exception);

        IClassTypeSymbol _errorException = generatorHelper.createClass("ErrorException");
        _errorException.setParent(_exception);
        _errorException.addParentTypeSymbol(_exception);
        symbols.put("\\ErrorException", _errorException);
        return symbols;
    }
}

