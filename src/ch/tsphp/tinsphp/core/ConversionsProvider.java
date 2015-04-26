/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.core;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.BuiltInConversionMethod;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;

import java.util.HashMap;
import java.util.Map;

public class ConversionsProvider implements IConversionsProvider
{
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> implicitConversions;
    private Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> explicitConversions;


    public ConversionsProvider(Map<String, ITypeSymbol> thePrimitiveTypes) {
        primitiveTypes = thePrimitiveTypes;
    }

    @Override
    public Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> getImplicitConversions() {
        if (implicitConversions == null) {
            implicitConversions = createImplicitConversions();
        }
        return implicitConversions;
    }

    private Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> createImplicitConversions() {
        ITypeSymbol nullTypeTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.NULL_TYPE);
        ITypeSymbol boolTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.BOOL);
        ITypeSymbol intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        ITypeSymbol floatTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.FLOAT);
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);
        ITypeSymbol numTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.NUM);
        ITypeSymbol arrayTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.ARRAY);
        ITypeSymbol mixedTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.MIXED);

        ITypeSymbol[][] castings = new ITypeSymbol[][]{
                {nullTypeTypeSymbol, boolTypeSymbol},
                {nullTypeTypeSymbol, intTypeSymbol},
                {nullTypeTypeSymbol, floatTypeSymbol},
                {nullTypeTypeSymbol, stringTypeSymbol},
                {nullTypeTypeSymbol, arrayTypeSymbol},
                //bool
                {boolTypeSymbol, intTypeSymbol},
                {boolTypeSymbol, floatTypeSymbol},
                {boolTypeSymbol, stringTypeSymbol},
                //int
                {intTypeSymbol, floatTypeSymbol},
                //num
                {numTypeSymbol, stringTypeSymbol},
                //mixed
                {mixedTypeSymbol, boolTypeSymbol},
                {mixedTypeSymbol, arrayTypeSymbol}
        };

        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> conversions = new HashMap<>();
        for (ITypeSymbol[] fromTo : castings) {
            addToCastings(conversions, fromTo[0], fromTo[1], new BuiltInConversionMethod(fromTo[1]));
        }
        return conversions;
    }

    private static void addToCastings(Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> castings,
            ITypeSymbol from, ITypeSymbol to, IConversionMethod castingMethod) {
        if (!castings.containsKey(from)) {
            castings.put(from, new HashMap<ITypeSymbol, IConversionMethod>());
        }
        castings.get(from).put(to, castingMethod);
    }

    @Override
    public Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> getExplicitConversions() {
        if (explicitConversions == null) {
            explicitConversions = createExplicitConversions();
        }
        return explicitConversions;

    }

    private Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> createExplicitConversions() {
        ITypeSymbol intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        ITypeSymbol floatTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.FLOAT);
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);

        ITypeSymbol[][] castings = new ITypeSymbol[][]{
                {floatTypeSymbol, intTypeSymbol},
                {stringTypeSymbol, floatTypeSymbol},
                {stringTypeSymbol, intTypeSymbol},
        };

        Map<ITypeSymbol, Map<ITypeSymbol, IConversionMethod>> conversions = new HashMap<>();
        for (ITypeSymbol[] fromTo : castings) {
            addToCastings(conversions, fromTo[0], fromTo[1], new BuiltInConversionMethod(fromTo[1]));
        }
        return conversions;
    }

}
