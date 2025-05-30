package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySuggestionDTO {
    private int id;
    private String name;
    private String description;
    private String industry;
    private String location;
    private String size;
    private int foundedYear;

     @Override
    public String toString() {
        return "CompanySuggestionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", industry='" + industry + '\'' +
                ", location='" + location + '\'' +
                ", size='" + size + '\'' +
                ", foundedYear=" + foundedYear +
                '}';
    }
}
