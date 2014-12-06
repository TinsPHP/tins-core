/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;

import java.util.Map;

public interface IConversionsProvider
{
    Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> getImplicitConversions();

    Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> getExplicitConversions();
}
