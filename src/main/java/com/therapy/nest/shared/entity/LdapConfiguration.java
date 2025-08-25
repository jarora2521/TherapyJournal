package com.therapy.nest.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.leangen.graphql.annotations.GraphQLIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "ldap_configurations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE ldap_configurations SET deleted = true WHERE id = ?")
 @SQLRestriction("deleted = false")
public class LdapConfiguration extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -2722013070168253115L;

    @Column(nullable = false, name = "url", unique = true)
    private String ldapUrl;

    @Column(nullable = false, name = "user_dn")
    private String ldapUserDn;

    @Column(nullable = false, name = "base_dn")
    private String ldapBaseDn;

    @JsonIgnore
    @GraphQLIgnore
    @Column(nullable = false, name = "password")
    private String ldapPassword;

    @Column(name = "anonymous_read_only")
    private Boolean ldapAnonymousReadOnly = Boolean.FALSE;

    @Column(name = "retry_time")
    private Long ldapAuthTryTime = 5000L;
}