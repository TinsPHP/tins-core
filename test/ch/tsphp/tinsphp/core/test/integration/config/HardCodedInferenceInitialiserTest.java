/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration.config;

import ch.tsphp.common.AstHelper;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.config.ICoreInitialiser;
import ch.tsphp.tinsphp.common.resolving.ISymbolResolver;
import ch.tsphp.tinsphp.core.config.HardCodedCoreInitialiser;
import ch.tsphp.tinsphp.symbols.config.HardCodedSymbolsInitialiser;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HardCodedInferenceInitialiserTest
{

    @Test
    public void getCore_SecondCall_ReturnsSameInstanceAsFirstCall() {
        ICoreInitialiser initialiser = createInitialiser();
        ICore firstCall = initialiser.getCore();

        ICore result = initialiser.getCore();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getCoreSymbolResolver_SecondCall_ReturnsSameInstanceAsFirstCall() {
        ICoreInitialiser initialiser = createInitialiser();
        ISymbolResolver firstCall = initialiser.getCoreSymbolResolver();

        ISymbolResolver result = initialiser.getCoreSymbolResolver();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getCore_SecondCallAfterReset_ReturnsSameInstanceAsFirstCallBeforeReset() {
        ICoreInitialiser initialiser = createInitialiser();
        ICore firstCall = initialiser.getCore();
        initialiser.reset();

        ICore result = initialiser.getCore();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getCoreSymbolResolver_SecondCallAfterReset_ReturnsSameInstanceAsFirstCallBeforeReset() {
        ICoreInitialiser initialiser = createInitialiser();
        ISymbolResolver firstCall = initialiser.getCoreSymbolResolver();
        initialiser.reset();

        ISymbolResolver result = initialiser.getCoreSymbolResolver();

        assertThat(result, is(firstCall));
    }


    protected ICoreInitialiser createInitialiser() {
        return new HardCodedCoreInitialiser(new AstHelper(new TSPHPAstAdaptor()), new HardCodedSymbolsInitialiser());
    }
}
