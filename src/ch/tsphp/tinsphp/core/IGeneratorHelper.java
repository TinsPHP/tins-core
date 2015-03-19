/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;

public interface IGeneratorHelper
{
    IClassTypeSymbol createClass(String identifier);

    IMethodSymbol defineMethod(
            IClassTypeSymbol classTypeSymbol, String name, ITypeSymbol returnType);

    IVariableSymbol createConstant(String name, ITypeSymbol typeSymbol);

    IUnionTypeSymbol createUnionTypeSymbolFromPrimitives(String... types);
}
