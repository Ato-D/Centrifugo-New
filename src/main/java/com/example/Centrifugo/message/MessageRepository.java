package com.example.Centrifugo.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageTable, UUID> {


    @Query("SELECT m FROM MessageTable m WHERE m.receiverId = :receiverId")
    List<MessageTable> findByReceiverId(@Param("receiverId") UUID receiverId);
}

//    List<Message> findByReceiverAndReceiverType(UUID receiverId);

