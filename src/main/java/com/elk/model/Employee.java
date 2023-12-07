package com.elk.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long id;
    @NotNull
    private String name;
    private String email;
    private LocalDate createdAt;
    @NotNull
    private Long departmentId;
    private Long phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(name, employee.name) && Objects.equals(email, employee.email) && Objects.equals(createdAt, employee.createdAt) && Objects.equals(departmentId, employee.departmentId) && Objects.equals(phone, employee.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, createdAt, departmentId, phone);
    }
}