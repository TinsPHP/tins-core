/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.resolver.ISymbolResolver;
import ch.tsphp.tinsphp.core.gen.BuiltInSymbolProvider;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.Map;

public class Core implements ICore
{
    private final ISymbolResolver coreSymbolResolver;
    private final Map<String, ITypeSymbol> primitiveTypes;

    public Core(ISymbolFactory symbolFactory, IAstHelper astHelper) {
        primitiveTypes = new PrimitiveTypeProvider(symbolFactory).getTypes();

        symbolFactory.setMixedTypeSymbol(primitiveTypes.get(PrimitiveTypeNames.MIXED));

        IGeneratorHelper generatorHelper = new GeneratorHelper(astHelper, symbolFactory, primitiveTypes);

        ISymbolProvider builtInSymbolProvider = new BuiltInSymbolProvider(
                generatorHelper, symbolFactory, primitiveTypes);
        ISymbolProvider superGlobalSymbolResolver = new BuiltInSuperGlobalsProvider(
                generatorHelper, symbolFactory, primitiveTypes);

        coreSymbolResolver = new CoreSymbolResolver(
                builtInSymbolProvider.getSymbols(), superGlobalSymbolResolver.getSymbols());
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
