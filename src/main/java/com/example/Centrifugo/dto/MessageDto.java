package com.example.Centrifugo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {

    private UUID id;

    private String message;

    private UUID senderId;

    private UUID receiverId;

    private String receiver;

    private String sender;

    private String channel;
}
