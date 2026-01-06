package management.backend.inventory.controller;

import management.backend.inventory.entity.Grade;
import management.backend.inventory.repository.GradeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import management.backend.inventory.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeRepository gradeRepository;

    public GradeController(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Grade>> getAllGrades() {
        return ResponseEntity.ok(gradeRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Grade> createGrade(@RequestBody Grade grade) {
        if (gradeRepository.findByGradeNumber(grade.getGradeNumber()).isPresent()) {
            throw new IllegalArgumentException("Grade number already exists");
        }
        return ResponseEntity.ok(gradeRepository.save(grade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Grade> updateGrade(@PathVariable Long id, @RequestBody Grade gradeDetails) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
        
        grade.setGradeNumber(gradeDetails.getGradeNumber());
        grade.setDescription(gradeDetails.getDescription());
        
        return ResponseEntity.ok(gradeRepository.save(grade));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
        gradeRepository.delete(grade);
        return ResponseEntity.ok().build();
    }
}
