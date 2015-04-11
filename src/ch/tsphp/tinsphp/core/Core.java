/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.resolving.ISymbolResolver;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.gen.BuiltInSymbolsProvider;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.Map;

public class Core implements ICore
{
    private final ISymbolResolver coreSymbolResolver;
    private final Map<String, ITypeSymbol> primitiveTypes;
    private final Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> implicitConversions;
    private final Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> explicitConversions;
    private final Map<Integer, IMinimalMethodSymbol> operators;

    public Core(ISymbolFactory symbolFactory, IOverloadResolver overloadResolver, IAstHelper astHelper) {
        primitiveTypes = new PrimitiveTypesProvider(symbolFactory).getTypes();

        symbolFactory.setMixedTypeSymbol(primitiveTypes.get(PrimitiveTypeNames.MIXED));

        IGeneratorHelper generatorHelper = new GeneratorHelper(astHelper, symbolFactory, primitiveTypes);
        StandardConstraintAndVariables std = new StandardConstraintAndVariables(symbolFactory, primitiveTypes);

        ISymbolProvider builtInSymbolProvider = new BuiltInSymbolsProvider(
                generatorHelper, symbolFactory, overloadResolver, std);
        ISymbolProvider superGlobalSymbolResolver = new BuiltInSuperGlobalSymbolsProvider(
                generatorHelper, symbolFactory, primitiveTypes);

        coreSymbolResolver = new CoreSymbolResolver(
                builtInSymbolProvider.getSymbols(), superGlobalSymbolResolver.getSymbols());

        IConversionsProvider conversionProvider = new ConversionsProvider(primitiveTypes);
        implicitConversions = conversionProvider.getImplicitConversions();
        explicitConversions = conversionProvider.getExplicitConversions();

        IOperatorsProvider operatorsProvider = new OperatorProvider(symbolFactory, overloadResolver, std);
        operators = operatorsProvider.getOperators();
    }

    @Override
    public Map<Integer, IMinimalMethodSymbol> getOperators() {
        return operators;
    }

    @Override
    public Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> getImplicitConversions() {
        return implicitConversions;
    }

    @Override
    public Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> getExplicitConversions() {
        return explicitConversions;
    }

    @Override
    public Map<String, ITypeSymbol> getPrimitiveTypes() {
        return primitiveTypes;
    }

    @Override
    public ISymbolResolver getCoreSymbolResolver() {
        return coreSymbolResolver;
    }


}
