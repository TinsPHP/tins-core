/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core.gen;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.core.ITypeGeneratorHelper;
import ch.tsphp.tinsphp.core.ITypeProvider;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;

import java.util.HashMap;
import java.util.Map;

//will be generated later on
public class BuiltInTypesProvider implements ITypeProvider
{
    private ITypeGeneratorHelper typeGeneratorHelper;
    private ISymbolFactory symbolFactory;
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<String, ITypeSymbol> builtInTypes;

    public BuiltInTypesProvider(
            ITypeGeneratorHelper theTypeGeneratorHelper,
            ISymbolFactory theSymbolFactory,
            Map<String, ITypeSymbol> thePrimitiveType) {
        typeGeneratorHelper = theTypeGeneratorHelper;
        symbolFactory = theSymbolFactory;
        primitiveTypes = thePrimitiveType;
    }

    private Map<String, ITypeSymbol> createTypes() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        Map<String, ITypeSymbol> unionTypes;

        IClassTypeSymbol _exception = typeGeneratorHelper.createClass("Exception");
        unionTypes = new HashMap<>();
        unionTypes.put(PrimitiveTypeNames.TYPE_NAME_STRING, primitiveTypes.get(PrimitiveTypeNames.TYPE_NAME_STRING));
        unionTypes.put(PrimitiveTypeNames.TYPE_NAME_NULL, primitiveTypes.get(PrimitiveTypeNames.TYPE_NAME_NULL));
        typeGeneratorHelper.defineMethodWithoutParameters(
                _exception, "getMessage()", symbolFactory.createUnionTypeSymbol(unionTypes));
        types.put("\\Exception", _exception);

        IClassTypeSymbol _errorException = typeGeneratorHelper.createClass("ErrorException");
        _errorException.setParent(_exception);
        _errorException.addParentTypeSymbol(_exception);
        types.put("\\ErrorException", _exception);
        return types;
    }

    @Override
    public Map<String, ITypeSymbol> getTypes() {
        if (builtInTypes == null) {
            builtInTypes = createTypes();
        }
        return builtInTypes;
    }

}
