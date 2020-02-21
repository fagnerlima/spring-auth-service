package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

public enum Operation {

    EQUAL, EQUAL_IGNORE_CASE,
    LIKE, LIKE_IGNORE_CASE,
    GREATER_THAN, GREATER_THAN_OR_EQUAL,
    LESS_THAN, LESS_THAN_OR_EQUAL,

    /** Suporte para PostgreSQL */
    EQUALS_IGNORE_CASE_UNACCENT,

    /** Suporte para PostgreSQL */
    LIKE_IGNORE_CASE_UNACCENT,

}
