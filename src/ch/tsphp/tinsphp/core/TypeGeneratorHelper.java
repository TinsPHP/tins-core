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
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

public class TypeGeneratorHelper implements ITypeGeneratorHelper
{
    private final IAstHelper astHelper;
    private final ISymbolFactory symbolFactory;

    public TypeGeneratorHelper(IAstHelper theAstHelper, ISymbolFactory theSymbolFactory) {
        astHelper = theAstHelper;
        symbolFactory = theSymbolFactory;
    }

    @Override
    public IClassTypeSymbol createClass(String className) {
        ITSPHPAst classModifier = astHelper.createAst(TokenTypes.CLASS_MODIFIER, "cMod");
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.TYPE_NAME, className);
        return symbolFactory.createClassTypeSymbol(classModifier, identifier, null);
    }

    @Override
    public IMethodSymbol defineMethodWithoutParameters(IClassTypeSymbol classTypeSymbol, String methodName,
            ITypeSymbol returnType) {
        ITSPHPAst methodModifier = astHelper.createAst(TokenTypes.METHOD_MODIFIER, "mMod");
        methodModifier.addChild(astHelper.createAst(TokenTypes.Public, "public"));
        ITSPHPAst returnTypeModifier = astHelper.createAst(TokenTypes.TYPE_MODIFIER, "tMod");
        ITSPHPAst identifier = astHelper.createAst(TokenTypes.Identifier, methodName);
        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(
                methodModifier, returnTypeModifier, identifier, classTypeSymbol);
        methodSymbol.setType(returnType);
        classTypeSymbol.define(methodSymbol);
        return methodSymbol;
    }
}
