package mrsisa.project.controller;


import mrsisa.project.dto.InstructorDTO;
import mrsisa.project.model.Instructor;
import mrsisa.project.service.AddressService;
import mrsisa.project.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/instructor")
@CrossOrigin("*")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;


    @PutMapping(value = "/update")
    public ResponseEntity<InstructorDTO> updateInstructor(@RequestBody InstructorDTO dto)
    {
        Instructor instructor = instructorService.update(dto);
        if (instructor == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new InstructorDTO(instructor), HttpStatus.OK);
    }

    @GetMapping(value = "/get")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<InstructorDTO> getInstructor(Principal userP){
        return new ResponseEntity<>(new InstructorDTO(instructorService.findInstructorByUsername(userP.getName())), HttpStatus.OK);
    }

    @GetMapping(value="/getProfilePicture", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<InputStreamResource> getInstructorProfilePicture(Principal userP) throws IOException {
        Instructor instructor = instructorService.findInstructorByUsername(userP.getName());
        File file = new File(instructor.getProfilePhoto());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
