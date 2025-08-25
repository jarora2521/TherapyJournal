package com.therapy.nest.shared.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LdapConfigDto {
	private String uuid;
	private String ldapUrl;
	private String ldapUserDn;
	private String ldapBaseDn;
	private String ldapPassword;
	private Boolean ldapAnonymousReadOnly;
}
