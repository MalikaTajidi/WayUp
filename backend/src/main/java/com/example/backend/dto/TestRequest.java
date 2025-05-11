package com.example.backend.dto;

import java.util.List;

import lombok.Data;
@Data
public class TestRequest {
    private List<String> questions;
    private List<String> answers;
}