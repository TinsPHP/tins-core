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

public class PrimitiveTypeProvider implements ITypeProvider
{

    private ISymbolFactory symbolFactory;
    private ITypeSymbol mixedTypeSymbol;
    private Map<String, ITypeSymbol> types;

    public PrimitiveTypeProvider(ISymbolFactory theSymbolFactory) {
        symbolFactory = theSymbolFactory;
        mixedTypeSymbol = symbolFactory.getMixedTypeSymbol();

        createTypes();
    }

    private void createTypes() {
        types = new HashMap<>();

        INullTypeSymbol nullTypeSymbol = symbolFactory.createNullTypeSymbol();

        IPseudoTypeSymbol scalarTypeSymbol = symbolFactory.createPseudoTypeSymbol("scalar");

        IScalarTypeSymbol boolTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "bool", scalarTypeSymbol, TokenTypes.Bool, "false");

        IScalarTypeSymbol trueTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "true", boolTypeSymbol, TokenTypes.Bool, "true");
        IScalarTypeSymbol falseTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "false", boolTypeSymbol, TokenTypes.Bool, "false");

        IPseudoTypeSymbol numTypeSymbol = symbolFactory.createPseudoTypeSymbol("num");

        IScalarTypeSymbol intTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "int", numTypeSymbol, TokenTypes.Int, "0");

        IScalarTypeSymbol floatTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "float", numTypeSymbol, TokenTypes.Float, "0.0");

        IScalarTypeSymbol stringTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "string", scalarTypeSymbol, TokenTypes.String, "''");

        types.put(PrimitiveTypeNames.TYPE_NAME_NULL, nullTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_TRUE, trueTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_FALSE, falseTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_BOOL, boolTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_INT, intTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_FLOAT, floatTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_NUM, numTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_STRING, stringTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_SCALAR, scalarTypeSymbol);

        IArrayTypeSymbol arrayTypeSymbol = symbolFactory.createArrayTypeSymbol(
                "array", stringTypeSymbol, mixedTypeSymbol);

        IPseudoTypeSymbol resourceTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource");

        types.put(PrimitiveTypeNames.TYPE_NAME_ARRAY, arrayTypeSymbol);
        types.put(PrimitiveTypeNames.TYPE_NAME_RESOURCE, resourceTypeSymbol);
    }

    @Override
    public Map<String, ITypeSymbol> getTypes() {
        return types;
    }

}
