/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration.testutils;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.OperatorProvider;
import ch.tsphp.tinsphp.core.StandardConstraintAndVariables;
import org.junit.Ignore;

import java.util.Map;

@Ignore
public abstract class AOperatorProviderTest extends ATest
{
    protected IOperatorsProvider createOperatorProvider() {
        return createOperatorProvider(symbolFactory, typeHelper, std, builtInSymbols);
    }

    protected IOperatorsProvider createOperatorProvider(
            ISymbolFactory theSymbolFactory,
            ITypeHelper theTypeHelperResolver,
            StandardConstraintAndVariables standardConstraintAndVariables,
            Map<String, ISymbol> theBuiltInSymbols) {
        return new OperatorProvider(
                theSymbolFactory, theTypeHelperResolver, standardConstraintAndVariables, theBuiltInSymbols);
    }
}
