/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class StandardConstraintAndVariables
{
    public static final String T_LHS = "Tlhs";
    public static final String T_RHS = "Trhs";
    public static final String T_RETURN = "Treturn";
    public static final String T_EXPR = "Texpr";
    public static final String VAR_LHS = "$lhs";
    public static final String VAR_RHS = "$rhs";
    public static final String VAR_EXPR = "$expr";

    public final ITypeSymbol falseTypeSymbol;
    public final ITypeSymbol trueTypeSymbol;
    public final ITypeSymbol boolTypeSymbol;
    public final ITypeSymbol intTypeSymbol;
    public final ITypeSymbol floatTypeSymbol;
    public final ITypeSymbol numTypeSymbol;
    public final ITypeSymbol stringTypeSymbol;
    public final ITypeSymbol scalarTypeSymbol;
    public final ITypeSymbol arrayTypeSymbol;
    public final ITypeSymbol mixedTypeSymbol;
    public final IUnionTypeSymbol numOrFalse;
    public final IUnionTypeSymbol floatOrFalse;
    public final IUnionTypeSymbol intOrFalse;

    public final List<IVariable> binaryParameterIds;
    public final List<IVariable> unaryParameterId;
    public final IVariable lhs;
    public final IVariable rhs;
    public final IVariable expr;

    public StandardConstraintAndVariables(ISymbolFactory symbolFactory, Map<String, ITypeSymbol> primitiveType) {
        falseTypeSymbol = primitiveType.get(PrimitiveTypeNames.FALSE);
        trueTypeSymbol = primitiveType.get(PrimitiveTypeNames.TRUE);
        boolTypeSymbol = primitiveType.get(PrimitiveTypeNames.BOOL);
        intTypeSymbol = primitiveType.get(PrimitiveTypeNames.INT);
        floatTypeSymbol = primitiveType.get(PrimitiveTypeNames.FLOAT);
        numTypeSymbol = primitiveType.get(PrimitiveTypeNames.NUM);
        stringTypeSymbol = primitiveType.get(PrimitiveTypeNames.STRING);
        scalarTypeSymbol = primitiveType.get(PrimitiveTypeNames.SCALAR);
        arrayTypeSymbol = primitiveType.get(PrimitiveTypeNames.ARRAY);
        mixedTypeSymbol = primitiveType.get(PrimitiveTypeNames.MIXED);

        numOrFalse = symbolFactory.createUnionTypeSymbol();
        numOrFalse.addTypeSymbol(numTypeSymbol);
        numOrFalse.addTypeSymbol(falseTypeSymbol);

        intOrFalse = symbolFactory.createUnionTypeSymbol();
        intOrFalse.addTypeSymbol(intTypeSymbol);
        intOrFalse.addTypeSymbol(falseTypeSymbol);

        floatOrFalse = symbolFactory.createUnionTypeSymbol();
        floatOrFalse.addTypeSymbol(floatTypeSymbol);
        floatOrFalse.addTypeSymbol(falseTypeSymbol);

        lhs = symbolFactory.createVariable(VAR_LHS);
        rhs = symbolFactory.createVariable(VAR_RHS);
        binaryParameterIds = Arrays.asList(lhs, rhs);

        expr = symbolFactory.createVariable(VAR_EXPR);
        unaryParameterId = Arrays.asList(expr);
    }
}
