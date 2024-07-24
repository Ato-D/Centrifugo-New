package com.example.setups.service;


import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.FormDetailsDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.utility.ObjectNotValidException;
import com.example.setups.Form;
import com.example.setups.FormDetails;
import com.example.setups.repository.FormRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.Centrifugo.utility.AppUtils.*;

@Service
@AllArgsConstructor
@Slf4j
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    private final ModelMapper modelMapper;

    @Override
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

                response = getResponseDTO("Successfully retrieved all forms", HttpStatus.OK, formDTOList);
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


    @Override
    public ResponseEntity<ResponseDTO> findById(UUID id) {
        log.info("Inside find Form by Id ::: Trying to find a form by id -> {}", id);
        ResponseDTO response;
        try {
            var res = formRepository.findById(id);
            if (res.isPresent()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, res);
                response = getResponseDTO("Successfully retreived the form with id " + id, HttpStatus.OK, res);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
            log.info("No record found! statusCode -> {} and Message -> {}", HttpStatus.NOT_FOUND, res);
            response = (getResponseDTO("Not Found!", HttpStatus.NOT_FOUND));

        } catch (ResponseStatusException e) {
            log.error("Exception Occurred!  Reason -> {} and Message -> {}", e.getReason(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    /**
     * Saves a form to the repository.
     *
     * @param formDTO the DTO containing form data to be saved
     * @return ResponseEntity containing the ResponseDTO with the result of the save operation
     * Map the FormDTO to a Form entity using ModelMapper
     * Check if the formDTO has formDetails and populate formDetailsList
     * Set form details, created and updated timestamps, and user IDs
     * Save the form entity to the repository
     */

    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> saveForm(FormDTO formDTO) {
        log.info("Inside the save form method ::: Trying to save a form");

        ResponseDTO respose;
        try {
            var form = modelMapper.map(formDTO, Form.class);
            List<FormDetails> formDetailsList = new ArrayList<>();

            if (isNotNullOrEmpty(formDTO.getFormDetails())) {
                for (FormDetails details : formDTO.getFormDetails()) {
                    FormDetails formDetails = new FormDetails();
                    formDetails.setLabel(details.getLabel());
                    formDetails.setInputType(details.getInputType());
                    formDetails.setOption(details.getOption());
                    formDetails.setKeyValue(details.getKeyValue());
                    formDetails.setCreatedAt(ZonedDateTime.now());
                    formDetails.setCreatedBy(getAuthenticatedUserId());
                    formDetailsList.add(formDetails);
                }
            }

            form.setFormDetails(formDetailsList);
            form.setCreatedBy(getAuthenticatedUserId());
            form.setCreatedAt(ZonedDateTime.now());
            form.setUpdatedAt(ZonedDateTime.now());
            form.setCreatedBy(getAuthenticatedUserId());

            var record = formRepository.save(form);
            log.info("Saved record -> {}", record);

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


    @Override
    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
        ResponseDTO response = new ResponseDTO();

        try {

            Form existingForm = formRepository.findById(id)
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + "does not exixt"));

            // Check if the version in the request matches the version in the database

            if (formDTO.getVersion() != existingForm.getVersion()) {
                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
                        id, formDTO.getVersion(), existingForm.getVersion());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");

            }
            existingForm.setName(formDTO.getName());
            existingForm.setFormDetails(formDTO.getFormDetails());
            existingForm.setCreatedBy(getAuthenticatedUserId());
            existingForm.setCreatedAt(ZonedDateTime.now());
            existingForm.setUpdatedAt(ZonedDateTime.now());


            existingForm.setVersion(existingForm.getVersion() + 1);

            var record = formRepository.save(existingForm);
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

}


//    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
//        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
//        ResponseDTO response;
//
//        try {
//            Form existingForm = formRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + " does not exist"));
//
//            if (formDTO.getVersion() != existingForm.getVersion()) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
//            }
//
//            existingForm.setName(formDTO.getName());
//
//            List<FormDetails> formDetailsList = new ArrayList<>();
//            if (isNotNullOrEmpty(formDTO.getFormDetails())) {
//                for (FormDetailsDTO detailsDTO : formDTO.getFormDetails()) {
//                    FormDetails formDetails = formDetailsRepository.findById(detailsDTO.getId())
//                            .orElse(new FormDetails());
//
//                    formDetails.setLabel(detailsDTO.getLabel());
//                    formDetails.setInputType(detailsDTO.getInputType());
//                    formDetails.setOption(detailsDTO.getOption());
//                    formDetails.setKeyValue(detailsDTO.getKeyValue());
//                    formDetails.setCreatedAt(detailsDTO.getCreatedAt() != null ? detailsDTO.getCreatedAt() : ZonedDateTime.now());
//                    formDetails.setCreatedBy(detailsDTO.getCreatedBy() != null ? detailsDTO.getCreatedBy() : getAuthenticatedUserId());
//                    formDetailsList.add(formDetails);
//                }
//            }
//
//            existingForm.setFormDetails(formDetailsList);
//            existingForm.setUpdatedAt(ZonedDateTime.now());
//            existingForm.setUpdatedBy(getAuthenticatedUserId());
//
//            // Increment the version
//            existingForm.setVersion(existingForm.getVersion() + 1);
//
//            var record = formRepository.save(existingForm);
