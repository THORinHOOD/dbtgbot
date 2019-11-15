package com.thorinhood.dbtg.models.filters;

import org.springframework.data.jpa.domain.Specification;

public interface Operation<T> {

    Specification<T> process(String field, Object arg);

}
