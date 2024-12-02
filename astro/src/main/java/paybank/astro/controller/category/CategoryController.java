package paybank.astro.controller.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paybank.astro.dto.category.CategoryRequest;
import paybank.astro.dto.category.CategoryResponse;
import paybank.astro.entity.Category;
import paybank.astro.service.category.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse<Category>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = this.categoryService.createCategory(request);

        CategoryResponse<Category> response = new CategoryResponse<>();
        response.setData(category);
        response.setStatus("201");
        response.setMessage("Category created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CategoryResponse<List<Category>>> getAllCategories() {
        List<Category> categories = this.categoryService.getAllCategories();
        CategoryResponse<List<Category>> response = new CategoryResponse<>();
        response.setData(categories);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
