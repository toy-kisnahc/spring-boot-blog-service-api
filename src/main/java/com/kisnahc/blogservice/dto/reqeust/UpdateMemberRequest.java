package com.kisnahc.blogservice.dto.reqeust;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateMemberRequest {
    @Size(min = 6, max = 20, message = "Please enter no more than 6 and no more than 20 characters.")
    @NotBlank(message = "Please enter your nickname.")
    private String nickname;
}
