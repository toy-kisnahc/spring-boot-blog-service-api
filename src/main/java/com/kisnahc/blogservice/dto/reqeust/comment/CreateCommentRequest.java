package com.kisnahc.blogservice.dto.reqeust.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateCommentRequest {

    @Size(min = 3, max = 50, message = "Please enter no more than 3 and no more than 50 characters.")
    @NotBlank(message = "Please enter comment")
    private String content;
    private Long parentId;
}
