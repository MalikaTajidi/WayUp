package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entities.Skill;
import com.example.backend.repository.SkillRepository;
import com.example.backend.repository.UserRepository;

// SkillController.java
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
public ResponseEntity<List<Skill>> getSkillsByUserId(@PathVariable int userId) {
    List<Skill> skills = skillRepository.findByUserId(userId);
    return ResponseEntity.ok(skills);
}


   @PutMapping("/{skillId}")
public ResponseEntity<Skill> updateSkill(@PathVariable Long skillId, @RequestBody Skill updatedSkill) {
    Optional<Skill> skillOpt = skillRepository.findById(skillId);
    if (skillOpt.isEmpty()) return ResponseEntity.notFound().build();

    Skill skill = skillOpt.get();
    skill.setAcquired(updatedSkill.isAcquired());
    skillRepository.save(skill);
    return ResponseEntity.ok(skill);
}

}

