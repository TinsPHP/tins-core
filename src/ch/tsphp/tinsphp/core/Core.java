/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.INullTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.resolver.ISymbolResolver;
import ch.tsphp.tinsphp.common.symbols.resolver.ITypeSymbolResolver;
import ch.tsphp.tinsphp.core.gen.BuiltInTypesProvider;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.HashMap;
import java.util.Map;

public class Core implements ICore
{
    private final INullTypeSymbol nullTypeSymbol;
    private final ITypeSymbolResolver coreTypeSymbolResolver;
    private final ISymbolResolver coreSymbolResolver;

    public Core(ISymbolFactory symbolFactory, IAstHelper astHelper) {

        ITypeProvider primitiveTypeProvider = new PrimitiveTypeProvider(symbolFactory);
        Map<String, ITypeSymbol> primitiveTypes = primitiveTypeProvider.getTypes();
        nullTypeSymbol = (INullTypeSymbol) primitiveTypes.get(PrimitiveTypeNames.TYPE_NAME_NULL);

        ITypeProvider builtInTypeProvider = new BuiltInTypesProvider(
                new TypeGeneratorHelper(astHelper, symbolFactory),
                symbolFactory,
                primitiveTypes
        );

        coreTypeSymbolResolver = new CoreTypeSymbolResolver(primitiveTypeProvider, builtInTypeProvider);
        coreSymbolResolver = new CoreSymbolResolver(new HashMap<String, ISymbol>(), new HashMap<String, ISymbol>());
    }

    @Override
    public ISymbolResolver getCoreSymbolResolver() {
        return coreSymbolResolver;
    }

    @Override
    public ITypeSymbolResolver getCoreTypeSymbolResolver() {
        return coreTypeSymbolResolver;
    }

    @Override
    public INullTypeSymbol getNullTypeSymbol() {
        return nullTypeSymbol;
    }
}
