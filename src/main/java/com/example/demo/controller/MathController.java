package com.example.demo.controller;

import com.example.demo.service.MathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/math")
public class MathController {

    @Autowired
    private MathService mathService;

    @GetMapping("/prime/{n}")
    public boolean isPrime(@PathVariable int n) {
        return mathService.isPrime(n);
    }

    @GetMapping("/factorial/{n}")
    public long factorial(@PathVariable int n) {
        return mathService.factorial(n);
    }
}
