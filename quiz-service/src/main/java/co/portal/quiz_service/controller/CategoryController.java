package co.portal.quiz_service.controller;

import co.portal.quiz_service.dto.CategoryDTO;
import co.portal.quiz_service.service.CategoryService;
import co.portal.quiz_service.service.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private CategoryServiceImpl categoryService;

    @Autowired
    public void categoryController(CategoryServiceImpl categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/create")
    public ResponseEntity<HttpStatus> createCategory(@Valid  @RequestBody CategoryDTO request) throws  Exception {
        categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    };


}
