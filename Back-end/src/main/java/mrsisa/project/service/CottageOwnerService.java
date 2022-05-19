package mrsisa.project.service;

import mrsisa.project.dto.OwnerDTO;
import mrsisa.project.model.*;
import mrsisa.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CottageOwnerService {

    @Autowired
    CottageOwnerRepository cottageOwnerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    final String PICTURES_PATH = "src/main/resources/static/pictures/cottageOwner/";


    public Person add(OwnerDTO dto, MultipartFile[] multipartFiles) throws IOException {
        CottageOwner owner = dtoToCottageOwner(dto);
        List<String> paths = addPictures(owner, multipartFiles);
        owner.setProfilePhoto(paths.get(0));
        cottageOwnerRepository.save(owner);
        return owner;
    }

    public List<String> addPictures(CottageOwner owner, MultipartFile[] multipartFiles) throws IOException {
        List<String> paths = new ArrayList<>();
        if(multipartFiles == null) return paths;
        Path path = Paths.get(PICTURES_PATH + owner.getId());
        savePicturesOnPath(owner, multipartFiles, paths, path);
        return paths.stream().distinct().collect(Collectors.toList());
    }

    private void savePicturesOnPath(CottageOwner owner, MultipartFile[] multipartFiles, List<String> paths, Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        for (MultipartFile mpf : multipartFiles) {
            String fileName = mpf.getOriginalFilename();
            try (InputStream inputStream = mpf.getInputStream()) {
                Path filePath = path.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                paths.add(PICTURES_PATH + owner.getId() + "/" + fileName);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }
    }

    private CottageOwner dtoToCottageOwner(OwnerDTO dto) {
        CottageOwner owner = new CottageOwner();
        owner.setName(dto.getName());
        owner.setSurname(dto.getSurname());
        owner.setAddress(dto.getAddress());
        owner.setUsername(dto.getUsername());
        owner.setPassword(passwordEncoder.encode(dto.getPassword()));
        owner.setActive(true);
        owner.setEmail(dto.getEmail());
        owner.setPhoneNumber(dto.getPhoneNumber());
        owner.setRegistrationExplanation(dto.getRegistrationExplanation());
        owner.setApprovedAccount(false);
        owner.setPoints(0);
        List<Role> roles = roleService.findByName(dto.getRole());
        owner.setRoles(roles);
        return owner;
    }
}
