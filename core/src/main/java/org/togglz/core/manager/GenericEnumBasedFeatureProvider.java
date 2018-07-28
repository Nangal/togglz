package org.togglz.core.manager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.togglz.core.GenericEnumFeature;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.metadata.enums.GenericEnumFeatureMetaData;
import org.togglz.core.spi.FeatureProvider;

public class GenericEnumBasedFeatureProvider implements FeatureProvider {

    private final Map<String, FeatureMetaData> metaDataCache = new HashMap<>();
    private final Map<Enum<?>, Feature> features = new LinkedHashMap<>();

    public GenericEnumBasedFeatureProvider() {
        // nothing to do
    }

    @SafeVarargs
    public GenericEnumBasedFeatureProvider(Class<? extends Enum<?>>... featureEnums) {
        if (featureEnums == null) {
            throw new IllegalArgumentException("The featureEnums argument must not be null");
        }
        for (Class<? extends Enum<?>> featureEnum : featureEnums) {
            addFeatureEnum(featureEnum);
        }
    }

    public GenericEnumBasedFeatureProvider addFeatureEnum(Class<? extends Enum<?>> featureEnum) {
        if (featureEnum == null) {
            throw new IllegalArgumentException("The featureEnum argument must be an enum");
        }
        addFeatures(Arrays.asList(featureEnum.getEnumConstants()));
        return this;
    }

    private void addFeatures(Collection<? extends Enum<?>> newEnumValues) {
        for (Enum<?> newEnumValue : newEnumValues) {
            Feature newFeature = new GenericEnumFeature(newEnumValue);
            if (metaDataCache.put(newFeature.name(), new GenericEnumFeatureMetaData(newEnumValue, newFeature)) != null) {
                throw new IllegalStateException("The feature " + newFeature + " has already been added");
            };
            features.put(newEnumValue, newFeature);
        }
    }

    @Override
    public Set<Feature> getFeatures() {
        return new LinkedHashSet<>(features.values());
    }

    @Override
    public FeatureMetaData getMetaData(Feature feature) {
        return metaDataCache.get(feature.name());
    }

    public FeatureMetaData getMetaData(Enum<?> enumValue) {
        return getMetaData(features.get(enumValue));
    }
}
