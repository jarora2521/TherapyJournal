package com.therapy.nest.attachment_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.therapy.nest.shared.entity.BaseEntity;
import com.therapy.nest.uaa.entity.UserAccount;
import io.leangen.graphql.annotations.GraphQLIgnore;
import jakarta.persistence.*;
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
@Table(name = "attachments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE attachments SET deleted = true WHERE id = ?")
public class Attachment extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "path", columnDefinition = "TEXT", nullable = false)
	private String path;

	@JsonIgnore
	@GraphQLIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private UserAccount user;

	@Transient
	private String base64String;
}
