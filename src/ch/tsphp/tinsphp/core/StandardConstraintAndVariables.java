/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.TypeVariableNames;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;

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

    public final TypeConstraint falseTypeConstraint;
    public final TypeConstraint trueTypeConstraint;
    public final TypeConstraint boolTypeConstraint;
    public final TypeConstraint intTypeConstraint;
    public final TypeConstraint floatTypeConstraint;
    public final TypeConstraint numTypeConstraint;
    public final TypeConstraint stringTypeConstraint;
    public final TypeConstraint arrayTypeConstraint;
    public final TypeConstraint mixedTypeConstraint;
    public final TypeConstraint numOrFalseTypeConstraint;
    public final TypeConstraint floatOrFalseTypeConstraint;
    public final TypeConstraint intOrFalseTypeConstraint;

    public final List<IVariable> binaryParameterIds;
    public final List<IVariable> unaryParameterId;
    public final IVariable fixTypedReturnVariable;
    public final IVariable variableTypedReturnVariable;
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
        numOrFalse.seal();

        intOrFalse = symbolFactory.createUnionTypeSymbol();
        intOrFalse.addTypeSymbol(intTypeSymbol);
        intOrFalse.addTypeSymbol(falseTypeSymbol);
        intOrFalse.seal();

        floatOrFalse = symbolFactory.createUnionTypeSymbol();
        floatOrFalse.addTypeSymbol(floatTypeSymbol);
        floatOrFalse.addTypeSymbol(falseTypeSymbol);
        floatOrFalse.seal();

        falseTypeConstraint = new TypeConstraint(falseTypeSymbol);
        trueTypeConstraint = new TypeConstraint(trueTypeSymbol);
        boolTypeConstraint = new TypeConstraint(boolTypeSymbol);
        intTypeConstraint = new TypeConstraint(intTypeSymbol);
        floatTypeConstraint = new TypeConstraint(floatTypeSymbol);
        numTypeConstraint = new TypeConstraint(numTypeSymbol);
        stringTypeConstraint = new TypeConstraint(stringTypeSymbol);
        arrayTypeConstraint = new TypeConstraint(arrayTypeSymbol);
        mixedTypeConstraint = new TypeConstraint(mixedTypeSymbol);

        numOrFalseTypeConstraint = new TypeConstraint(numOrFalse);
        intOrFalseTypeConstraint = new TypeConstraint(intOrFalse);

        floatOrFalseTypeConstraint = new TypeConstraint(floatOrFalse);

        IVariable lhs = symbolFactory.createVariable("$lhs", T_LHS);
        lhs.setHasFixedType();
        IVariable rhs = symbolFactory.createVariable("$rhs", T_RHS);
        rhs.setHasFixedType();
        binaryParameterIds = Arrays.asList(lhs, rhs);
        fixTypedReturnVariable = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, T_RETURN);
        fixTypedReturnVariable.setHasFixedType();
        variableTypedReturnVariable = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, T_RETURN);

        IVariable expr = symbolFactory.createVariable("$expr", T_EXPR);
        expr.setHasFixedType();
        unaryParameterId = Arrays.asList(expr);

        tLhs = symbolFactory.createVariable("$lhs", "T");
        tRhs = symbolFactory.createVariable("$rhs", "T");
        tReturn = symbolFactory.createVariable(TypeVariableNames.RETURN_VARIABLE_NAME, "T");
        tExpr = symbolFactory.createVariable("$expr", "T");
    }
}
