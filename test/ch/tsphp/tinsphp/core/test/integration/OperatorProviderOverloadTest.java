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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;


@RunWith(Parameterized.class)
public class OperatorProviderOverloadTest extends AOperatorProviderTest
{
    private String operatorName;
    private int operatorType;
    private String[] signatures;

    public OperatorProviderOverloadTest(String theOperatorName, int theOperatorType, String[] theSignatures) {
        operatorName = theOperatorName;
        operatorType = theOperatorType;
        signatures = theSignatures;
    }

    @Test
    public void test() throws RecognitionException {
        //no arrange necessary

        IOperatorsProvider provider = createOperatorProvider();
        Map<Integer, IMinimalMethodSymbol> result = provider.getOperators();

        List<IFunctionType> overloads = result.get(operatorType).getOverloads();
        List<String> overloadSignatures = new ArrayList<>();
        for (IFunctionType overload : overloads) {
            overloadSignatures.add(overload.getSignature());
        }

        try {
            assertThat(overloadSignatures, containsInAnyOrder(signatures));
        } catch (AssertionError ex) {
            System.err.println(operatorName + " failed. Overloads where:");
            for (String overloadSignature : overloadSignatures) {
                System.err.println(overloadSignature);
            }
            throw ex;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"or", TokenTypes.LogicOrWeak, new String[]{
                        "falseType x falseType -> falseType",
                        "trueType x (falseType | trueType) -> trueType",
                        "trueType x {as (falseType | trueType)} -> trueType",
                        "(falseType | trueType) x trueType -> trueType",
                        "{as (falseType | trueType)} x trueType -> trueType",
                        "(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)",
                        "{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)",

                }},
                {"xor", TokenTypes.LogicXorWeak, new String[]{
                        "falseType x trueType -> trueType",
                        "trueType x falseType -> trueType",
                        "falseType x falseType -> falseType",
                        "trueType x trueType -> falseType",
                        "(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)",
                        "{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)",
                }},
                {"and", TokenTypes.LogicAndWeak, new String[]{
                        "falseType x (falseType | trueType) -> falseType",
                        "falseType x {as (falseType | trueType)} -> falseType",
                        "(falseType | trueType) x falseType -> falseType",
                        "{as (falseType | trueType)} x falseType -> falseType",
                        "trueType x trueType -> trueType",
                        "(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)",
                        "{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)",
                }},
                {"=", TokenTypes.Assign, new String[]{"Tlhs x Trhs -> Tlhs \\ Trhs <: Tlhs"}},
                {"+=", TokenTypes.PlusAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x float -> Tlhs \\ float <: Tlhs <: float",
                        "Tlhs x {as (float | int)} -> Tlhs \\ float <: Tlhs <: float",
                        "Tlhs x float -> Tlhs \\ float <: Tlhs <: {as (float | int)}",
                        "Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: (float | int)",
                        "Tlhs x array -> Tlhs \\ array <: Tlhs <: array"
                }},
                {"-=", TokenTypes.MinusAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x float -> Tlhs \\ float <: Tlhs <: float",
                        "Tlhs x {as (float | int)} -> Tlhs \\ float <: Tlhs <: float",
                        "Tlhs x float -> Tlhs \\ float <: Tlhs <: {as (float | int)}",
                        "Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: (float | int)",
                }},
                {"*=", TokenTypes.MultiplyAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x float -> Tlhs \\ float <: Tlhs <: float",
                        "Tlhs x {as (float | int)} -> Tlhs \\ float <: Tlhs <: float",
                        "Tlhs x float -> Tlhs \\ float <: Tlhs <: {as (float | int)}",
                        "Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: (float | int)",
                }},
                {"/=", TokenTypes.DivideAssign, new String[]{
                        "Tlhs x float -> Tlhs \\ (falseType | float) <: Tlhs <: (falseType | float)",
                        "Tlhs x float -> Tlhs \\ (falseType | float) <: Tlhs <: {as (float | int)}",
                        "Tlhs x {as (float | int)} -> Tlhs \\ (falseType | float | int) <: Tlhs <: {as (float | int)}"
                }},
                {"%=", TokenTypes.ModuloAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ (falseType | int) <: Tlhs <: (falseType | int)",
                        "Tlhs x {as (float | int)} -> Tlhs \\ (falseType | int) <: Tlhs <: {as (float | int)}",
                }},
                {"&=", TokenTypes.BitwiseAndAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x {as (float | int)} -> Tlhs \\ int <: Tlhs <: {as (float | int)}",
                        "Tlhs x string -> Tlhs \\ string <: Tlhs <: string",
                }},
                {"^=", TokenTypes.BitwiseXorAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x {as (float | int)} -> Tlhs \\ int <: Tlhs <: {as (float | int)}",
                        "Tlhs x string -> Tlhs \\ string <: Tlhs <: string",
                }},
                {"|=", TokenTypes.BitwiseOrAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x {as (float | int)} -> Tlhs \\ int <: Tlhs <: {as (float | int)}",
                        "Tlhs x string -> Tlhs \\ string <: Tlhs <: string",
                }},
                {">>=", TokenTypes.ShiftLeftAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x {as (float | int)} -> Tlhs \\ int <: Tlhs <: {as (float | int)}"
                }},
                {"<<=", TokenTypes.ShiftRightAssign, new String[]{
                        "Tlhs x int -> Tlhs \\ int <: Tlhs <: int",
                        "Tlhs x {as (float | int)} -> Tlhs \\ int <: Tlhs <: {as (float | int)}"
                }},
                {".=", TokenTypes.DotAssign, new String[]{
                        "Tlhs x string -> Tlhs \\ string <: Tlhs <: string",
                        "Tlhs x {as string} -> Tlhs \\ string <: Tlhs <: {as string}"
                }},
                {"?", TokenTypes.QuestionMark, new String[]{
                        "falseType x mixed x Telse -> Treturn \\ Telse <: Treturn",
                        "trueType x Tif x mixed -> Treturn \\ Tif <: Treturn",
                        "(falseType | trueType) x Tif x Telse -> Treturn \\ (Telse | Tif) <: Treturn",
                        "{as (falseType | trueType)} x Tif x Telse -> Treturn \\ (Telse | Tif) <: Treturn"
                }},
                {"||", TokenTypes.LogicOr, new String[]{
                        "falseType x falseType -> falseType",
                        "trueType x (falseType | trueType) -> trueType",
                        "trueType x {as (falseType | trueType)} -> trueType",
                        "(falseType | trueType) x trueType -> trueType",
                        "{as (falseType | trueType)} x trueType -> trueType",
                        "(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)",
                        "{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)",
                }},
                {"&&", TokenTypes.LogicAnd, new String[]{
                        "falseType x (falseType | trueType) -> falseType",
                        "falseType x {as (falseType | trueType)} -> falseType",
                        "(falseType | trueType) x falseType -> falseType",
                        "{as (falseType | trueType)} x falseType -> falseType",
                        "trueType x trueType -> trueType",
                        "(falseType | trueType) x (falseType | trueType) -> (falseType | trueType)",
                        "{as (falseType | trueType)} x {as (falseType | trueType)} -> (falseType | trueType)",
                }},
                {"|", TokenTypes.BitwiseOr, new String[]{
                        "int x int -> int",
                        "{as (float | int)} x {as (float | int)} -> int",
                        "string x string -> string"
                }},
                {"^", TokenTypes.BitwiseXor, new String[]{
                        "int x int -> int",
                        "{as (float | int)} x {as (float | int)} -> int",
                        "string x string -> string"
                }},
                {"&", TokenTypes.BitwiseAnd, new String[]{
                        "int x int -> int",
                        "{as (float | int)} x {as (float | int)} -> int",
                        "string x string -> string"
                }},
                {"==", TokenTypes.Equal, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"===", TokenTypes.Identical, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"!=", TokenTypes.NotEqual, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"!==", TokenTypes.NotIdentical, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"<", TokenTypes.LessThan, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"<=", TokenTypes.LessEqualThan, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {">", TokenTypes.GreaterThan, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {">=", TokenTypes.GreaterEqualThan, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"<<", TokenTypes.ShiftLeft, new String[]{"int x int -> int", "{as (float | int)} x {as (float | int)" +
                        "} -> int"}},
                {">>", TokenTypes.ShiftRight, new String[]{"int x int -> int", "{as (float | int)} x {as (float | " +
                        "int)} -> int"}},
                {"+", TokenTypes.Plus, new String[]{
                        "int x int -> int",
                        "float x float -> float",
                        "float x {as (float | int)} -> float",
                        "{as (float | int)} x float -> float",
                        "{as T} x {as T} -> T \\ T <: (float | int)",
                        "array x array -> array"
                }},
                {"-", TokenTypes.Minus, new String[]{
                        "int x int -> int",
                        "float x float -> float",
                        "float x {as (float | int)} -> float",
                        "{as (float | int)} x float -> float",
                        "{as T} x {as T} -> T \\ T <: (float | int)",
                }},
                {".", TokenTypes.Dot, new String[]{
                        "string x string -> string",
                        "{as string} x {as string} -> string"
                }},
                {"*", TokenTypes.Multiply, new String[]{
                        "int x int -> int",
                        "float x float -> float",
                        "float x {as (float | int)} -> float",
                        "{as (float | int)} x float -> float",
                        "{as T} x {as T} -> T \\ T <: (float | int)",
                }},
                {"/", TokenTypes.Divide, new String[]{
                        "float x float -> (falseType | float)",
                        "float x {as (float | int)} -> (falseType | float)",
                        "{as (float | int)} x float -> (falseType | float)",
                        "{as (float | int)} x {as (float | int)} -> (falseType | float | int)"
                }},
                {"%", TokenTypes.Modulo, new String[]{
                        "int x int -> (falseType | int)",
                        "{as (float | int)} x {as (float | int)} -> (falseType | int)"
                }},
                {"instanceof", TokenTypes.Instanceof, new String[]{"mixed x mixed -> (falseType | trueType)"}},
                {"preIncr", TokenTypes.PRE_INCREMENT, new String[]{
                        "T -> T \\ int <: T <: int",
                        "T -> T \\ float <: T <: float",
                        "T -> T \\ (float | int) <: T <: (float | int)",
                        "T -> T \\ (float | int | string) <: T <: (float | int | string)",
                        "T -> T \\ (int | nullType) <: T <: (int | nullType)",
                        "T -> T"
                }},
                {"preDecr", TokenTypes.PRE_DECREMENT, new String[]{
                        "T -> T \\ int <: T <: int",
                        "T -> T \\ float <: T <: float",
                        "T -> T \\ (float | int) <: T <: (float | int)",
                        "T -> T \\ (float | int | string) <: T <: (float | int | string)",
                        "T -> T"
                }},
                {"cast", TokenTypes.CAST, new String[]{"T x {as T} -> T"}},
                {"@", TokenTypes.At, new String[]{"T -> T"}},
                {"~", TokenTypes.BitwiseNot, new String[]{"int -> int", "float -> int", "string -> string"}},
                {"!", TokenTypes.LogicNot, new String[]{
                        "falseType -> trueType",
                        "trueType -> falseType",
                        "(falseType | trueType) -> (falseType | trueType)"
                }},
                {"uMinus", TokenTypes.UNARY_MINUS, new String[]{
                        "T -> T \\ T <: (falseType | float | int | nullType | string | trueType)"
                }},
                {"uPlus", TokenTypes.UNARY_PLUS, new String[]{
                        "T -> T \\ T <: (falseType | float | int | nullType | string | trueType)"
                }},
                {"clone", TokenTypes.Clone, new String[]{"T -> T"}},
                {"new", TokenTypes.New, new String[]{"T -> T"}},
                {"postIncr", TokenTypes.POST_INCREMENT, new String[]{
                        "T -> T \\ int <: T <: int",
                        "T -> T \\ float <: T <: float",
                        "T -> T \\ (float | int) <: T <: (float | int)",
                        "T -> T \\ (float | int | string) <: T <: (float | int | string)",
                        "T -> T \\ (int | nullType) <: T <: (int | nullType)",
                        "T -> T"
                }},
                {"postDecr", TokenTypes.POST_DECREMENT, new String[]{
                        "T -> T \\ int <: T <: int",
                        "T -> T \\ float <: T <: float",
                        "T -> T \\ (float | int) <: T <: (float | int)",
                        "T -> T \\ (float | int | string) <: T <: (float | int | string)",
                        "T -> T"
                }},
                {"if", TokenTypes.If, new String[]{
                        "(falseType | trueType) -> mixed",
                        "{as (falseType | trueType)} -> mixed",
                }},
                {"while", TokenTypes.While, new String[]{
                        "(falseType | trueType) -> mixed",
                        "{as (falseType | trueType)} -> mixed",
                }},
                {"do", TokenTypes.Do, new String[]{
                        "(falseType | trueType) -> mixed",
                        "{as (falseType | trueType)} -> mixed",
                }},
                {"for", TokenTypes.For, new String[]{
                        "(falseType | trueType) -> mixed",
                        "{as (falseType | trueType)} -> mixed",
                }},
                {"foreach", TokenTypes.Foreach, new String[]{"array x mixed x (int | string) -> mixed"}},
                {"switch", TokenTypes.Switch, new String[]{"(falseType | float | int | string | trueType) -> mixed"}},
                {"throw", TokenTypes.Throw, new String[]{"Exception -> mixed"}},
                {"catch", TokenTypes.Catch, new String[]{"Tlhs x Trhs -> Trhs \\ Tlhs <: Trhs"}},
                {"echo", TokenTypes.Echo, new String[]{"string -> mixed", "{as string} -> mixed"}},
                {"exit", TokenTypes.Exit, new String[]{"int -> mixed", "string -> mixed", "{as string} -> mixed"}},
        });
    }
}
