/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;

import java.util.Map;

public class Core implements ICore
{

    private final Map<String, ITypeSymbol> primitiveTypes;
    private final Map<Integer, IMinimalMethodSymbol> operators;

    public Core(Map<String, ITypeSymbol> thePrimitiveTypes, Map<Integer, IMinimalMethodSymbol> theOperators) {
        primitiveTypes = thePrimitiveTypes;
        operators = theOperators;
    }

    @Override
    public Map<Integer, IMinimalMethodSymbol> getOperators() {
        return operators;
    }

    @Override
    public Map<String, ITypeSymbol> getPrimitiveTypes() {
        return primitiveTypes;
    }

}
