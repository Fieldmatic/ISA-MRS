package mrsisa.project.service;

import mrsisa.project.dto.AdventureDTO;
import mrsisa.project.model.Address;
import mrsisa.project.model.Adventure;
import mrsisa.project.model.PriceList;
import mrsisa.project.repository.AddressRepository;
import mrsisa.project.repository.AdventureRepository;
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
public class AdventureService {

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private AddressRepository addressRepository;

    final String PICTURES_PATH = "src/main/resources/static/pictures/adventure/";

    public Adventure save(Adventure adventure) {
        return adventureRepository.save(adventure);
    }

    public void add(AdventureDTO adventureDTO, MultipartFile[] multipartFiles) throws IOException {
        Adventure adventure = this.dtoToAdventure(adventureDTO);
        List<String> paths = addPictures(adventure, multipartFiles);
        adventure.setPictures(paths);
        adventure.setProfilePicture(paths.get(0));
        adventureRepository.save(adventure);
    }

    public void edit(AdventureDTO dto, Long id) {
        Adventure adventure = adventureRepository.findById(id).orElse(null);
        if (adventure != null) {
            adventure.setName(dto.getName());
            adventure.setAddress(dto.getAddress());
            adventure.setPromotionalDescription(dto.getPromoDescription());
            adventure.setRules(dto.getRules());
            adventure.getPriceList().setHourlyRate(dto.getHourlyRate());
            adventure.getPriceList().setCancellationConditions(dto.getCancellationConditions());
            adventure.setCapacity(dto.getCapacity());
            adventure.setFishingEquipment(dto.getEquipment());
            adventureRepository.save(adventure);
        }
    }

    public Adventure findOne(Long id) {
        return adventureRepository.findById(id).orElseGet(null);
    }

    public List<Adventure> findAll() {
        return adventureRepository.findAll();
    }

    public void remove(Long id) {
        adventureRepository.deleteById(id);
    }

    private Adventure dtoToAdventure(AdventureDTO dto) {
        Adventure adventure = new Adventure();
        adventure.setName(dto.getName());
        Address address = dto.getAddress();
        addressRepository.save(address);
        adventure.setAddress(address);
        adventure.setPromotionalDescription(dto.getPromoDescription());
        adventure.setRules(dto.getRules());
        PriceList priceList = new PriceList();
        priceList.setHourlyRate(dto.getHourlyRate());
        priceList.setCancellationConditions(dto.getCancellationConditions());
        priceListRepository.save(priceList);
        adventure.setPriceList(priceList);
        adventure.setRating(0.0);
        adventure.setCapacity(dto.getCapacity());
        adventure.setFishingEquipment(dto.getEquipment());
        return adventure;
    }

    public List<String> addPictures(Adventure adventure, MultipartFile[] multipartFiles) throws IOException {
        List<String> paths = new ArrayList<>();

        if(multipartFiles == null) {
            return paths;
        }
        Path path = Paths.get(PICTURES_PATH + adventure.getId());
        savePicturesOnPath(adventure, multipartFiles, paths, path);
        return paths.stream().distinct().collect(Collectors.toList());
    }

    private void savePicturesOnPath(Adventure adventure, MultipartFile[] multipartFiles, List<String> paths, Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        for (MultipartFile mpf : multipartFiles) {
            String fileName = mpf.getOriginalFilename();
            try (InputStream inputStream = mpf.getInputStream()) {
                Path filePath = path.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                paths.add(PICTURES_PATH + adventure.getId() + "/" + fileName);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }
    }
}