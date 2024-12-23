package co.portal.quiz_service.repository;

import co.portal.quiz_service.entity.Category;
import co.portal.quiz_service.service.CategoryService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

     Category findByTitle(String title);

}
