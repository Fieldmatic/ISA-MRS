package mrsisa.project.controller;

import mrsisa.project.dto.CottageDTO;
import mrsisa.project.model.Cottage;
import mrsisa.project.model.CottageOwner;
import mrsisa.project.repository.PersonRepository;
import mrsisa.project.service.CottageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/cottage")
public class CottageController {
    @Autowired
    private CottageService cottageService;

    @Autowired
    private PersonRepository personRepository;


    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ResponseEntity<String> addCottage(@RequestPart("cottage") CottageDTO dto, @RequestPart(value = "files",required = false) MultipartFile[] multiPartFiles, Principal userP) throws IOException {
        cottageService.add(dto, Optional.ofNullable(multiPartFiles), userP);
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }

    @GetMapping(value="/all")
    public ResponseEntity<List<CottageDTO>> getAllCottages() {
        List<CottageDTO> cottagesDTO = cottageService.findAll();
        return new ResponseEntity<>(cottagesDTO, HttpStatus.OK);
    }

    @GetMapping(value="/allAvailableByCity/{startDate}/{endDate}/{city}")
    public ResponseEntity<List<CottageDTO>> getAvailableCottagesByCity(@PathVariable String startDate, @PathVariable String endDate, @PathVariable String city) {
        List<CottageDTO> availableCottages = cottageService.getAvailableCottagesByCity(city, startDate, endDate);
        return new ResponseEntity<>(availableCottages, HttpStatus.OK);
    }

    @GetMapping(value="/allAvailable/{startDate}/{endDate}")
    public ResponseEntity<List<CottageDTO>> getAvailableCottages(@PathVariable String startDate, @PathVariable String endDate) {
        List<CottageDTO> availableCottages = cottageService.getAvailableCottages(startDate, endDate);
        return new ResponseEntity<>(availableCottages, HttpStatus.OK);
    }

    @GetMapping(value="/getOwnerCottages")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ResponseEntity<List<CottageDTO>> getOwnerCottages(Principal userP) {
        List<CottageDTO> cottagesDTO = cottageService.findOwnerCottages(personRepository.findByUsername(userP.getName()).getId());
        return new ResponseEntity<>(cottagesDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/reviewsNumber/{id}")
    public ResponseEntity<Integer> getNumberOfCottageReviews(@PathVariable("id") Long id) {
        Integer reviews = cottageService.getNumberOfReviews(id);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ResponseEntity<String> editCottage(@RequestPart("cottage") CottageDTO dto,@RequestPart(value = "files",required = false) MultipartFile[] multiPartFiles, @PathVariable("id") Long id) throws IOException {
        if (cottageService.edit(dto, id,Optional.ofNullable(multiPartFiles))) return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
        else return ResponseEntity.status(HttpStatus.CONFLICT).body("Cottage has pending reservations!");

    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<CottageDTO> getCottage(@PathVariable("id") Long id) throws IOException {
        CottageDTO cottage = cottageService.getCottage(id);
        if (cottage == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(cottage, HttpStatus.OK);
    }

    @GetMapping(value="/getProfilePicture/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity getCottageProfilePicture(@PathVariable Long id) throws IOException {
        Cottage cottage = cottageService.findOne(id);
        File file = new File(cottage.getProfilePicture());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok().body(resource);
    }


    @DeleteMapping(value = "/deleteCottage/{id}")
    @PreAuthorize("hasAnyRole('COTTAGE_OWNER', 'ADMIN')")
    public ResponseEntity<String> deleteCottage(@PathVariable Long id, Principal userP) {
        if (cottageService.deleteCottage(id, userP)) return ResponseEntity.ok().body("Success");
        else return ResponseEntity.status(HttpStatus.CONFLICT).body("Cottage has active reservations!");
    }

}
