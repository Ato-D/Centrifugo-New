package com.example.Centrifugo.message;

import com.example.Centrifugo.CentrifugoPublisher;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.MessageDto;
import com.example.Centrifugo.utility.ObjectNotValidException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.Centrifugo.utility.AppUtils.*;

@Service
@AllArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final CentrifugoPublisher centrifugoPublisher;



    /**
     * This method is use to find all the messages saved in the db
     * @param params the query parameters we are passing
     * @return the respose onbject and the status code
     */
    @Override
    public ResponseEntity<ResponseDTO> findAllMessages(Map<String, String> params) {
        log.info("Inside find All Messages :::: Trying to fetch messages per given pagination params");

        ResponseDTO response = new ResponseDTO();
        try {

            if (params == null || params.getOrDefault("paginate", "false").equalsIgnoreCase("false")) {
                List<MessageTable> messages;
                messages = messageRepository.findAll();
                if (!messages.isEmpty()) {
                    log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, messages);
                    List<MessageDto> messageDtosDtos = messages.stream()
                            .map(this::mapToMessageDTO)
                            .collect(Collectors.toList());
                    response = getResponseDTO("Successfully retrieved all messages", HttpStatus.OK, messageDtosDtos);
                    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
                } else {
                    response = getResponseDTO("No record found", HttpStatus.NOT_FOUND);
                    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
                }
            }

        } catch (ResponseStatusException e) {
            log.error("Exception Occured! and Message -> {} and Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! StatusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

    }


    /**
     * This method finds the message by his or her id
     * @param id represents the ID of the message we are finding
     * @return returns the response and the status code
     */

    @Override
    public ResponseEntity<ResponseDTO> findById(UUID id) {
        log.info("Inside find Find Messages by Id ::: Trying to find message type id -> {}", id);
        ResponseDTO response;
        try {
            var res = messageRepository.findById(id);
            if (res.isPresent()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, res);
                response = getResponseDTO("Successfully retreived the message with id " + id, HttpStatus.OK, res);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
            log.info("No record found! statusCode -> {} and Message -> {}", HttpStatus.NOT_FOUND, res);
            response = (getResponseDTO("Not Found!", HttpStatus.NOT_FOUND));
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
        }
        catch (ResponseStatusException e) {
            log.error("Exception Occured! Reason -> {} and Message -> {}", e.getCause(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }


    /**
     * This method saves the message in the database
     * @param messageDto represents the object to be saved
     * @return returns the response and the status code
     */
    @Override
    public ResponseEntity<ResponseDTO> createMessage(MessageDto messageDto) {
        log.info("Inside the Save message method ::: Trying to save a message");
        ResponseDTO respose;
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            log.info("MessageDto {}",messageDto);
            var message = MessageTable.builder()
                    .senderId(getAuthenticatedUserId())
                    .receiverId(messageDto.getReceiverId())
                    .message(messageDto.getMessage())
                    .sender(messageDto.getSender())
                    .receiver(messageDto.getReceiver())
                    .createdAt(ZonedDateTime.now())
                    .build();
            var record = messageRepository.save(message);
            log.info("Saved record -> {}",record);
            var centrifge = centrifugoPublisher.sendImmobilizationToCentrifugo(record, "save");
            log.info("Message published successfully! statusCode -> {} and Message -> {}", HttpStatus.ACCEPTED, centrifge);

            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.CREATED, record);
            respose = getResponseDTO("Record Saved Successfully", HttpStatus.OK, record);

        } catch (ResponseStatusException e) {
            log.error("Error Occured! statusCode -> {}, Message -> {}, Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            respose = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occured! Reason -> {}", message);
            respose = getResponseDTO(message, HttpStatus.BAD_REQUEST);

        } catch (DataIntegrityViolationException e) {
            log.error("Exception Occured! Message -> {} and Cause -> {}", e.getMostSpecificCause(), e.getMessage());
            respose = getResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            respose = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(respose, HttpStatus.valueOf(respose.getStatusCode()));
    }


    /**
     * The method performs the update of message
     * @param id the id of the message to be updated
     * @param messageDto the object we are updating
     * @return returns the response and the status code of the response
     */
    @Override
    public ResponseEntity<ResponseDTO> updateMessage(UUID id, MessageDto messageDto) {
        log.info("Inside the update message method ::: Trying to update a message");
        ResponseDTO response;

        try {
            MessageTable existingMessage = messageRepository.findById(id)
                        .orElseThrow(()
                                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "message with Id " + id + "Does Not Exist"));
                existingMessage.setMessage(messageDto.getMessage());
//                existingMessage.setSenderId(messageDto.getSenderId());
                existingMessage.setReceiverId(messageDto.getReceiverId());
                existingMessage.setSender(messageDto.getSender());
                existingMessage.setReceiver(messageDto.getReceiver());
                existingMessage.setCreatedAt(ZonedDateTime.now());



                var record = messageRepository.save(existingMessage);
                var centrifge = centrifugoPublisher.sendImmobilizationToCentrifugo(record, "save");
                log.info("Message published successfully! statusCode -> {} and Message -> {}", HttpStatus.ACCEPTED, centrifge);
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.ACCEPTED, record);
                response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, record);
        } catch (ResponseStatusException e) {
            log.error("Exception Occured! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occured! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    /**
     * This method is used for deleting the message
     * @param id represents the id of the message
     * @return returns the respose and the http status code of the response
     */

    public ResponseEntity<ResponseDTO> deleteMessage(UUID id) {
        log.info("Inside Delete message Method ::: Trying To Delete message Per Given Params");
        ResponseDTO response;

        try {
//            boolean isAdmin = hasAdminRole(getUserRoles());
//            if (isAdmin) {
            var existingMessage = messageRepository.findById(id);
            if (existingMessage.isPresent()) {
                messageRepository.deleteById(id);
            }
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, existingMessage);
            response = getResponseDTO("Messages deleted successfully", HttpStatus.OK);
//            }

//        else {
//                log.info("Not Authorized to Delete Message", HttpStatus.FORBIDDEN);
//                response = getResponseDTO("Not Authorized to Delete Message", HttpStatus.FORBIDDEN);
//            }
        }
        catch (ResponseStatusException e) {
            log.error("Exception Occured! Reason -> {} and Message -> {}", e.getCause(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @Override
    public List<MessageTable> getMessage(UUID receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }

    /**
     * This method maps the Message entity to the message dto
     * @param messages represents the instance of the Message entity
     * @return returns the message dto
     */

    private MessageDto mapToMessageDTO(MessageTable messages) {

        MessageDto messageDto = MessageDto.builder()
                .id(messages.getId())
                .message(messages.getMessage())
                .senderId(messages.getSenderId())
                .receiverId(messages.getReceiverId())
                .build();

        return messageDto;
    }


    //    public ResponseEntity<ResponseDTO> createMessage(MessageDto messageDto) {
//        log.info("Inside the Save message method ::: Trying to save a message");
//        ResponseDTO response;
//
//        try {
//            log.info("MessageDto {}", messageDto);
//            UUID senderId = getAuthenticatedUserId();
//            var message = MessageTable.builder()
//                    .senderId(senderId)
//                    .receiverId(messageDto.getReceiverId())
//                    .message(messageDto.getMessage())
//                    .sender(messageDto.getSender())
//                    .receiver(messageDto.getReceiver())
//                    .createdAt(ZonedDateTime.now())
//                    .build();
//            var record = messageRepository.save(message);
//            log.info("Saved record -> {}", record);
//
//            String channel = createChannelName(senderId, messageDto.getReceiverId());
//            centrifugoPublisher.sendToChannel(channel, record);
//
//            response = new ResponseDTO("Message saved and sent to channel: " + channel, 200);
//        } catch (Exception e) {
//            log.error("Error saving message", e);
//            response = new ResponseDTO("Error saving message", 500);
//        }
//
//        return ResponseEntity.ok(response);
//    }
}

