package paybank.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import paybank.astro.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
