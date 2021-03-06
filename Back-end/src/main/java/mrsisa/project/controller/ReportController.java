package mrsisa.project.controller;

import mrsisa.project.dto.ReportDTO;
import mrsisa.project.model.Administrator;
import mrsisa.project.model.Report;
import mrsisa.project.service.AdminService;
import mrsisa.project.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/addReport")
    @PreAuthorize("hasAnyRole('ROLE_COTTAGE_OWNER','ROLE_BOAT_OWNER','ROLE_INSTRUCTOR')")
    public ResponseEntity<String> addReport(@RequestBody ReportDTO reportDTO) {
        if (reportService.add(reportDTO)) return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        else return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Error");
    }

    @GetMapping(value = "/getReport/{reservationId}")
    @PreAuthorize("hasAnyRole('ROLE_COTTAGE_OWNER','ROLE_BOAT_OWNER','ROLE_INSTRUCTOR')")
    public ReportDTO getReport(@PathVariable("reservationId") Long id) {
        Report report = reportService.findByReservationId(id);
        if (report != null) return new ReportDTO(report);
        else return null;

    }

    @GetMapping(value = "/getAllReports")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReportDTO>> getAllReports(Principal userP) {
        Administrator admin = adminService.findAdminByUsername(userP.getName());
        if (admin.getLastPasswordResetDate() == null)
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        List<ReportDTO> list = reportService.getAllReports();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/reviewReport/{id}&{penalty}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> reviewReport(@PathVariable Long id,
                                               @PathVariable Boolean penalty,
                                               @RequestBody String message,
                                               Principal userP)
    {
        Administrator admin = adminService.findAdminByUsername(userP.getName());
        if (admin.getLastPasswordResetDate() == null)
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        Report report = reportService.findByReservationId(id);
        if (report == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error! Report not found.");
        reportService.reviewReport(report, message, penalty);
        return ResponseEntity.status(HttpStatus.OK).body("Email sent!");
    }

}
