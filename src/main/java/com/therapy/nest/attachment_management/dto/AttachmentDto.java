/**
 *
 */
package com.therapy.nest.attachment_management.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class AttachmentDto {
	private String uuid;
	private String name;
	private String path;
}
