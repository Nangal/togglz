package org.togglz.core.metadata.enums;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.togglz.core.Feature;
import org.togglz.core.annotation.ActivationParameter;
import org.togglz.core.annotation.DefaultActivationStrategy;
import org.togglz.core.metadata.FeatureGroup;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.util.EnumAnnotations;
import org.togglz.core.util.FeatureAnnotations;

public class GenericEnumFeatureMetaData implements FeatureMetaData {

    private final String label;

    private final FeatureState defaultFeatureState;

    private final Set<FeatureGroup> groups = new HashSet<>();

    private final Map<String, String> attributes = new LinkedHashMap<>();

    public GenericEnumFeatureMetaData(Enum<?> enumValue, Feature feature) {

        // lookup label via @Label annotation
        this.label = EnumAnnotations.getLabel(enumValue);

        // lookup default via @EnabledByDefault
        boolean enabledByDefault = EnumAnnotations.isEnabledByDefault(enumValue);
        this.defaultFeatureState = new FeatureState(feature, enabledByDefault);

        // lookup default activation strategy @DefaultActivationStrategy
        DefaultActivationStrategy defaultActivationStrategy = EnumAnnotations
            .getAnnotation(enumValue, DefaultActivationStrategy.class);
        if (defaultActivationStrategy != null){
            this.defaultFeatureState.setStrategyId(defaultActivationStrategy.id());

            for (ActivationParameter parameter : defaultActivationStrategy.parameters()) {
                this.defaultFeatureState.setParameter(parameter.name(), parameter.value());
            }
        }

        // process annotations on the feature
        for (Annotation annotation : EnumAnnotations.getAnnotations(enumValue)) {

            // lookup groups
            FeatureGroup group = AnnotationFeatureGroup.build(annotation.annotationType());
            if (group != null) {
                groups.add(group);
            }

            // check if this annotation is a feature attribute
            String[] attribute = FeatureAnnotations.getFeatureAttribute(annotation);
            if (attribute != null) {
                attributes.put(attribute[0], attribute[1]);
            }

        }

    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public FeatureState getDefaultFeatureState() {
        return defaultFeatureState.copy();
    }

    @Override
    public Set<FeatureGroup> getGroups() {
        return groups;
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

}
