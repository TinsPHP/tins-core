/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.resolver.ITypeSymbolResolver;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

public class CoreTypeSymbolResolver implements ITypeSymbolResolver
{
    private final ITypeProvider primitiveTypeProvider;
    private final ITypeProvider builtInTypeProvider;

    private ITypeSymbolResolver nextSymbolResolver;

    public CoreTypeSymbolResolver(ITypeProvider thePrimitiveTypeProvider, ITypeProvider theBuiltInTypeProvier) {
        primitiveTypeProvider = thePrimitiveTypeProvider;
        builtInTypeProvider = theBuiltInTypeProvier;
    }

    @Override
    public ITypeSymbol resolveTypeFor(ITSPHPAst ast) {
        ITypeSymbol typeSymbol;
        switch (ast.getType()) {
            case TokenTypes.METHOD_DECLARATION:
                typeSymbol = null;
                break;
            case TokenTypes.TypeBool:
            case TokenTypes.Bool:
                typeSymbol = primitiveTypeProvider.getTypes().get(PrimitiveTypeNames.TYPE_NAME_BOOL);
                break;
            default:
                typeSymbol = null;
                break;
        }
        if (typeSymbol == null && nextSymbolResolver != null) {
            typeSymbol = nextSymbolResolver.resolveTypeFor(ast);
        }
        return typeSymbol;
    }

    @Override
    public void setNextInChain(ITypeSymbolResolver next) {
        nextSymbolResolver = next;
    }
}
