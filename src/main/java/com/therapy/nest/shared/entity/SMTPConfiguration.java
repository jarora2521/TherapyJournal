package com.therapy.nest.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.leangen.graphql.annotations.GraphQLIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Setter
@Getter
@Entity
@Audited
@Table(name = "smtp_configurations")
@SQLDelete(sql = "UPDATE smtp_configurations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class SMTPConfiguration extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4161378732854384517L;

    @Column(name = "smtp_host", nullable = false)
    private String host;

    @Column(name = "port", nullable = false)
    private String port;

    @Column(name = "protocol", nullable = false)
    private String mailProtocol = "smtp";

    @Column(name = "username")
    private String username;

    @GraphQLIgnore
    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "default_config")
    private Boolean defaultConfig = false;

    @Column(name = "auth_required")
    private Boolean authRequired = true;

    @Column(name = "starttls_enabled")
    private Boolean starttlsEnabled = false;

    @Column(name = "starttls_required")
    private Boolean starttlsRequired = false;

    @Column(name = "ssl_enabled")
    private Boolean sslEnabled = false;

    @Column(name = "ssl_check_server_identity")
    private Boolean sslCheckServerIdentity = false;

    @Column(name = "ssl_trust")
    private String sslTrust = "*";

    private Boolean allowDebug = false;
}

