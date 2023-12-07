package com.elk.model;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Department {
    private Long id;
    private String name;
    private LocalDate createdAt;
    private List<Employee> employees;

    @PostConstruct
    public void init() {
        this.setCreatedAt(LocalDate.now());
    }
}