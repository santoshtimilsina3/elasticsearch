package com.elk.model;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Department {
    private Long id;
    private String name;
    private LocalDate createdAt;

    @PostConstruct
    public void init(){
        this.setCreatedAt(LocalDate.now());
    }
}