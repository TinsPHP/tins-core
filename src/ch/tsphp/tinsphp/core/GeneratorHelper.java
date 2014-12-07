/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.HashMap;
import java.util.Map;

public class GeneratorHelper implements IGeneratorHelper
{
    private final IAstHelper astHelper;
    private final ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;

    public GeneratorHelper(
            IAstHelper theAstHelper, ISymbolFactory theSymbolFactory, Map<String, ITypeSymbol> thePrimitiveTypes) {
        astHelper = theAstHelper;
        symbolFactory = theSymbolFactory;
        primitiveTypes = thePrimitiveTypes;
    }

    @Override
    public IClassTypeSymbol createClass(String name) {
        ITSPHPAst classModifier = astHelper.createAst(TokenTypes.CLASS_MODIFIER, "cMod");
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.TYPE_NAME, name);
        return symbolFactory.createClassTypeSymbol(classModifier, identifier, null);
    }

    @Override
    public IMethodSymbol defineMethod(IClassTypeSymbol classTypeSymbol, String name, ITypeSymbol returnType) {
        ITSPHPAst methodModifier = astHelper.createAst(TokenTypes.METHOD_MODIFIER, "mMod");
        ITSPHPAst returnTypeModifier = astHelper.createAst(TokenTypes.TYPE_MODIFIER, "tMod");
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.Identifier, name);
        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(
                methodModifier, returnTypeModifier, identifier, classTypeSymbol);
        methodSymbol.setType(returnType);
        classTypeSymbol.define(methodSymbol);
        return methodSymbol;
    }

    @Override
    public IMethodSymbol createFunction(String name, ITypeSymbol returnType) {
        ITSPHPAst functionModifier = astHelper.createAst(TokenTypes.FUNCTION_MODIFIER, "fMod");
        ITSPHPAst returnTypeModifier = astHelper.createAst(TokenTypes.TYPE_MODIFIER, "tMod");
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.Identifier, name);
        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(
                functionModifier, returnTypeModifier, identifier, null);
        methodSymbol.setType(returnType);
        return methodSymbol;
    }

    @Override
    public IVariableSymbol createParameter(IMethodSymbol function, String name, ITypeSymbol typeSymbol) {
        ITSPHPAst typeModifier = astHelper.createAst(TokenTypes.TYPE_MODIFIER, "tMod");
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.Identifier, name);
        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(typeModifier, identifier);
        variableSymbol.setType(typeSymbol);
        function.addParameter(variableSymbol);
        return variableSymbol;
    }

    @Override
    public IVariableSymbol createConstant(String name, ITypeSymbol typeSymbol) {
        ITSPHPAst typeModifier = astHelper.createAst(TokenTypes.TYPE_MODIFIER, "tMod");
        typeModifier.addChild(astHelper.createAst(TokenTypes.Final, "final"));
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.Identifier, name);
        IVariableSymbol constant = symbolFactory.createVariableSymbol(typeModifier, identifier);
        constant.setType(typeSymbol);
        return constant;
    }

    @Override
    public IUnionTypeSymbol createUnionTypeSymbolFromPrimitives(String... types) {
        Map<String, ITypeSymbol> unionTypes;
        unionTypes = new HashMap<>();
        for (String type : types) {
            if (primitiveTypes.containsKey(type)) {
                unionTypes.put(type, primitiveTypes.get(type));
            } else {
                throw new IllegalArgumentException(type + " was not found in the primitive types.");
            }
        }
        return symbolFactory.createUnionTypeSymbol(unionTypes);
    }
}
