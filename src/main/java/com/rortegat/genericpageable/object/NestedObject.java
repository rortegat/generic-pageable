package com.rortegat.genericpageable.object;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NestedObject {
    private String name;
    private NestedNestedObject nestedNestedObject;
}
