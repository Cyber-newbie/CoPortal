package paybank.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import paybank.astro.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
