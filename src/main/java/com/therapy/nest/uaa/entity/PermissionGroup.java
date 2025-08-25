package com.therapy.nest.uaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.therapy.nest.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "permission_groups")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE permission_groups SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class PermissionGroup extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "permissionGroup", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Permission> permissions;

    public PermissionGroup(String permissionGroup) {
        this.name = permissionGroup;
    }
}