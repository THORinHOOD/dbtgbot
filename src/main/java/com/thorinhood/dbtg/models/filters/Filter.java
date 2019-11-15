package com.thorinhood.dbtg.models.filters;

import org.springframework.data.jpa.domain.Specification;

public class Filter<T> {

    private Specification<T> filter;

    public Filter() {
    }

    public Filter(Filter<T> filter) {
        this.filter = filter.getFilter();
    }

    public Filter<T> andIfNotNull(String field, Object value, Operation<T> operation) {
        return value != null ? and(field, value, operation) : this;
    }

    public Filter<T> orIfNotNull(String field, Object value, Operation<T> operation) {
        return value != null ? or(field, value, operation) : this;
    }

    public Filter<T> and(String field, Object value, Operation<T> operation) {
        filter = filter == null ? operation.process(field, value) : filter.and(operation.process(field, value));
        return this;
    }

    public Filter<T> or(String field, Object value, Operation<T> operation) {
        filter = filter == null ? operation.process(field, value) : filter.or(operation.process(field, value));
        return this;
    }

    public static <T> Specification<T> contains(String field, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("contains works only for strings!");
        }
        return (root, query, cb) -> cb.like(root.get(field), "%" + value + "%");
    }

    public static <T> Specification<T> eq(String field, Object value) {
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    public Specification<T> getFilter() {
        return filter;
    }

}
