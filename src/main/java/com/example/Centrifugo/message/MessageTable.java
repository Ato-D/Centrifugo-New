package com.example.Centrifugo.message;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "table_message", schema = "centrifugo")
@Entity
public class MessageTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String message;

    private UUID senderId;

    private UUID receiverId;

    private String receiver;

    private String sender;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

}
