package com.example.Centrifugo.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LOVCategoryDTO {

    private UUID id;

    private String name;

    private boolean isEnabled;

    private UUID createdBy;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID updatedBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();
}
