package com.rortegat.genericpageable.object;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class NestedNestedObject {
    private BigDecimal id;
}
