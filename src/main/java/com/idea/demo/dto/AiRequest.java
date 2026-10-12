package com.idea.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiRequest {

    @NotBlank(message = "Please type your rough idea first")
    private String roughText;
}