package com.idea.demo.dto;

import com.idea.demo.entity.IdeaStatus;
import com.idea.demo.entity.IdeaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdeaRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String tags;

    private String skillsNeeded;

    @NotNull(message = "Type is required")
    private IdeaType type;

    private IdeaStatus status;
}