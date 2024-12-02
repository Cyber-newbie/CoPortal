package paybank.astro.service.category;

import paybank.astro.dto.category.CategoryRequest;
import paybank.astro.entity.Category;

import java.util.List;

public interface CategoryService {
    public Category getCategory(int id);

    public List<Category> getAllCategories();

    public Category createCategory(CategoryRequest categoryRequest);

    public Category updateCategory(Category category);

    public Category deleteCategory(int id);
}
