package com.otr.plugins.qualityGate.config.post;

import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CustomAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {

    List<Type> modes;
    DefaultListableBeanFactory beanFactory;

    @Override
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        final Object result = super.getSuggestedValue(descriptor);
        if (result != null) {
            return result;
        }

        if (descriptor.getDependencyType() != Map.class) {
            return null;
        }

        final ResolvableType dependencyGenericType = descriptor.getResolvableType().asMap();
        final ResolvableType[] typeParams = dependencyGenericType.getGenerics();

        final QualifierValue qualifierValue = typeParams[0].getRawClass().getAnnotation(QualifierValue.class);
        if (qualifierValue == null) {
            return null;
        }

        final String[] candidateBeanNames = beanFactory.getBeanNamesForType(typeParams[1]);
        final LinkedHashMap<Object, Object> injectedMap = new LinkedHashMap<>(candidateBeanNames.length);

        for (final String candidateBeanName : candidateBeanNames) {
            final Annotation annotation = beanFactory.findAnnotationOnBean(candidateBeanName, qualifierValue.value());

            if (annotation == null) {
                continue;
            }

            final Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation, false);
            final Object value = annotationAttributes.get("value");

            if (value == null || value.getClass() != typeParams[0].getRawClass()) {
                continue;
            }

            if (value instanceof Type && modes.contains(value)) {
                injectedMap.put(value, beanFactory.getBean(candidateBeanName));
            }
        }

        return injectedMap;
    }
}
