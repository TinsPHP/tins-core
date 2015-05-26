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
import ch.tsphp.tinsphp.common.utils.Pair;

import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;

public class ConversionsProvider implements IConversionsProvider
{
    private final Map<String, ITypeSymbol> primitiveTypes;
    private Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions;
    private Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions;


    public ConversionsProvider(Map<String, ITypeSymbol> thePrimitiveTypes) {
        primitiveTypes = thePrimitiveTypes;
    }

    @Override
    public Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> getImplicitConversions() {
        if (implicitConversions == null) {
            implicitConversions = createImplicitConversions();
        }
        return implicitConversions;
    }

    private Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> createImplicitConversions() {
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

        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionsMap = new HashMap<>();
        for (ITypeSymbol[] fromTo : castings) {
            addToConversions(conversionsMap, fromTo[0], fromTo[1], new BuiltInConversionMethod(fromTo[1]));
        }
        return conversionsMap;
    }

    private static void addToConversions(Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap,
            ITypeSymbol from, ITypeSymbol to, IConversionMethod castingMethod) {
        String absoluteName = from.getAbsoluteName();
        Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions = conversionMap.get(absoluteName);
        if (conversions == null) {
            conversions = new HashMap<>();
            conversionMap.put(absoluteName, conversions);
        }
        conversions.put(to.getAbsoluteName(), pair(to, castingMethod));
    }

    @Override
    public Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> getExplicitConversions() {
        if (explicitConversions == null) {
            explicitConversions = createExplicitConversions();
        }
        return explicitConversions;

    }

    private Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> createExplicitConversions() {
        ITypeSymbol intTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.INT);
        ITypeSymbol floatTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.FLOAT);
        ITypeSymbol stringTypeSymbol = primitiveTypes.get(PrimitiveTypeNames.STRING);

        ITypeSymbol[][] castings = new ITypeSymbol[][]{
                {floatTypeSymbol, intTypeSymbol},
                {stringTypeSymbol, floatTypeSymbol},
                {stringTypeSymbol, intTypeSymbol},
        };

        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionsMap = new HashMap<>();
        for (ITypeSymbol[] fromTo : castings) {
            addToConversions(conversionsMap, fromTo[0], fromTo[1], new BuiltInConversionMethod(fromTo[1]));
        }
        return conversionsMap;
    }

}
