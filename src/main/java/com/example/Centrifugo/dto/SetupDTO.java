package com.example.Centrifugo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetupDTO {

    private UUID id;

    private String name;

    private UUID categoryId;

    private boolean isEnabled;

    private UUID createdBy;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID updatedBy;

    private ZonedDateTime updatedAt = ZonedDateTime.now();


}
