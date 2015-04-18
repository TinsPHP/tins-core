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
import ch.tsphp.tinsphp.symbols.TypeVariableNames;

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

    public final ITypeSymbol falseTypeSymbol;
    public final ITypeSymbol trueTypeSymbol;
    public final ITypeSymbol boolTypeSymbol;
    public final ITypeSymbol intTypeSymbol;
    public final ITypeSymbol floatTypeSymbol;
    public final ITypeSymbol numTypeSymbol;
    public final ITypeSymbol stringTypeSymbol;
    public final ITypeSymbol arrayTypeSymbol;
    public final ITypeSymbol mixedTypeSymbol;
    public final IUnionTypeSymbol numOrFalse;
    public final IUnionTypeSymbol floatOrFalse;
    public final IUnionTypeSymbol intOrFalse;

    public final List<IVariable> fixBinaryParameterIds;
    public final List<IVariable> fixUnaryParameterId;
    public final IVariable fixTLhs;
    public final IVariable fixTRhs;
    public final IVariable fixTReturn;
    public final IVariable fixTExpr;

    public final IVariable variableTLhs;
    public final IVariable variableTRhs;
    public final IVariable variableTReturn;

    public final IVariable tLhs;
    public final IVariable tRhs;
    public final IVariable tReturn;
    public final IVariable tExpr;

    public StandardConstraintAndVariables(ISymbolFactory symbolFactory, Map<String, ITypeSymbol> primitiveType) {
        falseTypeSymbol = primitiveType.get(PrimitiveTypeNames.FALSE);
        trueTypeSymbol = primitiveType.get(PrimitiveTypeNames.TRUE);
        boolTypeSymbol = primitiveType.get(PrimitiveTypeNames.BOOL);
        intTypeSymbol = primitiveType.get(PrimitiveTypeNames.INT);
        floatTypeSymbol = primitiveType.get(PrimitiveTypeNames.FLOAT);
        numTypeSymbol = primitiveType.get(PrimitiveTypeNames.NUM);
        stringTypeSymbol = primitiveType.get(PrimitiveTypeNames.STRING);
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

        fixTLhs = symbolFactory.createVariable("$lhs", T_LHS);
        fixTLhs.setHasFixedType();
        fixTRhs = symbolFactory.createVariable("$rhs", T_RHS);
        fixTRhs.setHasFixedType();
        fixBinaryParameterIds = Arrays.asList(fixTLhs, fixTRhs);
        fixTReturn = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, T_RETURN);
        fixTReturn.setHasFixedType();
        fixTExpr = symbolFactory.createVariable("$expr", T_EXPR);
        fixTExpr.setHasFixedType();
        fixUnaryParameterId = Arrays.asList(fixTExpr);

        variableTLhs = symbolFactory.createVariable("$lhs", T_LHS);
        variableTRhs = symbolFactory.createVariable("$rhs", T_RHS);
        variableTReturn = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, T_RETURN);

        tLhs = symbolFactory.createVariable("$lhs", "T");
        tRhs = symbolFactory.createVariable("$rhs", "T");
        tReturn = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, "T");
        tExpr = symbolFactory.createVariable("$expr", "T");
    }
}
