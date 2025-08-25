package com.therapy.nest.uaa.entity;

import com.therapy.nest.uaa.enums.AuthenticationMethod;
import com.therapy.nest.shared.utils.CustomGeneratedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
public class UserAccount implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid = CustomGeneratedData.GenerateUniqueID();

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    @GraphQLIgnore
    private String password;

    @Column(unique = true, name = "email", nullable = false)
    private String email;

    @Pattern(regexp = "(^(([2]{1}[5]{2})|([0]{1}))[1-9]{2}[0-9]{7}$)", message = "Please enter valid phone number eg. 255756789865")
    @Column(name = "phone")
    private String phone;

    @Column(name = "picture")
    private String picture;

    @Column(name = "authentication_mode")
    @Enumerated(EnumType.STRING)
    private AuthenticationMethod authenticationMethod = AuthenticationMethod.BOTH;

    @Column(name = "first_login", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean firstLogin = Boolean.TRUE;

    @JsonIgnore
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @JsonIgnore
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role", referencedColumnName = "id")
    private Role role;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean accountNonExpired = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean accountNonLocked = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean credentialsNonExpired = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean enabled = true;

    @Column(name = "is_super_admin", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSuperAdmin = false;

    @Transient
    private String fullName;

    @Transient
    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @JsonIgnore
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @Basic(optional = false)
    @Column(name = "deleted")
    private Boolean deleted = false;

    @Basic(optional = false)
    @Column(name = "active")
    private Boolean active = true;


    public String getFullName() {
        return this.firstName + " " + (this.middleName != null ? this.middleName : "") + " " + this.lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UserAccount(boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }
}
