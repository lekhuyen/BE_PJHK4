package fpt.aptech.server_be.service;


import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.entities.Category;


import fpt.aptech.server_be.mapper.CategoryMapper;

import fpt.aptech.server_be.repositories.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;

    public CategoryResponse getCategoryById(Integer category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::toCategoryResponse).collect(Collectors.toList());
    }

    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        // Check if the category already exists
        boolean categoryExists = categoryRepository.existsByCategoryName(categoryRequest.getCategory_name());
        if (categoryExists) {
            //throw new AppException("This category already exists");
            //throw new AppException(CategoryError.CATEGORY_CONFLICT, CategoryError.CATEGORY_CONFLICT.getCategoryHttpStatus());
        }

        Category category = CategoryMapper.toCategory(categoryRequest);
        categoryRepository.save(category);
        CategoryResponse  categoryResponse = CategoryMapper.toCategoryResponse(category);
        return categoryResponse;
    }

    public boolean updateCategory(CategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategory_id()).orElseThrow(() -> new RuntimeException("Category not found"));


        category.setCategoryName(request.getCategory_name());
        category.setDescription(request.getDescription());

        Category categoryUpdated = categoryRepository.save(category);

        return true;
    }

    public void deleteCategory(int category_id) {
        categoryRepository.deleteById(category_id);
    }
}
