/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
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

    protected IBindingCollection createUnaryTBindingCollection() {
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable(VAR_EXPR, reference("T"));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, reference("T"));
        return bindingCollection;
    }


    protected IBindingCollection createAssignBindingCollection() {
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable(VAR_LHS, reference(T_LHS));
        bindingCollection.addVariable(VAR_RHS, fixReference(T_RHS));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, reference(T_LHS));
        return bindingCollection;
    }

    protected IBindingCollection createFixBinaryBindingCollection() {
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable(VAR_LHS, fixReference(T_LHS));
        bindingCollection.addVariable(VAR_RHS, fixReference(T_RHS));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        return bindingCollection;
    }

    protected IBindingCollection createFixUnaryBindingCollection() {
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable(VAR_EXPR, fixReference(T_EXPR));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, fixReference(T_RETURN));
        return bindingCollection;
    }

    protected TypeVariableReference reference(String name) {
        return new TypeVariableReference(name);
    }

    protected FixedTypeVariableReference fixReference(String name) {
        return new FixedTypeVariableReference(reference(name));
    }
}
