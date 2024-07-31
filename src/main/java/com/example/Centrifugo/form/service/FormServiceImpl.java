package com.example.Centrifugo.form.service;


import com.example.Centrifugo.dto.FormDTO;
import com.example.Centrifugo.dto.FormDetailsDTO;
import com.example.Centrifugo.dto.ResponseDTO;
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
           forms  = formRepository.findAll();
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
                                            .key(detail.getKey())
                                            .constraints(detail.getConstraints())
                                            .option(detail.getOption())
                                            .inputType(detail.getInputType())
                                            .createdBy(detail.getCreatedBy())
                                            .createdAt(ZonedDateTime.now())
                                            .updatedBy(getAuthenticatedUserId())
                                            .updatedAt(ZonedDateTime.now())
                                            .build())
                                    .collect(Collectors.toList()));
                            formDTO.setCreatedAt(ZonedDateTime.now());
                            formDTO.setCreatedBy(form.getCreatedBy());
                            formDTO.setUpdatedBy(getAuthenticatedUserId());
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
//    public ResponseEntity<ResponseDTO> saveForm(FormDTO formDTO) {
//        log.info("Inside the save form method ::: Trying to save a form");
//
//        ResponseDTO respose;
//        try {
//            var form = modelMapper.map(formDTO, Form.class);
//            List<FormDetails> formDetailsList = new ArrayList<>();
//
//            if (isNotNullOrEmpty(formDTO.getFormDetails())) {
//                for (FormDetails details : formDTO.getFormDetails()) {
//                    FormDetails formDetails = new FormDetails();
//                    formDetails.setLabel(details.getLabel());
//                    formDetails.setInputType(details.getInputType());
//                    formDetails.setConstraints(details.getConstraints());
//                    formDetails.setOption(details.getOption());
//                    formDetails.setKey(details.getKey());
//                    formDetails.setCreatedAt(ZonedDateTime.now());
//                    formDetails.setCreatedBy(getAuthenticatedUserId());
//                    formDetails.setUpdatedBy(getAuthenticatedUserId());
//                    formDetails.setUpdatedAt(ZonedDateTime.now());
//                    formDetailsList.add(formDetails);
//                }
//            }
//
//            form.setVersion(formDTO.getVersion());
//            form.setFormDetails(formDetailsList);
//            form.setCreatedBy(getAuthenticatedUserId());
//            form.setCreatedAt(ZonedDateTime.now());
//            form.setUpdatedBy(getAuthenticatedUserId());
//            form.setUpdatedAt(ZonedDateTime.now());
//
//            // Set the forms in each form detail to maintain the bidirectional relationship
//            for (FormDetails formDetails : formDetailsList) {
//                formDetails.setForm(Collections.singletonList(form));
//            }
//
//
//            var record = formRepository.save(form);
//            log.info("Saved record -> {}", record);
//
//            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.CREATED, record);
//            respose = getResponseDTO("Record Saved Successfully", HttpStatus.OK, record);
//
//        } catch (ResponseStatusException e) {
//            log.error("Error Occurred! statusCode -> {}, Message -> {}, Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
//            respose = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
//        } catch (ObjectNotValidException e) {
//            var message = String.join("\n", e.getErrorMessages());
//            log.info("Exception Occurred! Reason -> {}", message);
//            respose = getResponseDTO(message, HttpStatus.BAD_REQUEST);
//
//        } catch (DataIntegrityViolationException e) {
//            log.error("Exception Occurred! Message -> {} and Cause -> {}", e.getMostSpecificCause(), e.getMessage());
//            respose = getResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
//            respose = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(respose, HttpStatus.valueOf(respose.getStatusCode()));
//    }


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

            // Create list for FormDetails
            List<FormDetails> formDetailsList = new ArrayList<>();

            // Iterate through form details provided in the DTO
            if (isNotNullOrEmpty(formDTO.getFormDetails())) {
                for (FormDetails details : formDTO.getFormDetails()) {
                    FormDetails formDetails = new FormDetails();
                    formDetails.setLabel(details.getLabel());
                    formDetails.setInputType(details.getInputType());
                    formDetails.setConstraints(details.getConstraints());
                    formDetails.setOption(details.getOption());
                    formDetails.setKey(details.getKey());
                    formDetails.setCreatedAt(ZonedDateTime.now());
                    formDetails.setCreatedBy(getAuthenticatedUserId());
                    formDetails.setUpdatedBy(getAuthenticatedUserId());
                    formDetails.setUpdatedAt(ZonedDateTime.now());

                    formDetailsList.add(formDetails);
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
                    newForm.setVersion(newVersion);
                    System.out.println(newVersion);
                    newForm.setCreatedBy(getAuthenticatedUserId());
                    newForm.setCreatedAt(ZonedDateTime.now());
                    newForm.setUpdatedBy(getAuthenticatedUserId());
                    newForm.setUpdatedAt(ZonedDateTime.now());

                    // Copy form details
                    List<FormDetails> newFormDetailsList = new ArrayList<>();
                    for (FormDetails formDetails : existingForm.getFormDetails()) {
                        FormDetails newFormDetails = new FormDetails();
                        newFormDetails.setId(UUID.randomUUID());
                        newFormDetails.setLabel(formDetails.getLabel());
                        newFormDetails.setInputType(formDetails.getInputType());
                        newFormDetails.setOption(formDetails.getOption());
                        newFormDetails.setKey(formDetails.getKey());
                        newFormDetails.setConstraints(formDetails.getConstraints());
                        newFormDetails.setCreatedAt(ZonedDateTime.now());
                        newFormDetails.setCreatedBy(getAuthenticatedUserId());
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


//    @Override
//    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
//        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
//        ResponseDTO response = new ResponseDTO();
//
//        try {
//            Form existingForm = formRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + " does not exist"));
//
//            if (formDTO.getVersion() != existingForm.getVersion()) {
//                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
//                        id, formDTO.getVersion(), existingForm.getVersion());
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
//            }
//
//            Form newForm = new Form();
//            newForm.setId(UUID.randomUUID());
//            newForm.setName(formDTO.getName());
//            newForm.setFormDetails(formDTO.getFormDetails());
//            newForm.setCreatedBy(getAuthenticatedUserId());
//            newForm.setCreatedAt(existingForm.getCreatedAt());
//            newForm.setUpdatedBy(getAuthenticatedUserId());
//            newForm.setUpdatedAt(ZonedDateTime.now());
//            newForm.setVersion(existingForm.getVersion() + 1);
//
//            var savedForm = formRepository.save(newForm);
//
//            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, savedForm);
//        } catch (ResponseStatusException e) {
//            log.error("Exception Occurred! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
//            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
//        } catch (ObjectNotValidException e) {
//            var message = String.join("\n", e.getErrorMessages());
//            log.info("Exception Occurred! Reason -> {}", message);
//            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Error Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
//            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }


//    @Override
//    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
//        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
//        ResponseDTO response = new ResponseDTO();
//
//        try {
//            Form existingForm = formRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + " does not exist"));
//
//            // Check if the version in the request matches the version in the database
//            if (formDTO.getVersion() != existingForm.getVersion()) {
//                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
//                        id, formDTO.getVersion(), existingForm.getVersion());
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
//            }
//
//            Form newForm = new Form();
//            newForm.setId(UUID.randomUUID());
//            newForm.setName(formDTO.getName());
//            newForm.setFormDetails(formDTO.getFormDetails());
//            newForm.setCreatedBy(getAuthenticatedUserId());
//            newForm.setCreatedAt(existingForm.getCreatedAt());
//            newForm.setUpdatedBy(getAuthenticatedUserId());
//            newForm.setUpdatedAt(ZonedDateTime.now());
//            newForm.setVersion(existingForm.getVersion() + 1);
//
//            var savedForm = formRepository.save(newForm);
//            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, savedForm);
//        } catch (ResponseStatusException e) {
//            log.error("Exception Occurred! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
//            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
//        } catch (ObjectNotValidException e) {
//            var message = String.join("\n", e.getErrorMessages());
//            log.info("Exception Occurred! Reason -> {}", message);
//            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Error Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
//            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }
//    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
//        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
//        ResponseDTO response = new ResponseDTO();
//
//        try {
//
//            Form existingForm = formRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + "does not exixt"));
//
//            // Check if the version in the request matches the version in the database
//
//            if (formDTO.getVersion() != existingForm.getVersion()) {
//                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
//                        id, formDTO.getVersion(), existingForm.getVersion());
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
//
//            }
//            existingForm.setName(formDTO.getName());
//            existingForm.setFormDetails(formDTO.getFormDetails());
//            existingForm.setCreatedBy(getAuthenticatedUserId());
//            existingForm.setCreatedAt(ZonedDateTime.now());
//            existingForm.setUpdatedAt(ZonedDateTime.now());
//
//
//            existingForm.setVersion(existingForm.getVersion() + 1);
//
//            var record = formRepository.save(existingForm);
//            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, record);
//        } catch (ResponseStatusException e) {
//            log.error("Exception Occured! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
//            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
//        } catch (ObjectNotValidException e) {
//            var message = String.join("\n", e.getErrorMessages());
//            log.info("Exception Occured! Reason -> {}", message);
//            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Error Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
//            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }

}




//    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
//        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
//        ResponseDTO response = new ResponseDTO();
//
//        try {
//            Form existingForm = formRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + " does not exist"));
//
//            // Check if the version in the request matches the version in the database
//            if (!formDTO.getVersion().equals(existingForm.getVersion())) {
//                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
//                        id, formDTO.getVersion(), existingForm.getVersion());
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
//            }
//
//            Form newForm = new Form();
//            newForm.setId(UUID.randomUUID()); // Generate a new UUID for the new form version
//            newForm.setName(formDTO.getName());
//            newForm.setFormDetails(formDTO.getFormDetails());
//            newForm.setCreatedBy(existingForm.getCreatedBy()); // Keep the original creator
//            newForm.setCreatedAt(existingForm.getCreatedAt()); // Keep the original creation time
//            newForm.setUpdatedAt(ZonedDateTime.now());
//            newForm.setVersion(existingForm.getVersion() + 1);
//
//            Form savedForm = formRepository.save(newForm);
//            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, savedForm);
//        } catch (ResponseStatusException e) {
//            log.error("Exception Occurred! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
//            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
//        } catch (ObjectNotValidException e) {
//            var message = String.join("\n", e.getErrorMessages());
//            log.info("Exception Occurred! Reason -> {}", message);
//            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Error Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
//            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }


//        Fetch the existing form from the repository using the provided id
//        Create a new Form instance to represent the updated version.
//        Set the id to a new UUID to ensure a new record is created.
//        Copy the details from formDTO to the new form.
//        Retain the original creation time and creator information from the existing form.
//        Set the updatedAt field to the current time.
//        Increment the version number.


//    public ResponseEntity<ResponseDTO> update(UUID id, FormDTO formDTO) {
//        log.info("Inside Update Form :::: Trying to update Form -> {}", id);
//        ResponseDTO response = new ResponseDTO();
//
//        try {
//            // Retrieve the existing form
//            Form existingForm = formRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Existing Form Record " + id + " does not exist"));
//
//            // Check if the version in the request matches the version in the database
//            if (!formDTO.getVersion().equals(existingForm.getVersion())) {
//                log.error("Form version mismatch detected for Form ID -> {}. Request Version -> {}, Existing Version -> {}",
//                        id, formDTO.getVersion(), existingForm.getVersion());
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Form version mismatch");
//            }
//
//            // Create a new form record with updated details
//            Form newForm = new Form();
//            newForm.setId(UUID.randomUUID()); // Generate a new UUID for the new form version
//            newForm.setName(formDTO.getName());
//            newForm.setFormDetails(formDTO.getFormDetails());
//            newForm.setCreatedBy(getAuthenticatedUserId());
//            newForm.setCreatedAt(existingForm.getCreatedAt()); // Keep the original creation time
//            newForm.setUpdatedAt(ZonedDateTime.now());
//            newForm.setVersion(existingForm.getVersion() + 1);
//
//            // Save the new version of the form
//            Form savedForm = formRepository.save(newForm);
//            response = getResponseDTO("Record Updated Successfully", HttpStatus.ACCEPTED, savedForm);
//        } catch (ResponseStatusException e) {
//            log.error("Exception Occurred! statusCode -> {} and Message -> {} and Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
//            response = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
//        } catch (ObjectNotValidException e) {
//            var message = String.join("\n", e.getErrorMessages());
//            log.info("Exception Occurred! Reason -> {}", message);
//            response = getResponseDTO(message, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Error Occurred! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
//            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }



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
