package com.therapy.nest.uaa.dto;

import com.therapy.nest.uaa.enums.AuthenticationMethod;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;

@Data
public class UserDto {
    private String uuid;

    @GraphQLNonNull
    private String firstName;

    private String middleName;

    @GraphQLNonNull
    private String lastName;

    private String password;

    @GraphQLNonNull
    private String email;

    private String phone;
    private String picture;

    @GraphQLNonNull
    private AuthenticationMethod authenticationMethod;

    private String roleUuid;
}
