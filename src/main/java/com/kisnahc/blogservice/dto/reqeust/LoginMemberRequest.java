package com.kisnahc.blogservice.dto.reqeust;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginMemberRequest {

    @NotBlank(message = "Please enter your email.")
    @Email(message = "Please enter it in the format of your email.")
    private String email;

    @NotBlank(message = "Please enter your password.")
    private String password;
}
