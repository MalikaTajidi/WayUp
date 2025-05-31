package com.example.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Internship {
    private int id;
    private String title;
    private String company;
    private String location;
    private String duration;
    private String description;
    private String startDate;

    @Override
    public String toString() {
        return "Internship{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
