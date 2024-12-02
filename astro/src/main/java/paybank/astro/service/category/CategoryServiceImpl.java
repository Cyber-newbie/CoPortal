package paybank.astro.service.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paybank.astro.dto.category.CategoryRequest;
import paybank.astro.entity.Category;
import paybank.astro.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategory(int id) {
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setTitle(request.getTitle());
        category.setDescription(request.getDescription());
        this.categoryRepository.save(category);
        return category;
    }

    @Override
    public Category updateCategory(Category category) {
        return null;
    }

    @Override
    public Category deleteCategory(int id) {
        return null;
    }
}
