package mrsisa.project.repository;

import mrsisa.project.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
     Tag findByName(String name);
}
