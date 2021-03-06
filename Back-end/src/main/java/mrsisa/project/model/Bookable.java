package mrsisa.project.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Bookable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Version
    @Column(name = "optLock", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Integer version;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
    private String promotionalDescription;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> pictures;
    private String profilePicture;
    private String rules;
    private Double rating;
    private Integer capacity;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Tag> additionalServices;

    @OneToMany
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Review> reviews;

    @OneToMany
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Complaint> complaints;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Action> actions;

    @OneToOne(cascade = CascadeType.ALL)
    private PriceList priceList;

    @OneToMany(cascade = CascadeType.MERGE)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "bookable",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Period> periods;
    @ManyToMany
    private List<Client> subscribedClients;

}
