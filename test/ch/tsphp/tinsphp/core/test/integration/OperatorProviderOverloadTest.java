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
import static org.hamcrest.Matchers.containsInAnyOrder;


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
            System.out.println(operatorName + " failed:");
            throw ex;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"or", TokenTypes.LogicOrWeak, new String[]{
                        "falseType x falseType -> falseType",
                        "trueType x bool -> trueType",
                        "bool x trueType -> trueType",
                        "bool x bool -> bool"
                }},
                {"xor", TokenTypes.LogicXorWeak, new String[]{
                        "falseType x trueType -> trueType",
                        "trueType x falseType -> trueType",
                        "falseType x falseType -> falseType",
                        "trueType x trueType -> falseType",
                        "bool x bool -> bool",
                }},
                {"and", TokenTypes.LogicAndWeak, new String[]{
                        "falseType x bool -> falseType",
                        "bool x falseType -> falseType",
                        "trueType x trueType -> trueType",
                        "bool x bool -> bool",
                }},
                {"=", TokenTypes.Assign, new String[]{"Tlhs x Trhs -> Tlhs \\ Trhs < Tlhs"}},
                {"+=", TokenTypes.PlusAssign, new String[]{"T x T -> T \\ T < num", "array x array -> array"}},
                {"-=", TokenTypes.MinusAssign, new String[]{"T x T -> T \\ T < num"}},
                {"*=", TokenTypes.MultiplyAssign, new String[]{"T x T -> T \\ T < num"}},
                {"/=", TokenTypes.DivideAssign, new String[]{
                        "(bool | int) x bool -> (falseType | int)",
                        "Tlhs x Trhs -> Tlhs \\ (falseType | float | Trhs) < Tlhs, float < Trhs < num",
                }},
                {"%=", TokenTypes.ModuloAssign, new String[]{"(falseType | int) x int -> (falseType | int)"}},
                {"&=", TokenTypes.BitwiseAndAssign, new String[]{"int x int -> int", "string x string -> string"}},
                {"^=", TokenTypes.BitwiseXorAssign, new String[]{"int x int -> int", "string x string -> string"}},
                {"|=", TokenTypes.BitwiseOrAssign, new String[]{"int x int -> int", "string x string -> string"}},
                {">>=", TokenTypes.ShiftLeftAssign, new String[]{"int x int -> int"}},
                {"<<=", TokenTypes.ShiftRightAssign, new String[]{"int x int -> int"}},
                {".=", TokenTypes.DotAssign, new String[]{"string x string -> string"}},
                {"?", TokenTypes.QuestionMark, new String[]{
                        "falseType x mixed x Telse -> Treturn \\ Telse < Treturn",
                        "trueType x Tif x mixed -> Treturn \\ Tif < Treturn",
                        "bool x Tif x Telse -> Treturn \\ (Telse | Tif) < Treturn"
                }},
                {"||", TokenTypes.LogicOr, new String[]{
                        "falseType x falseType -> falseType",
                        "trueType x bool -> trueType",
                        "bool x trueType -> trueType",
                        "bool x bool -> bool"
                }},
                {"&&", TokenTypes.LogicAnd, new String[]{
                        "falseType x bool -> falseType",
                        "bool x falseType -> falseType",
                        "trueType x trueType -> trueType",
                        "bool x bool -> bool",
                }},
                {"|", TokenTypes.BitwiseOr, new String[]{"int x int -> int", "string x string -> string"}},
                {"^", TokenTypes.BitwiseXor, new String[]{"int x int -> int", "string x string -> string"}},
                {"&", TokenTypes.BitwiseAnd, new String[]{"int x int -> int", "string x string -> string"}},
                {"==", TokenTypes.Equal, new String[]{"mixed x mixed -> bool"}},
                {"===", TokenTypes.Identical, new String[]{"mixed x mixed -> bool"}},
                {"!=", TokenTypes.NotEqual, new String[]{"mixed x mixed -> bool"}},
                {"!==", TokenTypes.NotIdentical, new String[]{"mixed x mixed -> bool"}},
                {"<", TokenTypes.LessThan, new String[]{"mixed x mixed -> bool"}},
                {"<=", TokenTypes.LessEqualThan, new String[]{"mixed x mixed -> bool"}},
                {">", TokenTypes.GreaterThan, new String[]{"mixed x mixed -> bool"}},
                {">=", TokenTypes.GreaterEqualThan, new String[]{"mixed x mixed -> bool"}},
                {"<<", TokenTypes.ShiftLeft, new String[]{"int x int -> int"}},
                {">>", TokenTypes.ShiftRight, new String[]{"int x int -> int"}},
                {"+", TokenTypes.Plus, new String[]{
                        "bool x bool -> int",
                        "T x T -> T \\ T < num",
                        "array x array -> array"
                }},
                {"-", TokenTypes.Minus, new String[]{"bool x bool -> int", "T x T -> T \\ T < num"}},
                {".", TokenTypes.Dot, new String[]{"string x string -> string"}},
                {"*", TokenTypes.Multiply, new String[]{"bool x bool -> int", "T x T -> T \\ T < num"}},
                {"/", TokenTypes.Divide, new String[]{
                        "bool x bool -> (falseType | int)",
                        "T x T -> Treturn \\ float < T < num, (falseType | float | T) < Treturn",
                }},
                {"%", TokenTypes.Modulo, new String[]{"int x int -> (falseType | int)"}},
                {"instanceof", TokenTypes.Instanceof, new String[]{"mixed x mixed -> bool"}},
                {"cast", TokenTypes.CAST, new String[]{"Tlhs x Trhs -> Treturn \\ Tlhs < Treturn"}},
                {"clone", TokenTypes.Clone, new String[]{"T -> T"}},
                {"new", TokenTypes.New, new String[]{"T -> T"}},
                {"preIncr", TokenTypes.PRE_INCREMENT, new String[]{"T -> T \\ T < (bool | num)"}},
                {"postDecr", TokenTypes.PRE_DECREMENT, new String[]{"T -> T \\ T < (bool | num)"}},
                {"@", TokenTypes.At, new String[]{"T -> T"}},
                {"~", TokenTypes.BitwiseNot, new String[]{"int -> int", "string -> string"}},
                {"!", TokenTypes.LogicNot, new String[]{
                        "falseType -> trueType",
                        "trueType -> falseType",
                        "bool -> bool"
                }},
                {"uMinus", TokenTypes.UNARY_MINUS, new String[]{"bool -> int", "T -> T \\ T < num"}},
                {"uPlus", TokenTypes.UNARY_PLUS, new String[]{"bool -> int", "T -> T \\ T < num"}},
                {"postIncr", TokenTypes.POST_INCREMENT, new String[]{"T -> T \\ T < (bool | num)"}},
                {"postDecr", TokenTypes.POST_DECREMENT, new String[]{"T -> T \\ T < (bool | num)"}},
                {"if", TokenTypes.If, new String[]{"bool -> mixed"}},
                {"while", TokenTypes.While, new String[]{"bool -> mixed"}},
                {"do", TokenTypes.Do, new String[]{"bool -> mixed"}},
                {"for", TokenTypes.For, new String[]{"bool -> mixed"}},
                {"foreach", TokenTypes.Foreach, new String[]{"array x mixed x (int | string) -> mixed"}},
                {"switch", TokenTypes.Switch, new String[]{"scalar -> mixed"}},
                {"throw", TokenTypes.Throw, new String[]{"Exception -> mixed"}},
                {"catch", TokenTypes.Catch, new String[]{"Tlhs x Trhs -> Trhs \\ Tlhs < Trhs"}},
                {"echo", TokenTypes.Echo, new String[]{"string -> mixed"}},
                {"exit", TokenTypes.Exit, new String[]{"int -> mixed", "string -> mixed"}},
        });
    }
}
