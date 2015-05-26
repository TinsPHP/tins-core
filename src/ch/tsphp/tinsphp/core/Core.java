/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.utils.Pair;

import java.util.Map;

public class Core implements ICore
{

    private final Map<String, ITypeSymbol> primitiveTypes;
    private final Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions;
    private final Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions;
    private final Map<Integer, IMinimalMethodSymbol> operators;

    public Core(
            Map<String, ITypeSymbol> thePrimitiveTypes,
            Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> theImplicitConversions,
            Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> theExplicitConversions,
            Map<Integer, IMinimalMethodSymbol> theOperators) {

        primitiveTypes = thePrimitiveTypes;
        implicitConversions = theImplicitConversions;
        explicitConversions = theExplicitConversions;
        operators = theOperators;
    }

    @Override
    public Map<Integer, IMinimalMethodSymbol> getOperators() {
        return operators;
    }

    @Override
    public Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> getImplicitConversions() {
        return implicitConversions;
    }

    @Override
    public Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> getExplicitConversions() {
        return explicitConversions;
    }

    @Override
    public Map<String, ITypeSymbol> getPrimitiveTypes() {
        return primitiveTypes;
    }


}
