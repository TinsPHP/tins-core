/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.INullTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveTypeProvider implements ITypeSymbolProvider
{

    private ISymbolFactory symbolFactory;

    public PrimitiveTypeProvider(ISymbolFactory theSymbolFactory) {
        symbolFactory = theSymbolFactory;
    }

    @Override
    public Map<String, ITypeSymbol> getTypes() {
        Map<String, ITypeSymbol> types = new HashMap<>();

        IPseudoTypeSymbol mixedTypeSymbol = symbolFactory.createPseudoTypeSymbol("mixed");
        symbolFactory.setMixedTypeSymbol(mixedTypeSymbol);
        IPseudoTypeSymbol scalarTypeSymbol = symbolFactory.createPseudoTypeSymbol("scalar", mixedTypeSymbol);
        IPseudoTypeSymbol numTypeSymbol = symbolFactory.createPseudoTypeSymbol("num", scalarTypeSymbol);
        INullTypeSymbol nullTypeSymbol = symbolFactory.createNullTypeSymbol();
        types.put(PrimitiveTypeNames.MIXED, mixedTypeSymbol);
        types.put(PrimitiveTypeNames.SCALAR, scalarTypeSymbol);
        types.put(PrimitiveTypeNames.NUM, numTypeSymbol);
        types.put(PrimitiveTypeNames.NULL, nullTypeSymbol);

        IScalarTypeSymbol boolTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "bool", scalarTypeSymbol, TokenTypes.Bool, "false");

        IScalarTypeSymbol trueTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "true", boolTypeSymbol, TokenTypes.Bool, "true");
        IScalarTypeSymbol falseTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "false", boolTypeSymbol, TokenTypes.Bool, "false");

        IScalarTypeSymbol intTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "int", numTypeSymbol, TokenTypes.Int, "0");

        IScalarTypeSymbol floatTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "float", numTypeSymbol, TokenTypes.Float, "0.0");

        IScalarTypeSymbol stringTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "string", scalarTypeSymbol, TokenTypes.String, "''");

        types.put(PrimitiveTypeNames.TRUE, trueTypeSymbol);
        types.put(PrimitiveTypeNames.FALSE, falseTypeSymbol);
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
