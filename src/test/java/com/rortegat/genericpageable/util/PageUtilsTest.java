package com.rortegat.genericpageable.util;

import com.google.gson.Gson;
import com.rortegat.genericpageable.object.NestedNestedObject;
import com.rortegat.genericpageable.object.NestedObject;
import com.rortegat.genericpageable.object.NoGettersObject;
import com.rortegat.genericpageable.object.WrapperObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PageUtilsTest {

    private final Gson gson = new Gson();

    @Test
    void returnsSortedPage_whenSortListOfObjectsByNestedObjectProperties() {
        //given
        var list = List.of(
                WrapperObject.builder().guid("B").nestedObject(NestedObject.builder().name("X").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(1.0)).build()).build()).build(),
                WrapperObject.builder().guid("A").nestedObject(NestedObject.builder().name("Y").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(1.0)).build()).build()).build(),
                WrapperObject.builder().guid("B").nestedObject(NestedObject.builder().name("Y").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(2.0)).build()).build()).build(),
                WrapperObject.builder().guid("B").nestedObject(NestedObject.builder().name("Y").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(1.0)).build()).build()).build(),
                WrapperObject.builder().guid("A").nestedObject(NestedObject.builder().name("X").nestedNestedObject(NestedNestedObject.builder().id(null).build()).build()).build(),
                WrapperObject.builder().guid("B").nestedObject(NestedObject.builder().name("X").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(2.0)).build()).build()).build(),
                WrapperObject.builder().guid("A").nestedObject(NestedObject.builder().name("Y").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(2.0)).build()).build()).build(),
                WrapperObject.builder().guid("A").nestedObject(NestedObject.builder().name("X").nestedNestedObject(NestedNestedObject.builder().id(BigDecimal.valueOf(1.0)).build()).build()).build()
        );
        log.info("List of object with other objects as attributes, e.g. {}", list.get(0));
        log.info("Sorting by guid (ASC), nestedObject.name (ASC) and nestedObject.nestedNestedObject.id (DESC)");
        var sort = Sort.by("guid").and(Sort.by("nestedObject.name").and(Sort.by("nestedObject.nestedNestedObject.id")));
        log.info("Creating a Pageable object requesting for the first page with the first 10 elements applying the defined sort order");
        var pageable = PageRequest.of(0, 10, sort);

        //when
        var orderedPage = PageUtils.listToPage(list, pageable);
        log.info("Page retrieved: {}", gson.toJson(orderedPage));
        //then
        assertNull(orderedPage.getContent().get(0).getNestedObject().getNestedNestedObject().getId());
        assertEquals(BigDecimal.valueOf(2.0), orderedPage.getContent().get(orderedPage.getContent().size() - 1).getNestedObject().getNestedNestedObject().getId());
    }

    @Test
    void throwsInvocationTargetException_whenGetObjectPropertyThrowsInvocationTargetException() {
        //given
        var sort = Sort.by("firstName");
        var pageable = PageRequest.of(0, 5, sort);

        var objectsList = List.of(new NoGettersObject("A", "X"), new NoGettersObject("A", "Y"));

        //then
        var exception = assertThrows(RuntimeException.class, () ->
                //when
                PageUtils.listToPage(objectsList, pageable));

        log.error("Thrown exception due properties accessibility: {}", exception.getMessage());
        assertEquals("Object com.rortegat.genericpageable.object.NoGettersObject does not have public access for property firstName or does not exist.", exception.getMessage());
    }

}