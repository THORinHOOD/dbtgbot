package com.thorinhood.dbtg.models.filters;

import org.springframework.data.jpa.domain.Specification;

public interface OperationEmbedded<T> {
    Specification<T> process(String wrapper, String field, Object value);
}
