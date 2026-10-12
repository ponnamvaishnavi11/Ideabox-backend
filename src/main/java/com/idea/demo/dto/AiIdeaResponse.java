package com.idea.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiIdeaResponse {
    private String title;
    private String description;
    private String tags;
    private String skillsNeeded;
}