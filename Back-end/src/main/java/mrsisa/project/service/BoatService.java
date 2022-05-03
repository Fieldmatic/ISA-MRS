package mrsisa.project.service;


import mrsisa.project.dto.BoatDTO;
import mrsisa.project.dto.CottageDTO;
import mrsisa.project.model.*;
import mrsisa.project.repository.AddressRepository;
import mrsisa.project.repository.BoatRepository;
import mrsisa.project.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BoatService {
    @Autowired
    BoatRepository boatRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PriceListRepository priceListRepository;

    final String PICTURES_PATH = "src/main/resources/static/pictures/boat/";

    public void add(BoatDTO dto, MultipartFile[] multipartFiles) throws IOException {
        Boat boat = dtoToBoat(dto);
        boatRepository.save(boat);
        List<String> paths = addPictures(boat, multipartFiles);
        boat.setPictures(paths);
        boat.setProfilePicture(paths.get(0));
        boatRepository.save(boat);
    }

    public List<String> addPictures(Boat boat, MultipartFile[] multipartFiles) throws IOException {
        List<String> paths = new ArrayList<>();

        if(multipartFiles == null) {
            return paths;
        }
        Path path = Paths.get(PICTURES_PATH + boat.getId());
        savePicturesOnPath(boat, multipartFiles, paths, path);
        return paths.stream().distinct().collect(Collectors.toList());
    }

    private void savePicturesOnPath(Boat boat, MultipartFile[] multipartFiles, List<String> paths, Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        for (MultipartFile mpf : multipartFiles) {
            String fileName = mpf.getOriginalFilename();
            try (InputStream inputStream = mpf.getInputStream()) {
                Path filePath = path.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                paths.add(PICTURES_PATH + boat.getId() + "/" + fileName);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }
    }



    public void edit(BoatDTO dto, Long id) {
        Boat boat = boatRepository.findById(id).orElse(null);
        boat.setName(dto.getName());
        boat.setAddress(dto.getAddress());
        boat.setPromotionalDescription(dto.getPromotionalDescription());
        boat.setRules(dto.getRules());
        boat.getPriceList().setHourlyRate(dto.getHourlyRate());
        boat.getPriceList().setDailyRate(dto.getDailyRate());
        boat.getPriceList().setCancellationConditions(dto.getCancellationConditions());
        boatRepository.save(boat);
    }

    public Boat findOne(Long id) {
        return boatRepository.findById(id).orElse(null);
    }

    public List<Boat> findAll() {
        return boatRepository.findAll();
    }

    private Boat dtoToBoat(BoatDTO dto) {
        Boat boat = new Boat();
        boat.setName(dto.getName());
        Address address = dto.getAddress();
        addressRepository.save(address);
        boat.setAddress(address);
        boat.setPromotionalDescription(dto.getPromotionalDescription());
        boat.setRules(dto.getRules());
        PriceList priceList = new PriceList();
        priceList.setHourlyRate(dto.getHourlyRate());
        priceList.setDailyRate(dto.getDailyRate());
        priceList.setCancellationConditions(dto.getCancellationConditions());
        priceListRepository.save(priceList);
        boat.setPriceList(priceList);
        boat.setRating(0.0);
        boat.setType(BoatType.valueOf(dto.getType()));
        boat.setEnginesNumber(dto.getEnginesNumber());
        boat.setEnginePower(dto.getEnginePower());
        boat.setMaxSpeed(dto.getMaxSpeed());
        boat.setCapacity(dto.getCapacity());
        boat.setNavigationEquipment(dto.getNavigationEquipment());
        boat.setFishingEquipment(dto.getFishingEquipment());
        return boat;
    }
}
