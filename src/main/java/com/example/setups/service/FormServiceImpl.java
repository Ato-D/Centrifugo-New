package com.example.setups.service;


import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.FormDetailsDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.setups.Form;
import com.example.setups.FormDetails;
import com.example.setups.repository.FormRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.Centrifugo.utility.AppUtils.getResponseDTO;

@Service
@AllArgsConstructor
@Slf4j
public class FormServiceImpl {

    private final FormRepository formRepository;

    public ResponseEntity<ResponseDTO> findAllForms(Map<String, String> params) {
        log.info("Inside find All Forms :::::: Trying to fetch forms per given params");
        ResponseDTO response;

        try {
            List<Form> forms = formRepository.findAll();
            if (!forms.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, forms);
                List<FormDTO> formDTOList = forms.stream()
                        .map(form -> {
                            FormDTO formDTO = new FormDTO();
                            formDTO.setId(form.getId());
                            formDTO.setName(form.getName());
                            formDTO.setFormDetails(form.getFormDetails().stream()
                                    .map(detail -> FormDetails.builder()
                                            .id(detail.getId())
                                            .label(detail.getLabel())
                                            .keyValue(detail.getKeyValue())
                                            .inputType(detail.getInputType())
                                            .createdAt(ZonedDateTime.now())
                                            .createdBy(detail.getCreatedBy())
                                            .updatedAt(ZonedDateTime.now())
                                            .option(detail.getOption())
                                            .build())
                                    .collect(Collectors.toList()));
                            formDTO.setCreatedAt(ZonedDateTime.now());
                            formDTO.setCreatedBy(form.getCreatedBy());
                            formDTO.setUpdatedAt(ZonedDateTime.now());
                            return formDTO;
                        })
                        .collect(Collectors.toList());

                response = getResponseDTO("Successfully retrieved all messages", HttpStatus.OK, formDTOList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response = getResponseDTO("No record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (ResponseStatusException e) {
            log.error("Exception Occured! and Message -> {} and Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
            return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! StatusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<ResponseDTO> findById(UUID id) {
        log.info("Inside find JobCard by Id ::: Trying to find jobCard by id -> {}", id);
        ResponseDTO response;
        try {
            var res = formRepository.findById(id);
            if (res.isPresent()){
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, res);
                response = getResponseDTO("Successfully retreived the message with id " + id, HttpStatus.OK, res);
                return new ResponseEntity<>(response,HttpStatus.valueOf(response.getStatusCode()));
            }
            log.info("No record found! statusCode -> {} and Message -> {}", HttpStatus.NOT_FOUND, res);
            response = (getResponseDTO("Not Found!", HttpStatus.NOT_FOUND));

        } catch (ResponseStatusException e) {
            log.error("Exception Occurred!  Reason -> {} and Message -> {}",e.getReason(),e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(),e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.valueOf(response.getStatusCode()));
    }

}
