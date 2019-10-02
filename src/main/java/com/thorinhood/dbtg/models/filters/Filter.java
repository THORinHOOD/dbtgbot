package com.thorinhood.dbtg.models.filters;

import org.springframework.data.jpa.domain.Specification;

public class Filter<T> {

    private Specification<T> filter;

    public Filter<T> andIfNotNull(String field, Object value) {
        return value != null ? and(field, value) : this;
    }

    public Filter<T> and(String field, Object value) {
        filter = filter == null ? eq(field, value) : filter.and(eq(field, value));
        return this;
    }

    public static <T> Specification<T> eq(String field, Object value) {
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    public Specification<T> getFilter() {
        return filter;
    }
}
