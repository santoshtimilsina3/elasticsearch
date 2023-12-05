package com.elk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id ;
    @NotBlank @NotNull
    private String name;
    @NotBlank @NotNull
    private BigDecimal price;
}
