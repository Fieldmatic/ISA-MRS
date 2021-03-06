package mrsisa.project.service;

import mrsisa.project.dto.PeriodDTO;
import mrsisa.project.model.Bookable;
import mrsisa.project.model.Cottage;
import mrsisa.project.model.Period;
import mrsisa.project.model.Reservation;
import mrsisa.project.repository.BookableRepository;
import mrsisa.project.repository.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PeriodService {
    @Autowired
    private BookableRepository bookableRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Transactional
    public void createPeriodForCottage(Cottage cottage) {

        Period period = new Period();
        period.setStartDateTime(LocalDateTime.now());
        period.setEndDateTime(LocalDateTime.now().plusDays(20));
        period.setBookable(cottage);

        Bookable bookable = bookableRepository.getById(cottage.getId());
        bookable.getPeriods().add(period);
        periodRepository.save(period);
    }

    @Transactional
    public String add(PeriodDTO periodDTO) {
        Period period = dtoToPeriod(periodDTO);
        Bookable bookable = bookableRepository.getById(periodDTO.getBookableId());
        if (reservationExistsInPeriod(bookable.getReservations(), period)) return "Reservation exists in given period!";
        String answer = checkPeriodMatching(period.getStartDateTime(), period.getEndDateTime(), bookable);
        if (answer.equals("available")) {
            period.setBookable(bookable);
            bookable.getPeriods().add(period);
            bookableRepository.save(bookable);
            return "success";
        }
        return answer;
    }

    private boolean reservationExistsInPeriod(List<Reservation> reservations, Period period){
        for (Reservation reservation: reservations){
            if (reservation.getStartDateTime().isAfter(period.getStartDateTime()) && reservation.getEndDateTime().isBefore(period.getEndDateTime())) return true;
            if (reservation.getStartDateTime().equals(period.getStartDateTime()) || reservation.getEndDateTime().equals(period.getEndDateTime())) return true;
            if (reservation.getStartDateTime().isBefore(period.getStartDateTime()) && reservation.getEndDateTime().isAfter(period.getEndDateTime())) return true;
        }
        return false;
    }

    public void splitPeriodAfterReservation(Period period, Reservation reservation, Bookable bookable)  {
        if (reservation.getStartDateTime().isAfter(period.getStartDateTime()) && reservation.getEndDateTime().isBefore(period.getEndDateTime())){
            Period newPeriod = new Period();
            newPeriod.setStartDateTime(reservation.getEndDateTime());
            newPeriod.setEndDateTime(period.getEndDateTime());
            newPeriod.setBookable(bookable);
            period.setEndDateTime(reservation.getStartDateTime());
            bookable.getPeriods().add(newPeriod);
        }
        else if (reservation.getStartDateTime().isEqual(period.getStartDateTime()) && reservation.getEndDateTime().isEqual(period.getEndDateTime())){
            periodRepository.delete(period);
        }
        else if (reservation.getStartDateTime().isEqual(period.getStartDateTime()) && reservation.getEndDateTime().isBefore(period.getEndDateTime())){
            period.setStartDateTime(reservation.getEndDateTime());
        }
        else if (reservation.getStartDateTime().isAfter(period.getStartDateTime()) && reservation.getEndDateTime().isEqual(period.getEndDateTime())){
            period.setEndDateTime(reservation.getStartDateTime());
        }
        bookableRepository.save(bookable);
    }

    private String checkPeriodMatching(LocalDateTime start, LocalDateTime end, Bookable bookable) {
        for (Period period : bookable.getPeriods()) {
            if (start.isEqual(period.getStartDateTime()) && end.isEqual(period.getEndDateTime()))
                return "occupied";
            if (start.isAfter(period.getStartDateTime()) && end.isBefore(period.getEndDateTime()))
                return "occupied";
            if (start.isEqual(period.getStartDateTime()) && end.isBefore(period.getEndDateTime())) {
                return "occupied";
            }
            if (end.isEqual(period.getEndDateTime()) && start.isAfter(period.getStartDateTime())) {
                return "occupied";
            }
            if (start.isBefore(period.getStartDateTime()) && end.isAfter(period.getEndDateTime())) {
                extendPeriod(period,start,end);
                return "extended";
            }
            if (start.isBefore(period.getStartDateTime()) && end.isBefore(period.getEndDateTime()) && end.isAfter(period.getStartDateTime())) {
                extendPeriod(period,start,period.getEndDateTime());
                return "extended";
            }
            if (start.isAfter(period.getStartDateTime()) && start.isBefore(period.getEndDateTime()) && end.isAfter(period.getEndDateTime())) {
                extendPeriod(period,period.getStartDateTime(),end);
                return "extended";
            }
            if (start.isEqual(period.getEndDateTime()) && end.isAfter(period.getEndDateTime())) {
                extendPeriod(period,period.getStartDateTime(),end);
                return "extended";
            }
            if (start.isEqual(period.getStartDateTime()) && end.isAfter(period.getEndDateTime())) {
                extendPeriod(period,period.getStartDateTime(),end);
                return "extended";
            }
            if (end.isEqual(period.getEndDateTime()) && start.isBefore(period.getStartDateTime())) {
                extendPeriod(period,start,period.getEndDateTime());
                return "extended";
            }
            if (end.isEqual(period.getStartDateTime()) && start.isBefore(period.getStartDateTime())) {
                extendPeriod(period, start, period.getEndDateTime());
                return "extended";
            }
        }
        return "available";
    }

    private void extendPeriod(Period period,LocalDateTime start, LocalDateTime end){
        period.setStartDateTime(start);
        period.setEndDateTime(end);
        periodRepository.save(period);
    }


    private Period dtoToPeriod(PeriodDTO periodDTO){
        Period period = new Period();
        period.setStartDateTime(LocalDateTime.ofInstant(Instant.parse(periodDTO.getStartDateTime()), ZoneOffset.UTC));
        period.setEndDateTime(LocalDateTime.ofInstant(Instant.parse(periodDTO.getEndDateTime()), ZoneOffset.UTC));
        return period;
    }

    public void addPeriodOnReservationCancelling(LocalDateTime start, LocalDateTime end, Bookable bookable) {
        Period period = new Period();
        period.setStartDateTime(start);
        period.setEndDateTime(end);
        String answer = checkPeriodMatching(period.getStartDateTime(), period.getEndDateTime(), bookable);
        if (answer.equals("available")) {
            period.setBookable(bookable);
            bookable.getPeriods().add(period);
            bookableRepository.save(bookable);
        }
    }
}
