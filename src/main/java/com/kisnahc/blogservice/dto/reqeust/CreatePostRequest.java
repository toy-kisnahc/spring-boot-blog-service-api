package com.kisnahc.blogservice.dto.reqeust;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreatePostRequest {

    @NotBlank(message = "Please enter title.")
    private String title;

    @NotBlank(message = "Please enter content.")
    private String content;
}
