package com.igodating.commons.utils;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@UtilityClass
public class UrlParamsUtil {

    /**
     * Convert object value to url params
     *
     * @param value object to be converter to params
     * @return url params using spring {@link MultiValueMap}
     */
    @SuppressWarnings("unchecked")
    public MultiValueMap<String, String> toUrlParams(Object value) {
        if (value instanceof MultiValueMap) {
            final LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            ((Map<Object, List<Object>>) value).forEach((x, y) -> map.put(x.toString(), y.stream().map(Object::toString).collect(Collectors.toList())));
            return map;
        }

        final LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        final List<Field> declaredFields = getAllFields(value);
        for (Field field : declaredFields) {
            ReflectionUtils.makeAccessible(field);
            final String fieldName = getFieldName(field);

            final Object fieldVal = ReflectionUtils.getField(field, value);
            mapFields(params, field, fieldName, fieldVal);
        }
        return params;
    }

    private String getFieldName(Field field) {
        final ParamName paramName = field.getAnnotation(ParamName.class);
        final String fieldName;
        if (paramName != null) {
            final String paramVal = paramName.value();
            if (StringUtils.hasLength(paramVal)) {
                fieldName = paramVal;
            } else {
                fieldName = field.getName();
            }
        } else {
            fieldName = field.getName();
        }
        return fieldName;
    }

    private List<Field> getAllFields(Object t) {
        final List<Field> fields = new ArrayList<>();
        Class<?> clazz = t.getClass();
        while (clazz.getSuperclass() != null) {
            final Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                final int modifiers = field.getModifiers();
                if (!(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers))) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @SuppressWarnings("cast")
    private void mapFields(LinkedMultiValueMap<String, String> params,
                           Field currentField,
                           String fieldName,
                           @Nullable Object fieldVal) {
        if (fieldVal != null) {
            final Class<?> fieldClass = fieldVal.getClass();
            if (BeanUtils.isSimpleValueType(fieldClass) || fieldVal instanceof Number || fieldVal instanceof UUID) {
                params.add(fieldName, fieldVal.toString());

            } else {
                if (fieldVal instanceof Map) {
                    ((Map<?, ?>) fieldVal).forEach((key, value) -> {
                        params.add(fieldName + "[" + key + "]", value.toString());
                    });
                    return;
                }
                if (fieldVal instanceof List) {
                    final Iterator<?> iterator = ((Iterable<?>) fieldVal).iterator();
                    final boolean isSimpleCollection = currentField.isAnnotationPresent(SimpleArrayParam.class);
                    BiFunction<Integer, String, String> fieldMappingFunction = isSimpleCollection ?
                            (index, name) -> name : (index, name) -> name + "[" + index + "]";

                    int i = 0;
                    while (iterator.hasNext()) {
                        final Object iterElement = iterator.next();
                        mapFields(params, currentField, fieldMappingFunction.apply(i, fieldName), iterElement);
                        i++;
                    }
                } else {
                    if (fieldVal instanceof Set) {
                        for (Object iterElement : ((Iterable<?>) fieldVal)) {
                            mapFields(params, currentField, fieldName, iterElement);
                        }
                    } else {
                        if (fieldVal instanceof Collection) {
                            throw new IllegalArgumentException("Unknown collection, expected List or Set, but was " + fieldVal.getClass());
                        }
                        if (fieldVal.getClass().isArray()) {
                            final int length = Array.getLength(fieldVal);
                            for (int i = 0; i < length; i++) {
                                Object arrayElement = Array.get(fieldVal, i);
                                mapFields(params, currentField, fieldName + "[" + i + "]", arrayElement);
                            }
                        } else {
                            final List<Field> declaredFields = getAllFields(fieldVal);

                            for (Field field : declaredFields) {
                                ReflectionUtils.makeAccessible(field);
                                final String name = getFieldName(field);
                                final Object nestedField = ReflectionUtils.getField(field, fieldVal);
                                mapFields(params, field, fieldName + "." + name, nestedField);
                            }
                        }
                    }
                }
            }
        }
    }
}
