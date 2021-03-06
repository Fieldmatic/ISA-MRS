package mrsisa.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mrsisa.project.model.Address;
import mrsisa.project.model.Client;
import mrsisa.project.model.UserCategory;

@Setter
@Getter
@NoArgsConstructor
public class ClientDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String profilePhoto;
    private String phoneNumber;
    private Address address;
    private UserCategory category;
    private Integer points;
    private Integer penalties;

    public ClientDTO(Client client) {
        this(client.getId(), client.getUsername(), client.getPassword(), client.getName(), client.getSurname(),
                client.getEmail(), client.getProfilePhoto(), client.getPhoneNumber(), client.getAddress(), client.getCategory(), client.getPoints(), client.getPenalties());
    }

    public ClientDTO(Long id, String username, String password, String name, String surname, String email,
                     String profilePhoto, String phoneNumber, Address address, UserCategory category, Integer points, Integer penalties) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.category = category;
        this.points = points;
        this.penalties = penalties;
    }
}
