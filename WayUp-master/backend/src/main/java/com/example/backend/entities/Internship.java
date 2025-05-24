package com.example.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Internship {
    
    private Long id;
    private String name;
    private String description;
    private String industry;
    private String location;
    private String duration;
    private String stipend;
    
    // Default constructor needed for Jackson
    public Internship() {
    }
    
    // Constructor with all fields
    public Internship(Long id, String name, String description, String industry, 
                      String location, String duration, String stipend) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.location = location;
        this.duration = duration;
        this.stipend = stipend;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getStipend() {
        return stipend;
    }
    
    public void setStipend(String stipend) {
        this.stipend = stipend;
    }
    
    @Override
    public String toString() {
        return "Internship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", industry='" + industry + '\'' +
                ", location='" + location + '\'' +
                ", duration='" + duration + '\'' +
                ", stipend='" + stipend + '\'' +
                '}';
    }
}