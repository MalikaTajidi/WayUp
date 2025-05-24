package com.example.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InternshipSuggestionDTO {
    private int id;
    private String title;
    private String company;
    private String location;
    private String duration;
    private String description;
    private String requirements;

    @Override
    public String toString() {
        return "InternshipSuggestionDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                '}';
    }
}
