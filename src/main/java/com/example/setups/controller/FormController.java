package com.example.setups.controller;


import com.example.setups.service.FormService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/form")
@AllArgsConstructor
@Slf4j

public class FormController {

    private final FormService formService;
}
