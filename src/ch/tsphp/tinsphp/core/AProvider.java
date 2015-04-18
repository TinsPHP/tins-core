/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;

public abstract class AProvider
{
    protected final ISymbolFactory symbolFactory;
    protected final IOverloadResolver overloadResolver;
    protected final StandardConstraintAndVariables std;

    public AProvider(
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            StandardConstraintAndVariables standardConstraintAndVariables) {
        symbolFactory = theSymbolFactory;
        overloadResolver = theOverloadResolver;
        std = standardConstraintAndVariables;
    }

    protected IOverloadBindings createBindings(IVariable... variables) {
        OverloadBindings overloadBindings = new OverloadBindings(symbolFactory, overloadResolver);
        for (IVariable variable : variables) {
            overloadBindings.addVariable(
                    variable.getAbsoluteName(), new TypeVariableConstraint(variable.getTypeVariable()));
        }
        return overloadBindings;
    }
}
