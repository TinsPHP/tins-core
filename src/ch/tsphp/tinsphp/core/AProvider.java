/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import static ch.tsphp.tinsphp.common.TinsPHPConstants.RETURN_VARIABLE_NAME;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_EXPR;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RETURN;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.T_RHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_EXPR;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_LHS;
import static ch.tsphp.tinsphp.core.StandardConstraintAndVariables.VAR_RHS;

public abstract class AProvider
{
    protected final ISymbolFactory symbolFactory;
    protected final ITypeHelper typeHelper;
    protected final StandardConstraintAndVariables std;

    public AProvider(
            ISymbolFactory theSymbolFactory,
            ITypeHelper theTypeHelper,
            StandardConstraintAndVariables standardConstraintAndVariables) {
        symbolFactory = theSymbolFactory;
        typeHelper = theTypeHelper;
        std = standardConstraintAndVariables;
    }

    protected IOverloadBindings createUnaryTBindings() {
        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(VAR_EXPR, reference("T"));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference("T"));
        return overloadBindings;
    }

    protected IOverloadBindings createAssignOverloadBindings() {
        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(VAR_LHS, reference(T_LHS));
        overloadBindings.addVariable(VAR_RHS, reference(T_RHS));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_LHS));
        return overloadBindings;
    }

    protected IOverloadBindings createVariableBinaryBindings() {
        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(VAR_LHS, reference(T_LHS));
        overloadBindings.addVariable(VAR_RHS, reference(T_RHS));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, reference(T_RETURN));
        return overloadBindings;
    }

    protected IOverloadBindings createFixBinaryBindings() {
        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(VAR_LHS, fixReference(T_LHS));
        overloadBindings.addVariable(VAR_RHS, fixReference(T_RHS));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        return overloadBindings;
    }

    protected IOverloadBindings createFixUnaryBindings() {
        IOverloadBindings overloadBindings = symbolFactory.createOverloadBindings();
        overloadBindings.addVariable(VAR_EXPR, fixReference(T_EXPR));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        return overloadBindings;
    }

    protected TypeVariableReference reference(String name) {
        return new TypeVariableReference(name);
    }

    protected FixedTypeVariableReference fixReference(String name) {
        return new FixedTypeVariableReference(reference(name));
    }
}
