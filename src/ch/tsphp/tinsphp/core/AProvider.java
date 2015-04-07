/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AProvider
{
    protected static final String T_LHS = "Tlhs";
    protected static final String T_RHS = "Trhs";
    protected static final String T_RETURN = "Treturn";
    protected static final String T_EXPR = "Texpr";

    protected final ISymbolFactory symbolFactory;
    protected final IOverloadResolver overloadResolver;
    protected final ITypeSymbol falseTypeSymbol;
    protected final ITypeSymbol trueTypeSymbol;
    protected final ITypeSymbol boolTypeSymbol;
    protected final ITypeSymbol intTypeSymbol;
    protected final ITypeSymbol floatTypeSymbol;
    protected final ITypeSymbol numTypeSymbol;
    protected final ITypeSymbol stringTypeSymbol;
    protected final ITypeSymbol arrayTypeSymbol;
    protected final ITypeSymbol mixedTypeSymbol;
    protected final IUnionTypeSymbol numOrFalse;
    protected final IUnionTypeSymbol floatOrFalse;
    protected final IUnionTypeSymbol intOrFalse;

    protected final TypeConstraint falseTypeConstraint;
    protected final TypeConstraint trueTypeConstraint;
    protected final TypeConstraint boolTypeConstraint;
    protected final TypeConstraint intTypeConstraint;
    protected final TypeConstraint floatTypeConstraint;
    protected final TypeConstraint numTypeConstraint;
    protected final TypeConstraint stringTypeConstraint;
    protected final TypeConstraint arrayTypeConstraint;
    protected final TypeConstraint mixedTypeConstraint;
    protected final TypeConstraint numOrFalseTypeConstraint;
    protected final TypeConstraint floatOrFalseTypeConstraint;
    protected final TypeConstraint intOrFalseTypeConstraint;

    protected List<String> binaryParameterIds;
    protected List<String> unaryParameterId;

    public AProvider(
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            Map<String, ITypeSymbol> thePrimitiveType) {

        symbolFactory = theSymbolFactory;
        overloadResolver = theOverloadResolver;

        falseTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.FALSE);
        trueTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.TRUE);
        boolTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.BOOL);
        intTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.INT);
        floatTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.FLOAT);
        numTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.NUM);
        stringTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.STRING);
        arrayTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.ARRAY);
        mixedTypeSymbol = thePrimitiveType.get(PrimitiveTypeNames.MIXED);

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

        binaryParameterIds = Arrays.asList(T_LHS, T_RHS);
        unaryParameterId = Arrays.asList(T_EXPR);
    }
}
