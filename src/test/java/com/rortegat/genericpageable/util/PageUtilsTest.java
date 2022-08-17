package com.rortegat.genericpageable.util;

import com.rortegat.genericpageable.object.NestedNestedObject;
import com.rortegat.genericpageable.object.NestedObject;
import com.rortegat.genericpageable.object.NoGettersObject;
import com.rortegat.genericpageable.object.WrapperObject;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageUtilsTest {

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

        var sort = Sort.by("guid").and(Sort.by("nestedObject.name").and(Sort.by("nestedObject.nestedNestedObject.id")));
        var pageable = PageRequest.of(0, 10, sort);
        var pageUtils = new PageUtils<WrapperObject>();
        //when
        var orderedPage = pageUtils.listToPage(list, pageable);
        //then
        assertNull(orderedPage.getContent().get(0).getNestedObject().getNestedNestedObject().getId());
        assertEquals(BigDecimal.valueOf(2.0), orderedPage.getContent().get(orderedPage.getContent().size() - 1).getNestedObject().getNestedNestedObject().getId());
    }

    @Test
    void throwsInvocationTargetException_whenGetPropertyDoesNotExistsInObject() {
        //given
        var sort = Sort.by("firstName");
        var pageable = PageRequest.of(0, 5, sort);
        var pageUtil = new PageUtils<NoGettersObject>();

        var objectsList = List.of(new NoGettersObject("A", "X"), new NoGettersObject("A", "Y"));

        //then
        var exception = assertThrows(RuntimeException.class, () ->
                //when
                pageUtil.listToPage(objectsList, pageable));

        assertEquals("Object com.rortegat.genericpageable.object.NoGettersObject does not have public access for property firstName or does not exist.", exception.getMessage());
    }

}