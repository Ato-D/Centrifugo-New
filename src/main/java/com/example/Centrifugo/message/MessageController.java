package com.example.Centrifugo.message;


import com.example.Centrifugo.CentrifugoJwt;
import com.example.Centrifugo.config.CentrifugoConfiguration;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.Centrifugo.config.SecurityConfig.CONTEXT_PATH;


@CrossOrigin
@RestController
@RequestMapping(CONTEXT_PATH)
@AllArgsConstructor
@Slf4j
public class MessageController {


    private final MessageService messageService;

    private final CentrifugoJwt centrifugoJwt;
    private final CentrifugoConfiguration centrifugo;



    @GetMapping("/findAll")
    public ResponseEntity<ResponseDTO> findAll(@RequestParam Map<String, String> params) {
        return messageService.findAllMessages(params);
    }

    @GetMapping("/{id}/messsages")
    public ResponseEntity<ResponseDTO> findById(@PathVariable(name = "id") UUID id) {
        var res = messageService.findById(id);
        return res;
    }


    /**
     *  the MessageController class handles incoming requests to send and retrieve messages.
     *  The sendMessage method takes a Message object as a request body, and passes it to the MessageService to handle the business logic of sending the message.
     *  The getMessages method takes a recipientId as a path variable, and passes it to the MessageService to retrieve the messages for that recipient.
     * @param messageDto the object to be sent
     * @return
     */


    @PostMapping("/create-message")
    public ResponseEntity<ResponseDTO> sendMessage(@RequestBody MessageDto messageDto) {
        return messageService.createMessage(messageDto);
    }

    @GetMapping("{recipentId}")
    public List<MessageTable> getMessages(@PathVariable UUID recipentId) {
        return messageService.getMessage(recipentId);
    }



    @PutMapping("update-student/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable(name = "id") UUID id,
                                              @RequestBody MessageDto messageDto) {
        messageDto.setId(id);
        return messageService.updateMessage(id, messageDto);
    }

    @DeleteMapping("/delete-student/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable(name = "id") UUID id) {
        return messageService.deleteMessage(id);
    }

//        @GetMapping("/credentials")
//        public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal) throws Exception {
//            final String CENTRIFUGO_WEBSOCKET = centrifugo.getWebsocket();
//            final String CENTRIFUGO_ACCESS_TOKEN = centrifugoJwt.createJWT(user);
//            final String DEVICE_SUMMARY_CHANNEL = centrifugo.getActivitySummary();
//
////            var username = principal.getName();
//            var userId = user.getIdToken().getClaims().get("sub");
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("title", "Dashboard");
////            response.put("username", username);
//            response.put("userId", userId);
//            response.put("token", CENTRIFUGO_ACCESS_TOKEN);
//            response.put("centrifugoWebSocket", CENTRIFUGO_WEBSOCKET);
//            response.put("device_summary", DEVICE_SUMMARY_CHANNEL);
//
//            return ResponseEntity.ok(response);
//        }


    @GetMapping("/credentials")
    public ResponseEntity<Map<String, Object>> getDashboard(Principal principal) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       authentication.getPrincipal();

//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String user = principal.getName();
//        final String CENTRIFUGO_WEBSOCKET = centrifugo.getWebsocket();
        final String CENTRIFUGO_ACCESS_TOKEN = centrifugoJwt.createJWT(user);
        //final String DEVICE_SUMMARY_CHANNEL = centrifugo.getActivitySummary();

       // String userId = userDetails.getUsername();  // or another method to get the user ID


//        var userId = jwtAuthenticationToken.getToken().getClaims();
//        var userId = user.getIdToken().getClaims().get("sub");
//        var username = principal.getName();

        Map<String, Object> response = new HashMap<>();
//        response.put("title", "Dashboard");
        response.put("token", CENTRIFUGO_ACCESS_TOKEN);
//        response.put("centrifugoWebSocket", CENTRIFUGO_WEBSOCKET);
      //  response.put("device_summary", DEVICE_SUMMARY_CHANNEL);

        return ResponseEntity.ok(response);
    }







}



