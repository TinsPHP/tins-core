/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test;


import ch.tsphp.tinsphp.core.DummyClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DummyTest
{
    @Test
    public void dummyTestInOrderThatBuildPasses() {
        assertTrue(new DummyClass().dummyMethod(true));
    }

    @Test
    public void dummyTestInOrderThatBuildPasses2() {
        assertTrue(new DummyClass().dummyMethod(false));
    }
}
