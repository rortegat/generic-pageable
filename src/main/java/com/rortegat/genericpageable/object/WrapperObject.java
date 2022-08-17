package com.rortegat.genericpageable.object;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WrapperObject {
    private String guid;
    private NestedObject nestedObject;
}
