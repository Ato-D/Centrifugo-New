package com.example.Centrifugo.form.service;


import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.enums.Constraints;
import com.example.Centrifugo.form.Form;
import com.example.Centrifugo.form.FormDetails;
import com.example.Centrifugo.form.repository.FormDetailsRepository;
import com.example.Centrifugo.form.repository.FormRepository;
import com.example.Centrifugo.utility.ObjectNotValidException;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.example.Centrifugo.utility.AppUtils.*;
import static com.example.Centrifugo.utility.AppUtils.getAuthenticatedUserId;

@Service
@AllArgsConstructor
@Slf4j
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    private final FormDetailsRepository formDetailsRepository;

    private final ModelMapper modelMapper;


    @Override
    public ResponseEntity<ResponseDTO> findAllForms(Map<String, String> params) {
        log.info("Inside find All Forms :::::: Trying to fetch forms per given params");
        ResponseDTO response;

        try {
            List<Form> forms;
            forms = formRepository.findAll();
            if (!forms.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, forms);
                List<FormDTO> formDTOList = forms.stream()
                        .map(form -> {
                            FormDTO formDTO = new FormDTO();
                            formDTO.setId(form.getId());
                            formDTO.setName(form.getName());
                            formDTO.setVersion(form.getVersion());
                            formDTO.setIsEnabled(form.getIsEnabled());
                            formDTO.setFormDetails(form.getFormDetails().stream()
                                    .map(detail -> FormDetails.builder()
                                            .id(detail.getId())
                                            .index(detail.getIndex())
                                            .fieldLabel(detail.getFieldLabel())
                                            .fieldOptions(detail.getFieldOptions())
                                            .isRequired(detail.getIsRequired())
                                            .defaultValue(detail.getDefaultValue())
                                            .placeholder(detail.getPlaceholder())
                                            .fieldType(detail.getFieldType())
                                            .constraints(detail.getConstraints())
                                            .key(detail.getKey())
                                            .createdBy(detail.getCreatedBy())
                                            .createdAt(ZonedDateTime.now())
                                            .updatedBy(getAuthenticatedUserId())
                                            .updatedAt(ZonedDateTime.now())
                                            .build())
                                    .collect(Collectors.toList()));
                            formDTO.setCreatedBy(form.getCreatedBy());
                            formDTO.setCreatedAt(ZonedDateTime.now());
                            formDTO.setUpdatedBy(getAuthenticatedUserId());
                            formDTO.setUpdatedAt(ZonedDateTime.now());
                            return formDTO;
                        })
                        .collect(Collectors.toList());

                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 204, HttpStatus.NO_CONTENT, "Record Not Found");
                response = getResponseDTO("Successfully retrieved all forms", HttpStatus.OK, formDTOList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                log.info("Not Found! statusCode -> {}, Cause -> {}, Message -> {}", 404, HttpStatus.NOT_FOUND, "Record Not Found");
                response = getResponseDTO("No record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
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

        ResponseDTO response;
        try {
            Form form = new Form();
            form.setName(formDTO.getName());
            form.setVersion(1);
            form.setCreatedBy(getAuthenticatedUserId());
            form.setCreatedAt(ZonedDateTime.now());
            form.setUpdatedBy(getAuthenticatedUserId());
            form.setUpdatedAt(ZonedDateTime.now());

            List<FormDetails> formDetailsList = new ArrayList<>();

            int indexCounter = 1;

            if (isNotNullOrEmpty(formDTO.getFormDetails())) {
                for (FormDetails details : formDTO.getFormDetails()) {

                    try {
                        int indexToCheck = indexCounter;
                        boolean indexExists = formDetailsList.stream()
                                .anyMatch(fd -> fd.getIndex() == indexToCheck);
                        if (indexExists) {
                            log.info("Duplicate index found: " + indexToCheck);
                            throw new IllegalArgumentException("Duplicate index found: " + indexToCheck);
                        }
                        FormDetails formDetails = new FormDetails();
                        formDetails.setIndex(details.getIndex());
                        formDetails.setFieldLabel(details.getFieldLabel());
                        formDetails.setFieldOptions(details.getFieldOptions());
                        formDetails.setIsRequired(details.getIsRequired());
                        formDetails.setDefaultValue(details.getDefaultValue());
                        formDetails.setPlaceholder(details.getPlaceholder());
                        formDetails.setFieldType(details.getFieldType());
                        formDetails.setConstraints(details.getConstraints());
                        formDetails.setKey(details.getKey());
                        formDetails.setCreatedBy(getAuthenticatedUserId());
                        formDetails.setCreatedAt(ZonedDateTime.now());
                        formDetails.setUpdatedBy(getAuthenticatedUserId());
                        formDetails.setUpdatedAt(ZonedDateTime.now());

                        formDetailsList.add(formDetails);
                        indexCounter++;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            form.setFormDetails(formDetailsList);
            for (FormDetails formDetails : formDetailsList) {
                formDetails.setForm(Collections.singletonList(form));
            }
            var record = formRepository.save(form);
            log.info("Saved record -> {}", record);

            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.CREATED, record);
            response = getResponseDTO("Record Saved Successfully", HttpStatus.OK, record);

        } catch (ResponseStatusException e) {
            log.error("Error Occurred! statusCode -> {}, Message -> {}, Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occurred! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);

        } catch (DataIntegrityViolationException e) {
            log.error("Exception Occurred! Message -> {} and Cause -> {}", e.getMostSpecificCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

    }


    /**
     * Fetch the existing form from the repository using the provided id
     * Check if the version in the request matches the version in the database
     * Create a new Form instance to represent the updated version.
     * Set the id to a new UUID to ensure a new record is created
     * Copy the details from formDTO to the new form
     * Retain the original creation time and creator information from the existing form
     * Set the updatedAt field to the current time
     * Increment the version number
     */


    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
        ResponseDTO response;
        try {
            Form existingForm = formRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form not found"));

            int currentVersion = existingForm.getVersion();
            System.out.println(currentVersion);
            int newVersion = currentVersion + 1;

            Form newForm = new Form();
            newForm.setId(UUID.randomUUID());
            newForm.setName(formDTO.getName());
            newForm.setIsEnabled(formDTO.getIsEnabled());
            newForm.setVersion(newVersion);
            newForm.setCreatedBy(getAuthenticatedUserId());
            newForm.setCreatedAt(ZonedDateTime.now());
            newForm.setUpdatedBy(getAuthenticatedUserId());
            newForm.setUpdatedAt(ZonedDateTime.now());

            // Copy form details
            List<FormDetails> newFormDetailsList = new ArrayList<>();
            for (FormDetails formDetails : existingForm.getFormDetails()) {
                FormDetails newFormDetails = new FormDetails();
                newFormDetails.setId(UUID.randomUUID());
                newFormDetails.setIndex(formDetails.getIndex());
                newFormDetails.setFieldLabel(formDetails.getFieldLabel());
                newFormDetails.setFieldOptions(formDetails.getFieldOptions());
                newFormDetails.setIsRequired(formDetails.getIsRequired());
                newFormDetails.setDefaultValue(formDetails.getDefaultValue());
                newFormDetails.setPlaceholder(formDetails.getPlaceholder());
                newFormDetails.setFieldType(formDetails.getFieldType());
                newFormDetails.setConstraints(formDetails.getConstraints());
                newFormDetails.setKey(formDetails.getKey());
                newFormDetails.setCreatedBy(getAuthenticatedUserId());
                newFormDetails.setCreatedAt(ZonedDateTime.now());
                newFormDetails.setUpdatedBy(getAuthenticatedUserId());
                newFormDetails.setUpdatedAt(ZonedDateTime.now());
                // Set the new form as the parent for form details
                newFormDetails.setForm(Collections.singletonList(newForm));

                newFormDetailsList.add(newFormDetails);
            }
            newForm.setFormDetails(newFormDetailsList);

            if (formDTO.getVersion() != existingForm.getVersion()) {
                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
                        id, formDTO.getVersion(), existingForm.getVersion());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
            }

            Form savedForm = formRepository.save(newForm);
            response = getResponseDTO("Form updated successfully", HttpStatus.OK, savedForm);
        } catch (ResponseStatusException e) {
            log.error("Exception Occurred! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Error Occurred! statusCode -> {} and Cause -> {} and Message -> {}", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @Override
    public ResponseEntity<ResponseDTO> disable(UUID id, FormDTO formDTO) {
        log.info("Inside the disable form :::: Trying to disable a form by the given id");
        ResponseDTO response;

        try {
           var existingForm = formRepository.findById(id)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form Record " + "does not exist"));
           existingForm.setIsEnabled(formDTO.getIsEnabled());
           existingForm.setUpdatedBy(UUID.fromString(authentication().getName()));
           existingForm.setUpdatedAt(ZonedDateTime.now());

           var record = formRepository.save(existingForm);

           log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, "Record activity set to " + formDTO.getIsEnabled());
           response = getResponseDTO("Record activity updated to " + existingForm.getIsEnabled(), HttpStatus.ACCEPTED, record);
        } catch (ResponseStatusException e) {
            log.error("Exception Occurred!, Message -> {}, Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception occurred! Reason -> {}", message);
            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }




//            private List<FormDetails> mapListForConstraints(List<FormDetails> res) {
//                return res.stream()
//                        .map(formDetails -> {
//                            String constraintRepresentation = formDetails.getConstraints().toString();
//                            var keyValues = Constraints.getKeyValues();
//                            if (keyValues.containsKey(constraintRepresentation)) {
//                                formDetails.setConstraints(keyValues.get(constraintRepresentation));
//                            }
//                            return formDetails;
//                        })
//                        .collect(Collectors.toList());
//            }


}