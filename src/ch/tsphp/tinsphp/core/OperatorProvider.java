/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.BoundException;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.constraints.TypeVariableCollection;
import ch.tsphp.tinsphp.symbols.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;

public class OperatorProvider extends AProvider implements IOperatorsProvider
{

    private Map<Integer, IOverloadSymbol> builtInOperators;


    public OperatorProvider(
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            Map<String, ITypeSymbol> thePrimitiveType) {
        super(theSymbolFactory, theOverloadResolver, thePrimitiveType);
    }

    @Override
    public Map<Integer, IOverloadSymbol> getOperators() {
        if (builtInOperators == null) {
            createOperators();
        }
        return builtInOperators;
    }

    private void createOperators() {
        builtInOperators = new HashMap<>();

        addOperatorLists();
        defineLogicOperators();
        defineAssignmentOperators();
        defineBitLevelOperators();
        defineComparisonOperators();
        defineTernaryOperator();
        defineArithmeticOperators();
        defineDotOperator();
        defineInstanceOfOperator();
        defineAtAndCastOperator();
        defineCloneAndNewOperator();
    }

    private void addOperatorLists() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operatorTypes = new Pair[]{
                //binary operators
                pair("or", TokenTypes.LogicOrWeak), pair("xor", TokenTypes.LogicXorWeak),
                pair("and", TokenTypes.LogicAndWeak),
                pair("=", TokenTypes.Assign), pair("+=", TokenTypes.PlusAssign), pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign), pair("/=", TokenTypes.DivideAssign),
                pair("&=", TokenTypes.BitwiseAndAssign), pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("%=", TokenTypes.ModuloAssign), pair(".=", TokenTypes.DotAssign),
                pair("<<=", TokenTypes.ShiftLeftAssign), pair(">>=", TokenTypes.ShiftRightAssign),
                pair("||", TokenTypes.LogicOr), pair("&&", TokenTypes.LogicAnd),
                pair("|", TokenTypes.BitwiseOr), pair("&", TokenTypes.BitwiseAnd), pair("^", TokenTypes.BitwiseXor),
                pair("==", TokenTypes.Equal), pair("===", TokenTypes.Identical),
                pair("!=", TokenTypes.NotEqual), pair("!==", TokenTypes.NotIdentical),
                pair("<", TokenTypes.LessThan), pair("<=", TokenTypes.LessEqualThan),
                pair(">", TokenTypes.GreaterThan), pair(">=", TokenTypes.GreaterEqualThan),
                pair("<<", TokenTypes.ShiftLeft), pair(">>", TokenTypes.ShiftRight),
                pair("+", TokenTypes.Plus), pair("-", TokenTypes.Minus), pair("*", TokenTypes.Multiply),
                pair("/", TokenTypes.Divide), pair("%", TokenTypes.Modulo), pair(".", TokenTypes.Dot),
                pair("instanceof", TokenTypes.Instanceof),
                //unary operators
                pair("cast", TokenTypes.CAST),
                pair("++", TokenTypes.PRE_INCREMENT), pair("--", TokenTypes.PRE_DECREMENT),
                pair("@", TokenTypes.At), pair("~", TokenTypes.BitwiseNot), pair("!", TokenTypes.LogicNot),
                pair("-", TokenTypes.UNARY_MINUS), pair("+", TokenTypes.UNARY_PLUS),
                pair("++", TokenTypes.POST_INCREMENT), pair("--", TokenTypes.POST_DECREMENT),
                pair("clone", TokenTypes.Clone), pair("new", TokenTypes.New),
                //ternary operators
                pair("?", TokenTypes.QuestionMark),
        };

        for (Pair<String, Integer> operatorType : operatorTypes) {
            builtInOperators.put(operatorType.second, symbolFactory.createOverloadSymbol(operatorType.first));
        }
    }

    private void defineLogicOperators() {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] orOperators = new Pair[]{
                pair("or", TokenTypes.LogicOrWeak),
                pair("||", TokenTypes.LogicOr)
        };
        for (Pair<String, Integer> operator : orOperators) {
            //false x false -> false
            addToBinaryOperators(operator, falseTypeConstraint, falseTypeConstraint, falseTypeConstraint);
            //true x bool -> true
            addToBinaryOperators(operator, trueTypeConstraint, boolTypeConstraint, trueTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //true x ~{as bool} -> true

            //bool x true -> true
            addToBinaryOperators(operator, boolTypeConstraint, trueTypeConstraint, trueTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x true -> true

            //bool x bool -> bool
            addToBinaryOperators(operator, boolTypeConstraint, boolTypeConstraint, boolTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool

        }

        Pair<String, Integer> xorWeak = pair("xor", TokenTypes.LogicXorWeak);
        //false x true -> true
        addToBinaryOperators(xorWeak, falseTypeConstraint, trueTypeConstraint, trueTypeConstraint);
        //true x false -> true
        addToBinaryOperators(xorWeak, trueTypeConstraint, falseTypeConstraint, trueTypeConstraint);
        //false x false -> false
        addToBinaryOperators(xorWeak, falseTypeConstraint, falseTypeConstraint, falseTypeConstraint);
        //true x true -> false
        addToBinaryOperators(xorWeak, trueTypeConstraint, trueTypeConstraint, falseTypeConstraint);
        //bool x bool -> bool
        addToBinaryOperators(xorWeak, boolTypeConstraint, boolTypeConstraint, boolTypeConstraint);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x ~{as bool} -> bool

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] andOperators = new Pair[]{
                pair("and", TokenTypes.LogicAndWeak),
                pair("&&", TokenTypes.LogicAnd)
        };
        for (Pair<String, Integer> operator : andOperators) {
            //false x bool -> false
            addToBinaryOperators(operator, falseTypeConstraint, boolTypeConstraint, falseTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //false x ~{as bool} -> false

            //bool x false -> false
            addToBinaryOperators(operator, boolTypeConstraint, falseTypeConstraint, falseTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{bool} x false -> false

            //true x true -> true
            addToBinaryOperators(operator, trueTypeConstraint, trueTypeConstraint, trueTypeConstraint);
            //bool x bool -> bool
            addToBinaryOperators(operator, boolTypeConstraint, boolTypeConstraint, boolTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as bool} x ~{as bool} -> bool
        }

        Pair<String, Integer> logicNot = new Pair<>("!", TokenTypes.LogicNot);
        addToUnaryOperators(logicNot, falseTypeConstraint, trueTypeConstraint);
        addToUnaryOperators(logicNot, trueTypeConstraint, falseTypeConstraint);
        addToUnaryOperators(logicNot, boolTypeConstraint, boolTypeConstraint);
    }

    private void defineAssignmentOperators() throws LowerBoundException {
        //Tlhs x Trhs -> Tlhs \ Tlhs > Trhs
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound(T_LHS, new TypeVariableConstraint(T_RHS));
        IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                "=", collection, binaryParameterIds, T_LHS);
        addToOperators(TokenTypes.Assign, function);

        //Other assignment operators can be found in the corresponding sections.
        //For instance, += is in createAssignmentOperators
    }

    private void defineBitLevelOperators() throws BoundException {

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] intResultingNonAssignOperators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd),
                pair("<<", TokenTypes.ShiftLeft),
                pair(">>", TokenTypes.ShiftRight),
        };
        for (Pair<String, Integer> operator : intResultingNonAssignOperators) {
            //int x int -> int
            addToBinaryOperators(operator, intTypeConstraint, intTypeConstraint, intTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as int} x ~{as int} -> int
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] intResultingAssignOperators = new Pair[]{
                pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("&=", TokenTypes.BitwiseAndAssign),
                pair("<<=", TokenTypes.ShiftLeftAssign),
                pair(">>=", TokenTypes.ShiftRightAssign),
        };
        for (Pair<String, Integer> operator : intResultingAssignOperators) {
            //int x int -> int
            addToBinaryOperators(operator, intTypeConstraint, intTypeConstraint, intTypeConstraint);
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(int | ~{as int}) x ~{as int} -> int
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] stringResultingOperators = new Pair[]{
                pair("|", TokenTypes.BitwiseOr),
                pair("^", TokenTypes.BitwiseXor),
                pair("&", TokenTypes.BitwiseAnd),
                pair("|=", TokenTypes.BitwiseOrAssign),
                pair("^=", TokenTypes.BitwiseXorAssign),
                pair("&=", TokenTypes.BitwiseAndAssign),
        };
        for (Pair<String, Integer> operator : stringResultingOperators) {
            //string x string -> string
            addToBinaryOperators(operator, stringTypeConstraint, stringTypeConstraint, stringTypeConstraint);
        }

        Pair<String, Integer> bitwiseNot = pair("~", TokenTypes.BitwiseNot);
        //int -> int
        addToUnaryOperators(bitwiseNot, intTypeConstraint, intTypeConstraint);
        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} -> int

        //string -> string
        addToUnaryOperators(bitwiseNot, stringTypeConstraint, stringTypeConstraint);
    }

    private void defineComparisonOperators() throws BoundException {

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("==", TokenTypes.Equal),
                pair("===", TokenTypes.Identical),
                pair("!=", TokenTypes.NotEqual),
                pair("!==", TokenTypes.NotIdentical),
                pair("<", TokenTypes.LessThan),
                pair("<=", TokenTypes.LessEqualThan),
                pair(">", TokenTypes.GreaterThan),
                pair(">=", TokenTypes.GreaterEqualThan),
        };
        for (Pair<String, Integer> operator : operators) {
            addToBinaryOperators(operator, mixedTypeConstraint, mixedTypeConstraint, boolTypeConstraint);
        }
    }

    private void defineTernaryOperator() throws BoundException {
        final String T_COND = "Tcond";
        final String T_FALSE = "Tfalse";
        final String T_TRUE = "Ttrue";
        List<String> parameterIds = Arrays.asList(T_COND, T_TRUE, T_FALSE);

        //false x mixed x Tfalse -> Tfalse
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound(T_COND, falseTypeConstraint);
        collection.addUpperBound(T_COND, falseTypeConstraint);
        IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                "=", collection, parameterIds, T_FALSE);
        addToOperators(TokenTypes.QuestionMark, function);


        //true x Ttrue x mixed -> Ttrue
        collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound(T_COND, trueTypeConstraint);
        collection.addUpperBound(T_COND, trueTypeConstraint);
        function = symbolFactory.createFunctionTypeSymbol(
                "=", collection, parameterIds, T_TRUE);
        addToOperators(TokenTypes.QuestionMark, function);

        //bool x Ttrue x Tfalse -> (Ttrue | Tfalse)
        collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound(T_COND, boolTypeConstraint);
        collection.addUpperBound(T_COND, boolTypeConstraint);
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(T_FALSE));
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint(T_TRUE));
        function = symbolFactory.createFunctionTypeSymbol(
                "=", collection, parameterIds, T_RETURN);
        addToOperators(TokenTypes.QuestionMark, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as bool} x Ttrue x Tfalse -> (Ttrue | Tfalse)
    }

    private void defineArithmeticOperators() throws BoundException {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] operators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply),
                pair("+=", TokenTypes.PlusAssign),
                pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign),
        };

        for (Pair<String, Integer> operator : operators) {
            //T x T -> T \ T < num
            ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
            collection.addUpperBound("T", numTypeConstraint);
            IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                    operator.first, collection, Arrays.asList("T", "T"), "T");
            addToOperators(operator.second, function);
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] nonAssignOperators = new Pair[]{
                pair("+", TokenTypes.Plus),
                pair("-", TokenTypes.Minus),
                pair("*", TokenTypes.Multiply)
        };
        for (Pair<String, Integer> operator : nonAssignOperators) {
            //bool x bool -> int
            addToBinaryOperators(operator, boolTypeConstraint, boolTypeConstraint, intTypeConstraint);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as T} x ~{as T} -> T \ T< num
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] assignOperators = new Pair[]{
                pair("+=", TokenTypes.PlusAssign),
                pair("-=", TokenTypes.MinusAssign),
                pair("*=", TokenTypes.MultiplyAssign),
        };
        for (Pair<String, Integer> operator : assignOperators) {
            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(T | ~{as T}) x T -> T \ T < num
            //(T | ~{as T}) x ~{as T} -> T \ T < num
        }

        //array x array -> array
        addToBinaryOperators(pair("+", TokenTypes.Plus), arrayTypeConstraint, arrayTypeConstraint, arrayTypeConstraint);
        addToBinaryOperators(
                pair("+=", TokenTypes.PlusAssign), arrayTypeConstraint, arrayTypeConstraint, arrayTypeConstraint);

        createDivOperators();
        createModuloOperators();
        createUnaryArithmeticOperators();
    }

    private void createDivOperators() throws BoundException {
        IFunctionTypeSymbol function;

        //bool x bool -> (int | false)
        addToBinaryOperators(
                pair("/", TokenTypes.Divide), boolTypeConstraint, boolTypeConstraint, intOrFalseTypeConstraint);


        //T x T -> (T | false) \ float < T < num
        //expanded: T x T -> Treturn \ float < T < num, Treturn > T, Treturn > false
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound("T", floatTypeConstraint);
        collection.addUpperBound("T", numTypeConstraint);
        collection.addLowerBound(T_RETURN, new TypeVariableConstraint("T"));
        collection.addLowerBound(T_RETURN, falseTypeConstraint);
        function = symbolFactory.createFunctionTypeSymbol("/", collection, Arrays.asList("T", "T"), T_RETURN);
        addToOperators(TokenTypes.Divide, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //float x ~{as float} -> (float | false)
        //~{as float} x float -> (float | false)
        //~{as num} x ~{as num} -> (num | false)


        //(int | bool) x bool -> (int | false)
        IUnionTypeSymbol intOrBoolTypeSymbol = symbolFactory.createUnionTypeSymbol();
        intOrBoolTypeSymbol.addTypeSymbol(intTypeSymbol);
        intOrBoolTypeSymbol.addTypeSymbol(boolTypeSymbol);
        intOrBoolTypeSymbol.seal();
        addToBinaryOperators(
                pair("/=", TokenTypes.DivideAssign),
                new TypeConstraint(intOrBoolTypeSymbol),
                boolTypeConstraint,
                intOrFalseTypeConstraint);

        //(T | false) x T -> (T | false) \ float < T < num
        //expanded: Tlhs x T -> Tlhs \ float < T < num, Tlhs > T, Tlhs > false
        collection = new TypeVariableCollection(overloadResolver);
        collection.addLowerBound("T", floatTypeConstraint);
        collection.addUpperBound("T", numTypeConstraint);
        collection.addLowerBound(T_LHS, new TypeVariableConstraint("T"));
        collection.addLowerBound(T_LHS, falseTypeConstraint);
        function = symbolFactory.createFunctionTypeSymbol("/=", collection, Arrays.asList(T_LHS, "T"), T_LHS);
        addToOperators(TokenTypes.DivideAssign, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(T | ~{as T}) x ~{as T} -> (T | false) \ float < T < num
    }

    private void createModuloOperators() throws BoundException {
        //int x int -> (int | false)
        Pair<String, Integer> modulo = pair("%", TokenTypes.Modulo);
        addToBinaryOperators(modulo, intTypeConstraint, intTypeConstraint, intOrFalseTypeConstraint);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as int} x ~{as int} -> (int | false)

        //(int | false) x int -> (int | false)
        Pair<String, Integer> moduloAssign = pair("%=", TokenTypes.ModuloAssign);
        addToBinaryOperators(moduloAssign, intOrFalseTypeConstraint, intTypeConstraint, intOrFalseTypeConstraint);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(int | ~{as int}) x ~{as int} -> (int | false)
    }

    private void createUnaryArithmeticOperators() throws BoundException {
        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] incrDecrOperators = new Pair[]{
                pair("++", TokenTypes.PRE_INCREMENT),
                pair("++", TokenTypes.POST_INCREMENT),
                pair("--", TokenTypes.PRE_DECREMENT),
                pair("--", TokenTypes.POST_DECREMENT),
        };

        IUnionTypeSymbol numOrBoolTypeSymbol = symbolFactory.createUnionTypeSymbol();
        numOrBoolTypeSymbol.addTypeSymbol(numOrBoolTypeSymbol);
        numOrBoolTypeSymbol.addTypeSymbol(boolTypeSymbol);
        numOrBoolTypeSymbol.seal();

        for (Pair<String, Integer> operator : incrDecrOperators) {
            //T -> T \ T < (num | bool)
            ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
            collection.addUpperBound("T", boolTypeConstraint);
            collection.addUpperBound("T", numTypeConstraint);
            IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                    operator.first, collection, Arrays.asList("T"), "T");
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //(T | ~T) -> T \ T < num
        }

        @SuppressWarnings("unchecked")
        Pair<String, Integer>[] unaryPlusMinusOperators = new Pair[]{
                pair("-", TokenTypes.UNARY_MINUS),
                pair("+", TokenTypes.UNARY_PLUS)
        };
        for (Pair<String, Integer> operator : unaryPlusMinusOperators) {
            //bool x int
            addToUnaryOperators(operator, boolTypeConstraint, intTypeConstraint);
            //T -> T \ T < num
            ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
            collection.addUpperBound("T", numTypeConstraint);
            IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                    operator.first, collection, Arrays.asList("T"), "T");
            addToOperators(operator.second, function);

            //TODO rstoll TINS-347 create overloads for conversion constraints
            //~{as num} -> num
        }
    }

    private void defineDotOperator() throws BoundException {
        addToBinaryOperators(
                pair(".", TokenTypes.Dot), stringTypeConstraint, stringTypeConstraint, stringTypeConstraint);
        addToBinaryOperators(
                pair(".=", TokenTypes.DotAssign), stringTypeConstraint, stringTypeConstraint, stringTypeConstraint);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //~{as string} x ~{as string} -> string

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //(string | ~{as string}) x ~{as string} -> string
    }

    private void defineInstanceOfOperator() throws BoundException {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: object x mixed -> bool
        //mixed x mixed -> bool
        Pair<String, Integer> instanceOf = pair("instanceof", TokenTypes.Instanceof);
        addToBinaryOperators(instanceOf, mixedTypeConstraint, mixedTypeConstraint, boolTypeConstraint);
    }

    private void defineCloneAndNewOperator() {
        //TODO rstoll TINS-332 introduce object pseudo type
        //more precise would be: T -> T \ T < object
        //T -> T
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                "clone", collection, Arrays.asList("T"), "T");
        addToOperators(TokenTypes.Clone, function);

        //TODO TINS-349 structural constraints
        //not all classes return itself, some return null as well in error cases
        //see https://wiki.php.net/rfc/internal_constructor_behaviour
        //T -> T
        collection = new TypeVariableCollection(overloadResolver);
        function = symbolFactory.createFunctionTypeSymbol(
                "new", collection, Arrays.asList("T"), "T");
        addToOperators(TokenTypes.New, function);
    }

    private void defineAtAndCastOperator() {
        //T -> T
        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);
        IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                "@", collection, Arrays.asList("T"), "T");
        addToOperators(TokenTypes.At, function);

        //TODO rstoll TINS-347 create overloads for conversion constraints
        //T1 x T2 -> T1 \ T2 < ~{as T1}
        //T1 x T2 -> T1
        collection = new TypeVariableCollection(overloadResolver);
        function = symbolFactory.createFunctionTypeSymbol(
                "cast", collection, Arrays.asList("T1", "T2"), "T1");
        addToOperators(TokenTypes.CAST, function);
    }

    private void addToBinaryOperators(
            Pair<String, Integer> operator,
            TypeConstraint leftBound,
            TypeConstraint rightBound,
            TypeConstraint returnBound) {

        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);

        collection.addLowerBound(T_LHS, leftBound);
        collection.addUpperBound(T_LHS, leftBound);
        collection.addLowerBound(T_RHS, rightBound);
        collection.addUpperBound(T_RHS, rightBound);
        collection.addLowerBound(T_RETURN, returnBound);
        collection.addUpperBound(T_RETURN, returnBound);

        IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                operator.first, collection, binaryParameterIds, T_RETURN);

        addToOperators(operator.second, function);
    }

    private void addToUnaryOperators(
            Pair<String, Integer> operator, TypeConstraint formalBound, TypeConstraint returnBound) {

        ITypeVariableCollection collection = new TypeVariableCollection(overloadResolver);

        collection.addLowerBound(T_EXPR, formalBound);
        collection.addUpperBound(T_EXPR, formalBound);
        collection.addLowerBound(T_RETURN, returnBound);
        collection.addUpperBound(T_RETURN, returnBound);

        IFunctionTypeSymbol function = symbolFactory.createFunctionTypeSymbol(
                operator.first, collection, unaryParameterId, T_RETURN);

        addToOperators(operator.second, function);
    }

    private void addToOperators(int operatorType, IFunctionTypeSymbol functionTypeSymbol) {
        IOverloadSymbol overloadSymbol = builtInOperators.get(operatorType);
        overloadSymbol.addOverload(functionTypeSymbol);
    }

}

