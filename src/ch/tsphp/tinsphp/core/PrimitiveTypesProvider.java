/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveTypesProvider implements ITypeSymbolProvider
{

    private ISymbolFactory symbolFactory;

    public PrimitiveTypesProvider(ISymbolFactory theSymbolFactory) {
        symbolFactory = theSymbolFactory;
    }

    @Override
    public Map<String, ITypeSymbol> getTypes() {
        Map<String, ITypeSymbol> types = new HashMap<>();

        IPseudoTypeSymbol mixedTypeSymbol = symbolFactory.createPseudoTypeSymbol("mixed");
        symbolFactory.setMixedTypeSymbol(mixedTypeSymbol);

        IPseudoTypeSymbol nothingTypeSymbol = symbolFactory.createPseudoTypeSymbol("nothing");
        IPseudoTypeSymbol nullTypeTypeSymbol = symbolFactory.createPseudoTypeSymbol("nullType");


        //TODO rstoll TINS-366 scalar, num and bool cannot be own types
        IPseudoTypeSymbol scalarTypeSymbol = symbolFactory.createPseudoTypeSymbol("scalar", mixedTypeSymbol);
        IPseudoTypeSymbol numTypeSymbol = symbolFactory.createPseudoTypeSymbol("num", scalarTypeSymbol);
        IScalarTypeSymbol boolTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "bool", scalarTypeSymbol, TokenTypes.Bool, "false");

        types.put(PrimitiveTypeNames.MIXED, mixedTypeSymbol);
        types.put(PrimitiveTypeNames.SCALAR, scalarTypeSymbol);
        types.put(PrimitiveTypeNames.NUM, numTypeSymbol);
        types.put(PrimitiveTypeNames.BOOL, boolTypeSymbol);
        types.put(PrimitiveTypeNames.NULL_TYPE, nullTypeTypeSymbol);
        types.put(PrimitiveTypeNames.NOTHING, nothingTypeSymbol);


        IScalarTypeSymbol falseTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "falseType", boolTypeSymbol, TokenTypes.False, "false");
        IScalarTypeSymbol trueTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "trueType", boolTypeSymbol, TokenTypes.False, "true");

        IScalarTypeSymbol intTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "int", numTypeSymbol, TokenTypes.Int, "0");

        IScalarTypeSymbol floatTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "float", numTypeSymbol, TokenTypes.Float, "0.0");

        IScalarTypeSymbol stringTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "string", scalarTypeSymbol, TokenTypes.String, "''");

        types.put(PrimitiveTypeNames.FALSE_TYPE, falseTypeSymbol);
        types.put(PrimitiveTypeNames.TRUE_TYPE, trueTypeSymbol);
        types.put(PrimitiveTypeNames.BOOL, boolTypeSymbol);
        types.put(PrimitiveTypeNames.INT, intTypeSymbol);
        types.put(PrimitiveTypeNames.FLOAT, floatTypeSymbol);
        types.put(PrimitiveTypeNames.STRING, stringTypeSymbol);


        IArrayTypeSymbol arrayTypeSymbol = symbolFactory.createArrayTypeSymbol(
                "array", scalarTypeSymbol, mixedTypeSymbol);
        IPseudoTypeSymbol resourceTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource");

        types.put(PrimitiveTypeNames.ARRAY, arrayTypeSymbol);
        types.put(PrimitiveTypeNames.RESOURCE, resourceTypeSymbol);


        return types;
    }

}
