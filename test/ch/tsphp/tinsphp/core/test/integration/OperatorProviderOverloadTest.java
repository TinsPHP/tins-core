/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.test.integration;

import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.core.IOperatorsProvider;
import ch.tsphp.tinsphp.core.test.integration.testutils.AOperatorProviderTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;


@RunWith(Parameterized.class)
public class OperatorProviderOverloadTest extends AOperatorProviderTest
{
    private String operatorName;
    private int operatorType;
    private Object[][] signaturesAndFlag;

    public OperatorProviderOverloadTest(String theOperatorName, int theOperatorType, Object[][] theSignatures) {
        operatorName = theOperatorName;
        operatorType = theOperatorType;
        signaturesAndFlag = theSignatures;
    }

    @Test
    public void test() throws RecognitionException {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, IMinimalMethodSymbol> result = provider.getOperators();

        Collection<IFunctionType> overloads = result.get(operatorType).getOverloads();
        List<String> overloadSignatures = new ArrayList<>();
        Map<String, Boolean> overloadSignaturesWithFlag = new HashMap<>();
        for (IFunctionType overload : overloads) {
            overloadSignatures.add(overload.getSignature());
            overloadSignaturesWithFlag.put(overload.getSignature(), overload.hasConvertibleParameterTypes());
        }

        String[] signatures = new String[signaturesAndFlag.length];
        for (int i = 0; i < signaturesAndFlag.length; ++i) {
            signatures[i] = (String) signaturesAndFlag[i][0];
        }

        try {
            assertThat(overloadSignatures, containsInAnyOrder(signatures));
        } catch (AssertionError ex) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(operatorName).append(" failed. Overloads where:\n");
            for (String overloadSignature : overloadSignatures) {
                stringBuilder.append(overloadSignature).append("\n");
            }
            stringBuilder.append(ex.getMessage());
            Assert.fail(stringBuilder.toString());
        }
        for (Object[] signatureAndFlag : signaturesAndFlag) {
            if (overloadSignaturesWithFlag.get((String) signatureAndFlag[0]) != (Boolean) signatureAndFlag[1]) {
                Assert.fail(operatorName + " failed.\nSignature " + signatureAndFlag[0] + " "
                                + ((Boolean) signatureAndFlag[1]
                                ? "was expected to have convertible parameter types but did not."
                                : "should not have convertible parameter types but did.")
                );
            }
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"or", TokenTypes.LogicOrWeak, new Object[][]{
//                        {"falseType x falseType -> falseType", false},
//                        {"trueType x trueType -> trueType", false},
//                        {"trueType x (falseType | trueType) -> trueType", false},
//                        {"trueType x {as (falseType | trueType)} -> trueType", true},
//                        {"(falseType | trueType) x trueType -> trueType", false},
//                        {"{as (falseType | trueType)} x trueType -> trueType", true},
                        {"(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)", false},
                        {"{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)", true},
                }},
                {"xor", TokenTypes.LogicXorWeak, new Object[][]{
//                        {"falseType x trueType -> trueType", false},
//                        {"trueType x falseType -> trueType", false},
//                        {"falseType x falseType -> falseType", false},
//                        {"trueType x trueType -> falseType", false},
                        {"(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)", false},
                        {"{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)", true},
                }},
                {"and", TokenTypes.LogicAndWeak, new Object[][]{
//                        {"falseType x falseType -> falseType", false},
//                        {"falseType x (falseType | trueType) -> falseType", false},
//                        {"falseType x {as (falseType | trueType)} -> falseType", true},
//                        {"(falseType | trueType) x falseType -> falseType", false},
//                        {"{as (falseType | trueType)} x falseType -> falseType", true},
//                        {"trueType x trueType -> trueType", false},
                        {"(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)", false},
                        {"{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)", true},
                }},
                {"=", TokenTypes.Assign, new Object[][]{
                        {"Tlhs x Trhs -> Tlhs \\ Trhs <: Tlhs", false}
                }},
                {"+=", TokenTypes.PlusAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x float -> Tlhs \\ float <: Tlhs <: float", false},
//                        {"Tlhs x {as (float | int)} -> Tlhs \\ float <: Tlhs <: float", true},
//                        {"Tlhs x float -> Tlhs \\ float <: Tlhs <: {as (float | int)}", true},
                        {"Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: (float | int)", true},
                        {"Tlhs x array -> Tlhs \\ array <: Tlhs <: array", false},
                }},
                {"-=", TokenTypes.MinusAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x float -> Tlhs \\ float <: Tlhs <: float", false},
//                        {"Tlhs x {as (float | int)} -> Tlhs \\ float <: Tlhs <: float", true},
//                        {"Tlhs x float -> Tlhs \\ float <: Tlhs <: {as (float | int)}", true},
                        {"Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: (float | int)", true},
                }},
                {"*=", TokenTypes.MultiplyAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x float -> Tlhs \\ float <: Tlhs <: float", false},
//                        {"Tlhs x {as (float | int)} -> Tlhs \\ float <: Tlhs <: float", true},
//                        {"Tlhs x float -> Tlhs \\ float <: Tlhs <: {as (float | int)}", true},
                        {"Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: (float | int)", true},
                }},
                {"/=", TokenTypes.DivideAssign, new Object[][]{
                        {"Tlhs x float -> Tlhs \\ (falseType | float) <: Tlhs <: (falseType | float)", false},
//                        {"Tlhs x (float | int) -> Tlhs "
//                                + "\\ (falseType | float | int) <: Tlhs <: (falseType | float | int)", false},
                        {"Tlhs x float -> Tlhs \\ (falseType | float) <: Tlhs <: {as (float | int)}", true},
                        {"Tlhs x {as (float | int)} -> Tlhs "
                                + "\\ (falseType | float | int) <: Tlhs <: {as (float | int)}", true},
                }},
                {"%=", TokenTypes.ModuloAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ (falseType | int) <: Tlhs <: (falseType | int)", false},
                        {"Tlhs x (array | {as int}) -> Tlhs \\ (falseType | int) <: Tlhs <: (array | {as int})", true},
                }},
                {"&=", TokenTypes.BitwiseAndAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x (array | {as int}) -> Tlhs \\ int <: Tlhs <: (array | {as int})", true},
                        {"Tlhs x string -> Tlhs \\ string <: Tlhs <: string", false},
                }},
                {"^=", TokenTypes.BitwiseXorAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x (array | {as int}) -> Tlhs \\ int <: Tlhs <: (array | {as int})", true},
                        {"Tlhs x string -> Tlhs \\ string <: Tlhs <: string", false},
                }},
                {"|=", TokenTypes.BitwiseOrAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x (array | {as int}) -> Tlhs \\ int <: Tlhs <: (array | {as int})", true},
                        {"Tlhs x string -> Tlhs \\ string <: Tlhs <: string", false},
                }},
                {">>=", TokenTypes.ShiftLeftAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x (array | {as int}) -> Tlhs \\ int <: Tlhs <: (array | {as int})", true},
                }},
                {"<<=", TokenTypes.ShiftRightAssign, new Object[][]{
                        {"Tlhs x int -> Tlhs \\ int <: Tlhs <: int", false},
                        {"Tlhs x (array | {as int}) -> Tlhs \\ int <: Tlhs <: (array | {as int})", true},
                }},
                {".=", TokenTypes.DotAssign, new Object[][]{
                        {"Tlhs x string -> Tlhs \\ string <: Tlhs <: string", false},
                        {"Tlhs x {as string} -> Tlhs \\ string <: Tlhs <: {as string}", true},
                }},
                {"?", TokenTypes.QuestionMark, new Object[][]{
//                        {"falseType x mixed x Telse -> Treturn \\ Telse <: Treturn", false},
//                        {"trueType x Tif x mixed -> Treturn \\ Tif <: Treturn", false},
                        {"(falseType | trueType) x Tif x Telse -> Treturn \\ (Telse | Tif) <: Treturn", false},
                        {"{as (falseType | trueType)} x Tif x Telse -> Treturn \\ (Telse | Tif) <: Treturn", true},
                }},
                {"||", TokenTypes.LogicOr, new Object[][]{
//                        {"falseType x falseType -> falseType", false},
//                        {"trueType x trueType -> trueType", false},
//                        {"trueType x (falseType | trueType) -> trueType", false},
//                        {"trueType x {as (falseType | trueType)} -> trueType", true},
//                        {"(falseType | trueType) x trueType -> trueType", false},
//                        {"{as (falseType | trueType)} x trueType -> trueType", true},
                        {"(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)", false},
                        {"{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)", true},
                }},
                {"&&", TokenTypes.LogicAnd, new Object[][]{
//                        {"falseType x falseType -> falseType", false},
//                        {"falseType x (falseType | trueType) -> falseType", false},
//                        {"falseType x {as (falseType | trueType)} -> falseType", true},
//                        {"(falseType | trueType) x falseType -> falseType", false},
//                        {"{as (falseType | trueType)} x falseType -> falseType", true},
//                        {"trueType x trueType -> trueType", false},
                        {"(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)", false},
                        {"{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)", true},
                }},
                {"|", TokenTypes.BitwiseOr, new Object[][]{
                        {"int x int -> int", false},
                        {"(array | {as int}) x (array | {as int}) -> int", true},
                        {"string x string -> string", false},
                }},
                {"^", TokenTypes.BitwiseXor, new Object[][]{
                        {"int x int -> int", false},
                        {"(array | {as int}) x (array | {as int}) -> int", true},
                        {"string x string -> string", false},
                }},
                {"&", TokenTypes.BitwiseAnd, new Object[][]{
                        {"int x int -> int", false},
                        {"(array | {as int}) x (array | {as int}) -> int", true},
                        {"string x string -> string", false},
                }},
                {"==", TokenTypes.Equal, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {"===", TokenTypes.Identical, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {"!=", TokenTypes.NotEqual, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {"!==", TokenTypes.NotIdentical, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {"<", TokenTypes.LessThan, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}},},
                {"<=", TokenTypes.LessEqualThan, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {">", TokenTypes.GreaterThan, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {">=", TokenTypes.GreaterEqualThan, new Object[][]{{"mixed x mixed -> (falseType | trueType)", false}}},
                {"<<", TokenTypes.ShiftLeft, new Object[][]{
                        {"int x int -> int", false},
                        {"(array | {as int}) x (array | {as int}) -> int", true}
                }},
                {">>", TokenTypes.ShiftRight, new Object[][]{
                        {"int x int -> int", false},
                        {"(array | {as int}) x (array | {as int}) -> int", true}
                }},
                {"+", TokenTypes.Plus, new Object[][]{
                        {"int x int -> int", false},
                        {"float x float -> float", false},
                        {"{as T} x {as T} -> T \\ T <: (float | int)", true},
                        {"array x array -> array", false},
                }},
                {"-", TokenTypes.Minus, new Object[][]{
                        {"int x int -> int", false},
                        {"float x float -> float", false},
                        {"{as T} x {as T} -> T \\ T <: (float | int)", true},
                }},
                {".", TokenTypes.Dot, new Object[][]{
                        {"string x string -> string", false},
                        {"{as string} x {as string} -> string", true},
                }},
                {"*", TokenTypes.Multiply, new Object[][]{
                        {"int x int -> int", false},
                        {"float x float -> float", false},
                        {"{as T} x {as T} -> T \\ T <: (float | int)", true},
                }},
                {"/", TokenTypes.Divide, new Object[][]{
                        {"int x int -> (falseType | float | int)", false},
                        {"float x float -> (falseType | float)", false},
                        {"float x {as (float | int)} -> (falseType | float)", true},
                        {"{as (float | int)} x float -> (falseType | float)", true},
                        {"{as (float | int)} x {as (float | int)} -> (falseType | float | int)", true},
                }},
                {"%", TokenTypes.Modulo, new Object[][]{
                        {"int x int -> (falseType | int)", false},
                        {"(array | {as int}) x (array | {as int}) -> (falseType | int)", true},
                }},
                {"instanceof", TokenTypes.Instanceof, new Object[][]{
                        {"mixed x mixed -> (falseType | trueType)", false}
                }},
                {"preIncr", TokenTypes.PRE_INCREMENT, new Object[][]{
                        {"T -> T \\ int <: T <: int", false},
                        {"T -> T \\ float <: T <: float", false},
                        {"T -> T \\ (float | int) <: T <: (float | int)", false},
                        {"T -> T \\ (float | int | string) <: T <: (float | int | string)", false},
                        {"T -> T \\ (int | nullType) <: T <: (int | nullType)", false},
                        {"T -> T", false},
                }},
                {"preDecr", TokenTypes.PRE_DECREMENT, new Object[][]{
                        {"T -> T \\ int <: T <: int", false},
                        {"T -> T \\ float <: T <: float", false},
                        {"T -> T \\ (float | int) <: T <: (float | int)", false},
                        {"T -> T \\ (float | int | string) <: T <: (float | int | string)", false},
                        {"T -> T", false},
                }},
                {"cast", TokenTypes.CAST, new Object[][]{{"Tlhs x mixed -> Tlhs", false}}},
                {"@", TokenTypes.At, new Object[][]{{"T -> T", false}}},
                {"~", TokenTypes.BitwiseNot, new Object[][]{
                        {"int -> int", false},
                        {"float -> int", false},
                        {"string -> string", false}
                }},
                {"!", TokenTypes.LogicNot, new Object[][]{
//                        {"falseType -> trueType", false},
//                        {"trueType -> falseType", false},
                        {"(falseType | trueType) -> (falseType | trueType)", false},
                        {"{as (falseType | trueType)} -> (falseType | trueType)", true},
                }},
                {"uMinus", TokenTypes.UNARY_MINUS, new Object[][]{
                        {"float -> float", false},
                        {"(falseType | float | int | nullType | string | trueType) -> int", false}
                }},
                {"uPlus", TokenTypes.UNARY_PLUS, new Object[][]{
                        {"float -> float", false},
                        {"(falseType | float | int | nullType | string | trueType) -> int", false}
                }},
                {"clone", TokenTypes.Clone, new Object[][]{{"T -> T", false}}},
                {"new", TokenTypes.New, new Object[][]{{"T -> T", false}}},
                {"postIncr", TokenTypes.POST_INCREMENT, new Object[][]{
                        {"T -> T \\ int <: T <: int", false},
                        {"T -> T \\ float <: T <: float", false},
                        {"T -> T \\ (float | int) <: T <: (float | int)", false},
                        {"T -> T \\ (float | int | string) <: T <: (float | int | string)", false},
                        {"T -> T \\ (int | nullType) <: T <: (int | nullType)", false},
                        {"T -> T", false},
                }},
                {"postDecr", TokenTypes.POST_DECREMENT, new Object[][]{
                        {"T -> T \\ int <: T <: int", false},
                        {"T -> T \\ float <: T <: float", false},
                        {"T -> T \\ (float | int) <: T <: (float | int)", false},
                        {"T -> T \\ (float | int | string) <: T <: (float | int | string)", false},
                        {"T -> T", false},
                }},
                {"arrAccess", TokenTypes.ARRAY_ACCESS, new Object[][]{
                        {"array x (int | string) -> mixed", false},
                        {"array x {as int} -> mixed", true}
                }},
                {"if", TokenTypes.If, new Object[][]{
                        {"(falseType | trueType) -> mixed", false},
                        {"{as (falseType | trueType)} -> mixed", true},
                }},
                {"while", TokenTypes.While, new Object[][]{
                        {"(falseType | trueType) -> mixed", false},
                        {"{as (falseType | trueType)} -> mixed", true},
                }},
                {"do", TokenTypes.Do, new Object[][]{
                        {"(falseType | trueType) -> mixed", false},
                        {"{as (falseType | trueType)} -> mixed", true},
                }},
                {"for", TokenTypes.For, new Object[][]{
                        {"(falseType | trueType) -> mixed", false},
                        {"{as (falseType | trueType)} -> mixed", true},
                }},
                {
                        "foreach", TokenTypes.Foreach,
                        new Object[][]{{"array x Tvalue x Tkey -> mixed "
                                + "\\ mixed <: Tvalue, (int | string) <: Tkey", false}}
                },
                {"switch", TokenTypes.Switch, new Object[][]{
                        {"(falseType | float | int | string | trueType) -> mixed", false}
                }},
                {"throw", TokenTypes.Throw, new Object[][]{{"Exception -> mixed", false}}},
                {"catch", TokenTypes.Catch, new Object[][]{{"Tlhs x Trhs -> Trhs \\ Tlhs <: Trhs", false}}},
                {"echo", TokenTypes.Echo, new Object[][]{{"string -> mixed", false}, {"{as string} -> mixed", true}}},
                {"exit", TokenTypes.Exit, new Object[][]{
                        {"int -> mixed", false}, {"string -> mixed", false}, {"{as string} -> mixed", true}
                }},
        });
    }
}
