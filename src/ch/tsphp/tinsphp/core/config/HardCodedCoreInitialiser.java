/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.config;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.config.ICoreInitialiser;
import ch.tsphp.tinsphp.common.config.ISymbolsInitialiser;
import ch.tsphp.tinsphp.common.resolving.ISymbolResolver;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.core.BuiltInSuperGlobalSymbolsProvider;
import ch.tsphp.tinsphp.core.ConversionsProvider;
import ch.tsphp.tinsphp.core.Core;
import ch.tsphp.tinsphp.core.CoreSymbolResolver;
import ch.tsphp.tinsphp.core.GeneratorHelper;
import ch.tsphp.tinsphp.core.IConversionsProvider;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.ISymbolProvider;
import ch.tsphp.tinsphp.core.OperatorProvider;
import ch.tsphp.tinsphp.core.PrimitiveTypesProvider;
import ch.tsphp.tinsphp.core.StandardConstraintAndVariables;
import ch.tsphp.tinsphp.core.gen.BuiltInSymbolsProvider;

import java.util.Map;

public class HardCodedCoreInitialiser implements ICoreInitialiser
{
    private final ISymbolResolver coreSymbolResolver;
    private final ICore core;


    public HardCodedCoreInitialiser(IAstHelper astHelper, ISymbolsInitialiser symbolsInitialiser) {
        ISymbolFactory symbolFactory = symbolsInitialiser.getSymbolFactory();
        IOverloadResolver overloadResolver = symbolsInitialiser.getOverloadResolver();

        Map<String, ITypeSymbol> primitiveTypes = new PrimitiveTypesProvider(symbolFactory).getTypes();
        symbolFactory.setMixedTypeSymbol(primitiveTypes.get(PrimitiveTypeNames.MIXED));

        IGeneratorHelper generatorHelper = new GeneratorHelper(astHelper, symbolFactory, primitiveTypes);
        StandardConstraintAndVariables std = new StandardConstraintAndVariables(symbolFactory, primitiveTypes);


        ISymbolProvider builtInSymbolProvider = new BuiltInSymbolsProvider(
                generatorHelper, symbolFactory, overloadResolver, std);
        ISymbolProvider superGlobalSymbolResolver = new BuiltInSuperGlobalSymbolsProvider(
                astHelper, symbolFactory, primitiveTypes);

        coreSymbolResolver = new CoreSymbolResolver(
                builtInSymbolProvider.getSymbols(), superGlobalSymbolResolver.getSymbols());

        IConversionsProvider conversionProvider = new ConversionsProvider(primitiveTypes);
        IOperatorsProvider operatorsProvider = new OperatorProvider(
                symbolFactory, overloadResolver, std, builtInSymbolProvider.getSymbols());

        core = new Core(primitiveTypes,
                conversionProvider.getImplicitConversions(),
                conversionProvider.getExplicitConversions(),
                operatorsProvider.getOperators());
    }

    @Override
    public ICore getCore() {
        return core;
    }

    @Override
    public ISymbolResolver getCoreSymbolResolver() {
        return coreSymbolResolver;
    }

    @Override
    public void reset() {
        //nothing to reset in the core component
    }
}
