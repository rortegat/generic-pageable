package com.rortegat.genericpageable.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generic class to use the pagination functionality provided by Spring JPA without querying databases.
 *
 * @param <T> The object type.
 */
@Slf4j
public class PageUtils<T> {

    /**
     * Convert any list of objects to pages.
     *
     * @param list     The list of objects.
     * @param pageable Pageable object.
     * @return Page of objects corresponding to specified in the Pageable object.
     */
    public Page<T> listToPage(List<T> list, Pageable pageable) {
        if (list == null || list.isEmpty())
            return new PageImpl<>(Collections.emptyList(), pageable, 0);

        Class<?> clazz = list.get(0).getClass();
        Sort sort = pageable.getSort();

        List<Comparator<T>> comparators = new ArrayList<>();
        //For each order specified in the Sort attribute creates a Comparator.
        sort.stream().forEach(order -> {
            String[] properties = order.getProperty().split("\\.");
            Comparator<T> comparator = Comparator.comparing(t -> {
                List<String> attributes = new LinkedList<>(Arrays.asList(properties));
                return (Comparable) invokeNestedObject(t, clazz, attributes);
            });
            if (order.isDescending())
                comparators.add(comparator.reversed());
            else
                comparators.add(comparator);
        });

        //Reduce all comparators by chaining them and then apply them to the list.
        list = comparators.stream()
                .reduce(Comparator::thenComparing)
                .map(list.stream()::sorted)
                .orElse(list.stream())
                .collect(Collectors.toList());

        final int start = (int) pageable.getOffset();
        if (start > list.size())
            return new PageImpl<>(Collections.emptyList(), pageable, list.size());
        final int end = Math.min((start + pageable.getPageSize()), list.size());

        List<T> subList = list.subList(start, end);
        return new PageImpl<>(subList, pageable, list.size());
    }

    /**
     * Invokes the getter method of required class attribute recursively.
     *
     * @param object     Reference object whose attributes will be invoked.
     * @param clazz      Class of the Reference object.
     * @param properties List of attributes whose size corresponds to the number of levels of nested objects.
     * @return Object resulting from the invocation of the getter method of the required attribute.
     */
    private Object invokeNestedObject(Object object, Class<?> clazz, List<String> properties) {
        try {
            var propertyDescriptor = new PropertyDescriptor(properties.get(0), clazz);
            var getterMethod = propertyDescriptor.getReadMethod();
            var invokedObject = getterMethod.invoke(object);

            if (properties.size() > 1) {
                var subClazz = propertyDescriptor.getPropertyType();
                properties.remove(0);
                return invokeNestedObject(invokedObject, subClazz, properties);
            } else {
                //If the attribute is NULL determine its type in order to apply a default ASC sort order (NULL values first)
                if (invokedObject == null)
                    switch (getterMethod.getReturnType().getSimpleName()) {
                        case "String": return "";
                        case "BigDecimal": return BigDecimal.valueOf(0.0);
                        default: return 0;
                    }
                return invokedObject instanceof String ? ((String) invokedObject).toLowerCase() : invokedObject;
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Object %s does not have public access for property %s or does not exist.", clazz.getName(), properties.get(0)), e);
        }
    }

}
