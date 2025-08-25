package com.therapy.nest.shared.utils.pagination;


import java.util.ArrayList;
import java.util.List;


public class PageableParam {
    private String sortBy;
    private String sortDirection;
    private Integer size;
    private Integer first;
    private List<SearchFieldsDto> searchFields = new ArrayList();

    public PageableParam(String sortBy, String sortDirection, Integer size, Integer first) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.size = size;
        this.first = first;
    }

    public PageableParam(Integer size, Integer first) {
        this.size = size;
        this.first = first;
    }

    public PageableParam(String sortBy, String sortDirection, Integer size, Integer first, List<SearchFieldsDto> searchFields) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.size = size;
        this.first = first;
        this.searchFields = searchFields;
    }

    public PageableParam() {
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return this.sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFirst() {
        return this.first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public List<SearchFieldsDto> getSearchFields() {
        return this.searchFields;
    }

    public void setSearchFields(List<SearchFieldsDto> searchFields) {
        this.searchFields = searchFields;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageableParam other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71:
                {
                    Object this$size = this.getSize();
                    Object other$size = other.getSize();
                    if (this$size == null) {
                        if (other$size == null) {
                            break label71;
                        }
                    } else if (this$size.equals(other$size)) {
                        break label71;
                    }

                    return false;
                }

                Object this$first = this.getFirst();
                Object other$first = other.getFirst();
                if (this$first == null) {
                    if (other$first != null) {
                        return false;
                    }
                } else if (!this$first.equals(other$first)) {
                    return false;
                }

                label57:
                {
                    Object this$sortBy = this.getSortBy();
                    Object other$sortBy = other.getSortBy();
                    if (this$sortBy == null) {
                        if (other$sortBy == null) {
                            break label57;
                        }
                    } else if (this$sortBy.equals(other$sortBy)) {
                        break label57;
                    }

                    return false;
                }

                Object this$sortDirection = this.getSortDirection();
                Object other$sortDirection = other.getSortDirection();
                if (this$sortDirection == null) {
                    if (other$sortDirection != null) {
                        return false;
                    }
                } else if (!this$sortDirection.equals(other$sortDirection)) {
                    return false;
                }

                Object this$searchFields = this.getSearchFields();
                Object other$searchFields = other.getSearchFields();
                if (this$searchFields == null) {
                    return other$searchFields == null;
                } else return this$searchFields.equals(other$searchFields);
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PageableParam;
    }

    public int hashCode() {
        int result = 1;
        Object $size = this.getSize();
        result = result * 59 + ($size == null ? 43 : $size.hashCode());
        Object $first = this.getFirst();
        result = result * 59 + ($first == null ? 43 : $first.hashCode());
        Object $sortBy = this.getSortBy();
        result = result * 59 + ($sortBy == null ? 43 : $sortBy.hashCode());
        Object $sortDirection = this.getSortDirection();
        result = result * 59 + ($sortDirection == null ? 43 : $sortDirection.hashCode());
        Object $searchFields = this.getSearchFields();
        result = result * 59 + ($searchFields == null ? 43 : $searchFields.hashCode());
        return result;
    }

    public String toString() {
        return "PageableParam(sortBy=" + this.getSortBy() + ", sortDirection=" + this.getSortDirection() + ", size=" + this.getSize() + ", first=" + this.getFirst() + ", searchFields=" + this.getSearchFields() + ")";
    }
}