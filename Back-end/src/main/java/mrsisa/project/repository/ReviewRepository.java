package mrsisa.project.repository;

import mrsisa.project.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findReviewByReservation_Id(Long id);

    List<Review> findReviewsByBookable_Id(Long id);
}
