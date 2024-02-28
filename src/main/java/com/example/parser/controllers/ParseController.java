package com.example.parser.controllers;

import com.example.parser.services.ParseProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/parse")
public class ParseController {
    private final ParseProductService parseProductService;

    @Autowired
    public ParseController(ParseProductService parseProductService) {
        this.parseProductService = parseProductService;
    }

    @GetMapping("/products/csv")
    public ResponseEntity<InputStreamResource> parseToCSV() {
        try {
            // Получаем CSV данные в виде строки
            String csvData = this.parseProductService.toCSV();

            // Преобразуем строку в ByteArrayInputStream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes());

            // Создаем объект InputStreamResource из ByteArrayInputStream
            InputStreamResource resource = new InputStreamResource(inputStream);

            // Настройка заголовков для файла
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv");

            // Возвращаем ResponseEntity с телом файла и настроенными заголовками
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
