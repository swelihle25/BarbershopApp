package com.barbershop.queue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShopsController {

    @GetMapping("/shops")
    public List<Map<String, Object>> getShops(){
        return List.of(
                Map.of("id", "1", "name", "Ferndale"),
                Map.of("id", "2", "name", "Cape Town"),
                Map.of("id", "3", "name", "Mall of Africa"),
                Map.of("id", "4", "name", "Cresta"),
                Map.of("id", "5", "name", "Montana")


        );
    }
}