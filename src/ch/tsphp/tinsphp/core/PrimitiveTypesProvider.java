/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;

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

        types.put(PrimitiveTypeNames.MIXED, mixedTypeSymbol);
        types.put(PrimitiveTypeNames.NULL_TYPE, nullTypeTypeSymbol);
        types.put(PrimitiveTypeNames.NOTHING, nothingTypeSymbol);

        IScalarTypeSymbol falseTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "falseType", mixedTypeSymbol, TokenTypes.False, "false");
        IScalarTypeSymbol trueTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "trueType", mixedTypeSymbol, TokenTypes.False, "true");

        IScalarTypeSymbol intTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "int", mixedTypeSymbol, TokenTypes.Int, "0");

        IScalarTypeSymbol floatTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "float", mixedTypeSymbol, TokenTypes.Float, "0.0");

        IScalarTypeSymbol stringTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "string", mixedTypeSymbol, TokenTypes.String, "''");

        types.put(PrimitiveTypeNames.FALSE_TYPE, falseTypeSymbol);
        types.put(PrimitiveTypeNames.TRUE_TYPE, trueTypeSymbol);
        types.put(PrimitiveTypeNames.INT, intTypeSymbol);
        types.put(PrimitiveTypeNames.FLOAT, floatTypeSymbol);
        types.put(PrimitiveTypeNames.STRING, stringTypeSymbol);

        IUnionTypeSymbol boolTypeSymbol = symbolFactory.createUnionTypeSymbol();
        boolTypeSymbol.addTypeSymbol(falseTypeSymbol);
        boolTypeSymbol.addTypeSymbol(trueTypeSymbol);

        IUnionTypeSymbol numTypeSymbol = symbolFactory.createUnionTypeSymbol();
        numTypeSymbol.addTypeSymbol(intTypeSymbol);
        numTypeSymbol.addTypeSymbol(floatTypeSymbol);

        IUnionTypeSymbol scalarTypeSymbol = symbolFactory.createUnionTypeSymbol();
        scalarTypeSymbol.addTypeSymbol(boolTypeSymbol);
        scalarTypeSymbol.addTypeSymbol(numTypeSymbol);
        scalarTypeSymbol.addTypeSymbol(stringTypeSymbol);

        types.put(PrimitiveTypeNames.BOOL, boolTypeSymbol);
        types.put(PrimitiveTypeNames.NUM, numTypeSymbol);
        types.put(PrimitiveTypeNames.SCALAR, scalarTypeSymbol);

        IArrayTypeSymbol arrayTypeSymbol = symbolFactory.createArrayTypeSymbol(
                "array", scalarTypeSymbol, mixedTypeSymbol);
        IPseudoTypeSymbol resourceTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource");

        types.put(PrimitiveTypeNames.ARRAY, arrayTypeSymbol);
        types.put(PrimitiveTypeNames.RESOURCE, resourceTypeSymbol);


        return types;
    }

}
