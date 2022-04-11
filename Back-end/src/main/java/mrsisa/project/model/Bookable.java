package mrsisa.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Bookable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String address;
    private String promotionalDescription;
    @ElementCollection
    private List<String> pictures;
    private String profilePicture;
    private String rules;
    private Double raiting;
    @OneToMany
    private List<Tag> additionalServices;
    @OneToMany
    private List<Review> reviews;
    @OneToMany
    private List<Discount> discounts;
    @OneToOne
    private PriceList priceList;
    @OneToMany
    private List<Reservation> reservations;
    @OneToMany
    private List<Period> periods;

}