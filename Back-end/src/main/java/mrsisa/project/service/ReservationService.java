package mrsisa.project.service;

import mrsisa.project.dto.ReservationDTO;
import mrsisa.project.model.*;
import mrsisa.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    BookableRepository bookableRepository;

    @Autowired
    PeriodService periodService;

    @Autowired
    EmailService emailService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    LoyaltyProgramService loyaltyProgramService;

    @Autowired
    UserCategoryService userCategoryService;

    @Autowired
    PaymentService paymentService;

    @Transactional
    public boolean addQuick(Long actionId, Principal userP) throws IOException {
        try {
            Action action = actionRepository.getById(actionId);
            Reservation reservation = createReservationFromAction(action);
            Bookable bookable = reservation.getBookable();
            Client client = clientRepository.findByUsername(userP.getName());
            reservation.setClient(client);
            reservationRepository.save(reservation);
            client.getReservations().add(reservation);
            clientRepository.save(client);
            bookable.getReservations().add(reservation);
            bookableRepository.save(bookable);
            action.setUsed(true);
            try {
                emailService.sendReservationMail(client, reservation);
            } catch (MailException ignored) {
            }
            return true;
        } catch (ObjectOptimisticLockingFailureException e) {
            return false;
        }
    }

    @Transactional
    public boolean add(ReservationDTO dto, Client client) throws IOException {
        Bookable bookable = bookableRepository.getById(dto.getBookableId());
        Reservation reservation = dtoToReservation(dto, bookable);
        if (checkIfClientAlreadyCanceledReservation(client, reservation.getStartDateTime(), reservation.getEndDateTime(), dto.getBookableId())) {
            return false;
        }
        Optional<Period> period = periodRepository.findPeriodByBookableIdAndStartBeforeOrEqualAndEndAfterOrEqual(reservation.getBookable().getId(), reservation.getStartDateTime(), reservation.getEndDateTime());
        if (period.isPresent()) {
            reservation.setClient(client);
            reservationRepository.save(reservation);
            client.getReservations().add(reservation);
            clientRepository.save(client);
            bookable.getReservations().add(reservation);
            bookableRepository.save(bookable);
            periodService.splitPeriodAfterReservation(period.get(), reservation, bookable);
            client.setPoints(client.getPoints() + loyaltyProgramService.getLoyaltyProgram().getClientPoints());
            this.tryChangeClientCategory(client, loyaltyProgramService.getLoyaltyProgram());
            Owner owner = (Owner) personRepository.getById(getBookableOwnerId(reservation.getBookable()));
            owner.setPoints(owner.getPoints() + loyaltyProgramService.getLoyaltyProgram().getOwnerPoints());
            this.tryChangeOwnerCategory(owner, loyaltyProgramService.getLoyaltyProgram());
            paymentService.increaseTotalMoney(reservation.getPrice() * paymentService.getMoneyPercentage());
            try {
                emailService.sendReservationMail(client, reservation);
            } catch (MailException ignored) {
            }
            return true;
        } else return false;
    }

    public void tryChangeOwnerCategory(Owner owner, LoyaltyProgram loyaltyProgram) {
        if (owner.getPoints() < loyaltyProgram.getBronzePoints()) {
            owner.setCategory(userCategoryService.getRegularCategory());
        } else if (owner.getPoints() >= loyaltyProgram.getBronzePoints() && owner.getPoints() < loyaltyProgram.getSilverPoints()) {
            owner.setCategory(userCategoryService.getBronzeCategory());
        } else if (owner.getPoints() >= loyaltyProgram.getSilverPoints() && owner.getPoints() < loyaltyProgram.getGoldPoints()) {
            owner.setCategory(userCategoryService.getSilverCategory());
        } else if (owner.getPoints() >= loyaltyProgram.getGoldPoints()) {
            owner.setCategory(userCategoryService.getGoldCategory());
        }
        personRepository.save(owner);
    }

    private Long getBookableOwnerId(Bookable bookable) {
        Long id = 0L;
        if (bookable instanceof Cottage) id = ((Cottage) bookable).getCottageOwner().getId();
        else if (bookable instanceof Boat) id = ((Boat) bookable).getBoatOwner().getId();
        else if (bookable instanceof Adventure) id = ((Adventure) bookable).getInstructor().getId();
        return id;
    }

    public void tryChangeClientCategory(Client client, LoyaltyProgram loyaltyProgram) {
        if (client.getPoints() < loyaltyProgram.getBronzePoints()) {
            client.setCategory(userCategoryService.getRegularCategory());
        } else if (client.getPoints() >= loyaltyProgram.getBronzePoints() && client.getPoints() < loyaltyProgram.getSilverPoints()) {
            client.setCategory(userCategoryService.getBronzeCategory());
        } else if (client.getPoints() >= loyaltyProgram.getSilverPoints() && client.getPoints() < loyaltyProgram.getGoldPoints()) {
            client.setCategory(userCategoryService.getSilverCategory());
        } else if (client.getPoints() >= loyaltyProgram.getGoldPoints()) {
            client.setCategory(userCategoryService.getGoldCategory());
        }
        personRepository.save(client);
    }

    private boolean checkIfClientAlreadyCanceledReservation(Client client, LocalDateTime start, LocalDateTime end, Long bookableId) {
        //ako je otkazana rezervacija tog dana
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        for (Reservation reservation : client.getReservations()) {
            if (reservation.getCanceled() && reservation.getBookable().getId().equals(bookableId) && startDate.isEqual(reservation.getStartDateTime().toLocalDate()) && endDate.isEqual(reservation.getEndDateTime().toLocalDate())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<ReservationDTO> getReservations(Principal userP) {
        Person person = personRepository.findByUsername(userP.getName());
        Role role = person.getRoles().get(0);
        switch (role.getName()) {
            case "ROLE_COTTAGE_OWNER":
                return getCottageOwnerReservations(person);
            case "ROLE_BOAT_OWNER":
                return getBoatOwnerReservations(person);
            case "ROLE_INSTRUCTOR":
                return getInstructorReservations(person);
            default:
                return getClientReservations(person);
        }
    }

    public List<ReservationDTO> getCottageOwnerReservations(Person person) {
        List<ReservationDTO> reservationsDTO = new ArrayList<>();
        CottageOwner owner = (CottageOwner) person;
        List<Cottage> cottages = owner.getCottages();
        for (Cottage cottage : cottages) {
            for (Reservation reservation : cottage.getReservations())
                reservationsDTO.add(new ReservationDTO(reservation));
        }
        return reservationsDTO;
    }

    public List<ReservationDTO> getBoatOwnerReservations(Person person) {
        List<ReservationDTO> reservationsDTO = new ArrayList<>();
        BoatOwner owner = (BoatOwner) person;
        List<Boat> boats = owner.getBoats();
        for (Boat boat : boats) {
            for (Reservation reservation : boat.getReservations()) reservationsDTO.add(new ReservationDTO(reservation));
        }
        return reservationsDTO;
    }

    public List<ReservationDTO> getInstructorReservations(Person person) {
        List<ReservationDTO> reservationsDTO = new ArrayList<>();
        Instructor instructor = (Instructor) person;
        List<Adventure> adventures = instructor.getAdventures();
        for (Adventure adventure : adventures) {
            for (Reservation reservation : adventure.getReservations())
                reservationsDTO.add(new ReservationDTO(reservation));
        }
        return reservationsDTO;
    }


    public List<ReservationDTO> getClientReservations(Person person) {
        List<ReservationDTO> reservationsDTO = new ArrayList<>();
        Client client = (Client) person;
        List<Reservation> reservations = client.getReservations();
        for (Reservation reservation : reservations) reservationsDTO.add(new ReservationDTO(reservation));
        return reservationsDTO;
    }

    @Transactional
    public List<ReservationDTO> getClientFutureReservations(Principal userP) {
        Person person = personRepository.findByUsername(userP.getName());
        List<ReservationDTO> reservationsDTO = new ArrayList<>();
        List<ReservationDTO> reservations = getClientReservations(person);
        for (ReservationDTO reservation : reservations) {
            if (reservation.getActive())
                reservationsDTO.add(reservation);
        }
        return reservationsDTO;
    }

    private Reservation createReservationFromAction(Action action) {
        Reservation reservation = new Reservation();
        reservation.setStartDateTime(action.getStartDateTime());
        reservation.setEndDateTime(action.getEndDateTime());
        reservation.setPersonLimit(action.getPersonLimit());
        reservation.setAdditionalServices(new ArrayList<Tag>(action.getAdditionalServices()));
        reservation.setActive(true);
        reservation.setPrice(action.getPrice());
        reservation.setBookable(action.getBookable());
        return reservation;
    }

    private Reservation dtoToReservation(ReservationDTO dto, Bookable bookable) {
        Reservation reservation = new Reservation();
        reservation.setStartDateTime(LocalDateTime.ofInstant(Instant.parse(dto.getStartDateTime()), ZoneOffset.UTC));
        reservation.setEndDateTime(LocalDateTime.ofInstant(Instant.parse(dto.getEndDateTime()), ZoneOffset.UTC));
        reservation.setPersonLimit(dto.getPersonLimit());
        reservation.setPrice(dto.getPrice());
        reservation.setActive(dto.getActive());
        reservation.setBookable(bookable);
        List<Tag> additionalServices = new ArrayList<>();
        for (Tag tag : bookable.getAdditionalServices()) {
            if (dto.getAdditionalServices().contains(tag.getName())) {
                additionalServices.add(tag);
            }
        }
        reservation.setAdditionalServices(additionalServices);

        return reservation;
    }

    @Transactional
    public boolean cancelReservation(Long id) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (getDaysToReservationStart(reservation.getStartDateTime()) >= 3) {
                reservation.setCanceled(true);
                reservationRepository.save(reservation);
                periodService.addPeriodOnReservationCancelling(reservation.getStartDateTime(), reservation.getEndDateTime(), reservation.getBookable());
                Client client = reservation.getClient();
                client.setPoints(client.getPoints() - loyaltyProgramService.getLoyaltyProgram().getClientPoints());
                this.tryChangeClientCategory(client, loyaltyProgramService.getLoyaltyProgram());
                Owner owner = (Owner) personRepository.getById(getBookableOwnerId(reservation.getBookable()));
                owner.setPoints(owner.getPoints() - loyaltyProgramService.getLoyaltyProgram().getOwnerPoints());
                this.tryChangeOwnerCategory(owner, loyaltyProgramService.getLoyaltyProgram());
                paymentService.decreaseTotalMoney(reservation.getPrice() * paymentService.getMoneyPercentage());
                return true;
            }
        }
        return false;

    }

    private Long getDaysToReservationStart(LocalDateTime startDateTime) {
        return DAYS.between(LocalDateTime.now(), startDateTime);
    }

}
