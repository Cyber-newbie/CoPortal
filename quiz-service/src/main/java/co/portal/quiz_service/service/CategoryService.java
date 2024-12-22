package co.portal.quiz_service.service;

import co.portal.quiz_service.dto.CategoryDTO;

public interface CategoryService {

    abstract public void createCategory(CategoryDTO request) throws Exception;

}
