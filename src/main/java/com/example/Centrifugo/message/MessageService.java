package com.example.Centrifugo.message;

import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.MessageDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageService {

    ResponseEntity<ResponseDTO> findAllMessages(Map<String, String> params);

    ResponseEntity<ResponseDTO> findById(UUID id);

    ResponseEntity<ResponseDTO> createMessage(MessageDto studentDto);

    ResponseEntity<ResponseDTO> updateMessage(UUID id, MessageDto studentDto);

    ResponseEntity<ResponseDTO> deleteMessage(UUID id);

    List<MessageTable> getMessage(UUID receiverId);
}
