package mrsisa.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import mrsisa.project.model.Report;
import mrsisa.project.model.ReportType;

@Data
@NoArgsConstructor
public class ReportDTO {

    private Long id;
    private Long reservationId;
    private String comment;
    private boolean showedUp;
    private ReportType type;
    private String clientEmail;
    private String clientUsername;

    private String ownerUsername;

    public ReportDTO(Report report) {
        this.reservationId = report.getReservation().getId();
        this.comment = report.getComment();
        this.showedUp = report.isShowedUp();
        this.type = report.getType();
        this.clientEmail = report.getClient().getEmail();
        this.clientUsername = report.getClient().getUsername();
        this.ownerUsername = report.getOwnerUsername();
        this.id = report.getId();
    }
}
