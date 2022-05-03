package mrsisa.project.controller;


import mrsisa.project.dto.AdventureDTO;
import mrsisa.project.dto.InstructorDTO;
import mrsisa.project.model.Instructor;
import mrsisa.project.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/instructor")
@CrossOrigin("*")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @PostMapping(path = "/add")
    public ResponseEntity<String> addInstructor(@RequestPart("instructor") InstructorDTO instructorDTO,
                                                @RequestPart("files") MultipartFile[] multiPartFiles) throws IOException
    {
        boolean added = instructorService.add(instructorDTO, multiPartFiles);
        if (!added) {
            return ResponseEntity.status(HttpStatus.IM_USED).body("NC");  // Not Created
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }
}