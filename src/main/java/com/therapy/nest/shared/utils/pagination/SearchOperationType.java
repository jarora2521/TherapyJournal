package com.therapy.nest.shared.utils.pagination;

public enum SearchOperationType {
    Equals,
    NotEquals,
    Like,
    LessThan,
    GreaterThan,
    In,
    Between,
    Empty,
    Null,
    NotNull,
    NotEmpty;

    SearchOperationType() {
    }
}