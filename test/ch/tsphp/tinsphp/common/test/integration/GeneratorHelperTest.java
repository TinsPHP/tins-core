/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.common.test.integration;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.core.IGeneratorHelper;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GeneratorHelperTest extends ATest
{
    @Test
    public void createClass_Standard_NameIsPassedName() {
        String name = "foo";

        IGeneratorHelper helper = createGenerator();
        IClassTypeSymbol result = helper.createClass(name);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createClass_Standard_ModifiersOnlyContainsNullable() {
        String name = "foo";

        IGeneratorHelper helper = createGenerator();
        IClassTypeSymbol result = helper.createClass(name);

        assertThat(result.getModifiers(), containsInAnyOrder(TokenTypes.QuestionMark));
    }

    @Test
    public void createClass_Standard_EnclosingScopeIsNull() {
        String name = "foo";

        IGeneratorHelper helper = createGenerator();
        IClassTypeSymbol result = helper.createClass(name);

        assertThat(result.getEnclosingScope(), is(nullValue()));
    }

    @Test
    public void defineMethod_Standard_NameIsPassedName() {
        String name = "foo";

        IGeneratorHelper helper = createGenerator();
        IMethodSymbol result = helper.defineMethod(
                mock(IClassTypeSymbol.class), name, mock(ITypeSymbol.class));

        assertThat(result.getName(), is(name));
    }

    @Test
    public void defineMethod_Standard_ReturnTypeIsPassedTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        IGeneratorHelper helper = createGenerator();
        IMethodSymbol result = helper.defineMethod(
                mock(IClassTypeSymbol.class), "foo", typeSymbol);

        assertThat(result.getType(), is(typeSymbol));
    }

    @Test
    public void defineMethod_Standard_DefinesSymbolInGivenClass() {
        IClassTypeSymbol classTypeSymbol = mock(IClassTypeSymbol.class);

        IGeneratorHelper helper = createGenerator();
        helper.defineMethod(classTypeSymbol, "", mock(ITypeSymbol.class));

        verify(classTypeSymbol).define(any(ISymbol.class));
    }

    @Test
    public void defineMethod_Standard_ModifiersIsEmpty() {
        //no arrange necessary

        IGeneratorHelper helper = createGenerator();
        IMethodSymbol result = helper.defineMethod(
                mock(IClassTypeSymbol.class), "foo", mock(ITypeSymbol.class));

        assertThat(result.getModifiers(), empty());
    }

    @Test
    public void createFunction_Standard_NameIsPassedName() {
        String name = "foo";

        IGeneratorHelper helper = createGenerator();
        IMethodSymbol result = helper.createFunction(name, mock(ITypeSymbol.class));

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createFunction_Standard_ReturnTypeIsPassedTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        IGeneratorHelper helper = createGenerator();
        IMethodSymbol result = helper.createFunction("foo", typeSymbol);

        assertThat(result.getType(), is(typeSymbol));
    }

    @Test
    public void createConstant_Standard_NameIsPassedName() {
        String name = "foo";

        IGeneratorHelper helper = createGenerator();
        IVariableSymbol result = helper.createConstant(name, mock(ITypeSymbol.class));

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createConstant_Standard_TypeIsPassedTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        IGeneratorHelper helper = createGenerator();
        IVariableSymbol result = helper.createConstant("foo", typeSymbol);

        assertThat(result.getType(), is(typeSymbol));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUnionTypeSymbolFromPrimitive_NonExisting_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IGeneratorHelper helper = createGenerator();
        helper.createUnionTypeSymbolFromPrimitives("nonExisting");

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUnionTypeSymbolFromPrimitive_SecondNonExisting_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IGeneratorHelper helper = createGenerator();
        helper.createUnionTypeSymbolFromPrimitives(PrimitiveTypeNames.ARRAY, "nonExisting");

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUnionTypeSymbolFromPrimitive_FirstOfThreeNonExisting_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IGeneratorHelper helper = createGenerator();
        helper.createUnionTypeSymbolFromPrimitives("nonExisting", PrimitiveTypeNames.ARRAY, PrimitiveTypeNames.ARRAY);

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUnionTypeSymbolFromPrimitive_SecondOfThreeNonExisting_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IGeneratorHelper helper = createGenerator();
        helper.createUnionTypeSymbolFromPrimitives(PrimitiveTypeNames.ARRAY, "nonExisting", PrimitiveTypeNames.ARRAY);

        //assert in annotation
    }

    @Test
    public void createUnionTypeSymbolFromPrimitive_AllTypes_ReturnsUnionTypeWithAllPrimitiveTypes() {
        //no arrange necessary

        IGeneratorHelper helper = createGenerator();
        IUnionTypeSymbol result = helper.createUnionTypeSymbolFromPrimitives(
                PrimitiveTypeNames.NULL,
                PrimitiveTypeNames.TRUE,
                PrimitiveTypeNames.FALSE,
                PrimitiveTypeNames.BOOL,
                PrimitiveTypeNames.INT,
                PrimitiveTypeNames.FLOAT,
                PrimitiveTypeNames.NUM,
                PrimitiveTypeNames.STRING,
                PrimitiveTypeNames.SCALAR,
                PrimitiveTypeNames.ARRAY,
                PrimitiveTypeNames.RESOURCE,
                PrimitiveTypeNames.MIXED
        );

        assertThat(result.getTypeSymbols().entrySet(), everyItem(isIn(primitiveTypes.entrySet())));
    }


    private IGeneratorHelper createGenerator() {
        return createGenerator(astHelper, symbolFactory, primitiveTypes);
    }

}
