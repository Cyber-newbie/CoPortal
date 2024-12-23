package co.portal.quiz_service.service;

import co.portal.quiz_service.dto.CategoryDTO;
import co.portal.quiz_service.entity.Category;
import co.portal.quiz_service.exception.CategoryAlreadyExists;
import co.portal.quiz_service.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CategoryServiceImpl implements  CategoryService{


    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repo){
        this.categoryRepository = repo;
    }

    @Override
    public void createCategory(CategoryDTO request) throws Exception {

         Optional.of(categoryRepository.findByTitle(request.getTitle()))
                 .ifPresent((existingCategory) -> {
                     throw new CategoryAlreadyExists("Category already exists " + request.getTitle());
                 });

        Category category = Category.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        try {
             categoryRepository.save(category);
        } catch (Exception e){
            log.error("Error occurred while creating category, reason: {}", e.getMessage());
        }

    }
}
