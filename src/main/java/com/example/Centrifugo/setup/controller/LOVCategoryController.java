package com.example.Centrifugo.setup.controller;


import com.example.Centrifugo.dto.LOVCategoryDTO;
import com.example.Centrifugo.dto.LOVDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.setup.model.LOVCategory;
import com.example.Centrifugo.setup.repository.LOVCategoryRepository;
import com.example.Centrifugo.setup.service.LOVCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.example.Centrifugo.config.SecurityConfig.CONTEXT_PATH;

@RestController
@RequestMapping(CONTEXT_PATH)
@AllArgsConstructor
@Slf4j
public class LOVCategoryController {

    private final LOVCategoryService lovCategoryService;


    /**
     * Handles a GET request to retrieve all categories.
     *
     * @return ResponseEntity containing the ResponseDTO with the list of categories.
     *
     */
    @GetMapping("/find-all-categories")
   public ResponseEntity<ResponseDTO> getAllCategories(@RequestParam(defaultValue = "{}") Map<String, String> params) {
       return lovCategoryService.findAllCategories(params);
   }

    /**
     * Handles a GET request to retrieve a category by its ID.
     *
     * @param id The ID of the form to retrieve.
     * @return ResponseEntity containing the ResponseDTO with the requested form.
     */
    @GetMapping("/get-categoryId/{id}")
    public ResponseEntity<ResponseDTO> getCategoryId(@PathVariable (name = "id")UUID id) {
        return lovCategoryService.findById(id);
    }

   @PostMapping("/create-category")
   public ResponseEntity<ResponseDTO> createCategory(@RequestBody LOVCategoryDTO lovCategoryDTO){
        return lovCategoryService.createCategory(lovCategoryDTO);
   }

    /**
     * Handles a PUT request to update an existing form.
     *
     * @param lovCategoryDTO The updated LOVCategoryDTO containing information to update category.
     * @param id The ID of the category to be updated.
     * @return ResponseEntity containing the ResponseDTO with the updated category.
     */
   @PutMapping("/update-category/{id}")
   public ResponseEntity<ResponseDTO> updateCategory(@PathVariable (name = "id")UUID id, LOVCategoryDTO lovCategoryDTO) {
        return lovCategoryService.updateCategory(id, lovCategoryDTO);
   }

    /**
     * Handles a PUT request to disable a category.
     *
     * @param lovCategoryDTO The LOVCategoryDTO containing enabled field to disable or enable a category.
     * @param id The ID of the tyre brand to be disabled or enabled.
     * @return ResponseEntity containing the ResponseDTO.
     */
    @PutMapping("/disable-category/{id}")
    public ResponseEntity<ResponseDTO> disableCategory(@RequestBody LOVCategoryDTO lovCategoryDTO, @PathVariable(name = "id") UUID id ){
        return lovCategoryService.disableCategory(id,lovCategoryDTO);
    }

}
