package com.example.Centrifugo;

import com.example.Centrifugo.config.CentrifugoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
@Slf4j
public class CentrifugoPublisher {

    private final RestTemplate restTemplate;
    private final CentrifugoConfiguration centrifugo;
//    private ReminderRepository reminderRepository;

    public ResponseEntity<CentrifugalDto<?>> sendImmobilizationToCentrifugo(Object data, String topic) {
        log.info("Pushing object -> {} to centrifugo topic -> {}",data,topic);
        final String API_KEY = centrifugo.getApiKey();
        final String CENTRIFUGO_URL = centrifugo.getUrl();
        final String CENTRIFUGO_METHOD = centrifugo.getMethod();

        log.info("URL -> {}",CENTRIFUGO_URL);

        var params = new CentrifugalDto.Params<Object>();
        params.setChannel(topic);
        params.setData(data);

        var payload = CentrifugalDto.<Object>builder()
                .method(CENTRIFUGO_METHOD)
                .params(params)
                .build();

        return sendToApi(API_KEY, CENTRIFUGO_URL, payload);
    }

    private <T> ResponseEntity<T> sendToApi(String apiKey, String url, T payload) {
        return sendToApi("apikey", apiKey, url, payload);
    }

    private <T> ResponseEntity<T> sendToApi(String tokenScheme, String token, String url, T payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, tokenScheme + " " + token);
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<T> request = new HttpEntity<>(payload, headers);
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api")
                .build()
                .toUri();

        var restResponse = restTemplate.exchange(uri, HttpMethod.POST, request,
                new ParameterizedTypeReference<T>() {
                });

        System.out.println("Centrifugo response "+ restResponse);
        return restResponse;

    }

//    public ResponseEntity<CentrifugalDto<?>> sendImmobilizationToCentrifugo(Object data, String channel) {
//        log.info("Pushing object -> {} to centrifugo channel -> {}", data, channel);
//        final String API_KEY = centrifugo.getApiKey();
//        final String CENTRIFUGO_URL = centrifugo.getUrl();
//        final String CENTRIFUGO_METHOD = centrifugo.getMethod();
//
//        log.info("URL -> {}", CENTRIFUGO_URL);
//
//        var params = new CentrifugalDto.Params<Object>();
//        params.setChannel(channel);
//        params.setData(data);
//
//        var payload = CentrifugalDto.<Object>builder()
//                .method(CENTRIFUGO_METHOD)
//                .params(params)
//                .build();
//
//        return sendToApi(API_KEY, CENTRIFUGO_URL, payload);



//    public class MessageService {
//
//        private static final String CENTRIFUGO_API_URL = "http://your-centrifugo-server/api";
//        private static final String CENTRIFUGO_API_KEY = "your-centrifugo-api-key";
//        private static final ObjectMapper objectMapper = new ObjectMapper();
//
//        public Response sendToConversation(UUID userAId, UUID userBId, MessageTable message) {
//            String channel = createChannelName(userAId, userBId);
//            return sendToChannel(channel, message);
//        }

//        private String createChannelName(UUID userAId, UUID userBId) {
//            UUID[] sortedIds = Stream.of(userAId, userBId).sorted().toArray(UUID[]::new);
//            return "chat_" + sortedIds[0] + "_" + sortedIds[1];
//        }
//
//        public Response sendToChannel(String channel, MessageTable message) {
//            try {
//                HttpClient client = HttpClient.newHttpClient();
//
//                Map<String, Object> payload = new HashMap<>();
//                payload.put("method", "publish");
//                payload.put("params", Map.of("channel", channel, "data", message));
//
//                String requestBody = objectMapper.writeValueAsString(payload);
//
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(new URI(CENTRIFUGO_API_URL))
//                        .header("Content-Type", "application/json")
//                        .header("Authorization", "apikey " + CENTRIFUGO_API_KEY)
//                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                        .build();
//
//                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//                if (response.statusCode() == 200) {
//                    return new Response("Message sent to channel: " + channel, 200);
//                } else {
//                    return new Response("Failed to send message to channel: " + channel, response.statusCode());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return new Response("Error sending message to channel: " + channel, 500);
//            }
//        }


//    public class MessageService {
//
//        private static final String CENTRIFUGO_API_URL = "http://your-centrifugo-server/api";
//        private static final String CENTRIFUGO_API_KEY = "your-centrifugo-api-key";
//        private static final ObjectMapper objectMapper = new ObjectMapper();
//
//        public Response sendToConversation(UUID userAId, UUID userBId, MessageTable message) {
//            String channel = createChannelName(userAId, userBId);
//            return sendToChannel(channel, message);
//        }
//
//        private String createChannelName(UUID userAId, UUID userBId) {
//            UUID[] sortedIds = Stream.of(userAId, userBId).sorted().toArray(UUID[]::new);
//            return "chat_" + sortedIds[0] + "_" + sortedIds[1];
//        }
//
//        public Response sendToChannel(String channel, MessageTable message) {
//            try {
//                HttpClient client = HttpClient.newHttpClient();
//
//                Map<String, Object> payload = new HashMap<>();
//                payload.put("method", "publish");
//                payload.put("params", Map.of("channel", channel, "data", message));
//
//                String requestBody = objectMapper.writeValueAsString(payload);
//
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(new URI(CENTRIFUGO_API_URL))
//                        .header("Content-Type", "application/json")
//                        .header("Authorization", "apikey " + CENTRIFUGO_API_KEY)
//                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                        .build();
//
//                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//                if (response.statusCode() == 200) {
//                    return new Response("Message sent to channel: " + channel, 200);
//                } else {
//                    return new Response("Failed to send message to channel: " + channel, response.statusCode());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return new Response("Error sending message to channel: " + channel, 500);
//            }
//        }

    }





