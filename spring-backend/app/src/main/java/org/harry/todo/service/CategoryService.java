package org.harry.todo.service;


import org.harry.todo.dto.response.CategoryResponseDTO;
import org.harry.todo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getAllCategories(){

        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponseDTO.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .build()
                ).toList();
    }
}
