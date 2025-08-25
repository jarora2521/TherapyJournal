package com.therapy.nest.shared.utils.pagination;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenericSpecificationSearch<T> {

    public Specification<T> getSearchSpec(List<SearchFieldsDto> searchFieldsDtos) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (SearchFieldsDto searchFieldsDto : searchFieldsDtos) {

                switch (searchFieldsDto.getSearchType()) {
                    case In:
                        predicates.add(root.get(searchFieldsDto.getFieldName()).in(searchFieldsDto.getFieldValues()));
                        break;
                    case Equals:
                        predicates
                                .add(criteriaBuilder.equal(criteriaBuilder.lower(root.get(searchFieldsDto.getFieldName())),
                                        searchFieldsDto.getFieldValue().toString().toLowerCase()));
                        break;
                    case GreaterThan:
                        predicates.add(criteriaBuilder.greaterThan(root.get(searchFieldsDto.getFieldName()),
                                searchFieldsDto.getFieldValue().toString().toLowerCase()));
                        break;
                    case LessThan:
                        predicates.add(criteriaBuilder.lessThan(root.get(searchFieldsDto.getFieldName()),
                                searchFieldsDto.getFieldValue().toString().toLowerCase()));
                        break;
                    case Like:
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(searchFieldsDto.getFieldName())),
                                "%" + searchFieldsDto.getFieldValue().toString().toLowerCase() + "%"));
                        break;
                    case NotEquals:
                        predicates.add(
                                criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(searchFieldsDto.getFieldName())),
                                        searchFieldsDto.getFieldValue().toString().toLowerCase()));
                        break;
                }
            }
            if (searchFieldsDtos.size() > 0 && searchFieldsDtos.get(0).getQueryOperator() == SearchFieldsDto.QueryOperator.AND) {
                return searchFieldsDtos.size() > 0 ? criteriaBuilder.and(predicates.toArray(new Predicate[0])) : null;
            } else {
                return searchFieldsDtos.size() > 0 ? criteriaBuilder.or(predicates.toArray(new Predicate[0])) : null;
            }
        };
    }

    public Specification<T> createSpecification(SearchFieldsDto input) {
        switch (input.getSearchType()) {

            case Equals:
                return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(input.getFieldName()),
                        input.getFieldValue());

            case NotEquals:
                return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(input.getFieldName()),
                        input.getFieldValue());

            case GreaterThan:
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(input.getFieldName()),
                        input.getFieldValue().toString());

            case LessThan:
                return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(input.getFieldName()),
                        input.getFieldValue().toString());

            case Like:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(input.getFieldName()),
                        "%" + input.getFieldValue() + "%");

            case In:
                return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(input.getFieldName()))
                        .value(input.getFieldValues());

            case Empty:
                return (root, query, criteriaBuilder) -> criteriaBuilder.isEmpty(root.get(input.getFieldName()));

            case Null:
                return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(input.getFieldName()));

            case NotNull:
                return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(input.getFieldName()));

            case NotEmpty:
                return (root, query, criteriaBuilder) -> criteriaBuilder.isNotEmpty(root.get(input.getFieldName()));

            default:
                throw new RuntimeException("Operation not supported yet");
        }
    }

    public Specification<T> getEqualSpec(String fieldName, Object fieldValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(fieldName), fieldValue);
    }

    public Specification<T> getNotEqualSpec(String fieldName, Object fieldValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get(fieldName), fieldValue);
    }

    public Specification<T> getGreaterThanOrEqualSpec(String fieldName, LocalDate fieldValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), fieldValue);
    }

    public Specification<T> getLessThanOrEqualSpec(String fieldName, LocalDate fieldValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), fieldValue);
    }

    public Specification<T> getIsNullSpec(String fieldName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(fieldName));
    }

    public Specification<T> getIsNotNullSpec(String fieldName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get(fieldName));
    }

}
