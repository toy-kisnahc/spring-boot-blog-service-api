package com.kisnahc.blogservice.dto.reqeust.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateMemberRequest {

    @NotBlank(message = "Please enter your email.")
    @Email(message = "Please enter it in the format of your email.")
    private String email;

    @Size(min = 6, max = 20, message = "Please enter no more than 6 and no more than 20 characters.")
    @NotBlank(message = "Please enter your nickname.")
    private String nickname;

    @NotBlank(message = "Please enter your password.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$", message = "must contain at least 8 to 20 characters, numbers, and special characters.")
    private String password;
}
