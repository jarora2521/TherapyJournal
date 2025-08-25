package com.therapy.nest.shared.entity;

import com.therapy.nest.shared.utils.CustomGeneratedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
 @SQLRestriction("deleted = false")
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotAudited
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid = CustomGeneratedData.GenerateUniqueID();

    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonIgnore
    @CreatedBy
    private String createdBy;

    @JsonIgnore
    @LastModifiedBy
    private String updatedBy;

    @Basic(optional = false)
    @Column(name = "deleted")
    private Boolean deleted = false;

    @Basic(optional = false)
    @Column(name = "active")
    private Boolean active = true;

}
