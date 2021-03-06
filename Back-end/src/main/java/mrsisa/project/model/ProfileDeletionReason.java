package mrsisa.project.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProfileDeletionReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "optLock", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Integer version;
    private String reason;
    @OneToOne
    private Person user;
    private Boolean approved;
    private Boolean viewed;

    public ProfileDeletionReason(String reason, Boolean approved, Person user) {
        this.reason = reason;
        this.approved = approved;
        this.user = user;
        this.viewed = false;
    }
}
