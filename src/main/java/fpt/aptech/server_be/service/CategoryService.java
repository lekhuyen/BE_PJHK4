package fpt.aptech.server_be.service;


import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.CategoryRespone;
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

    public CategoryRespone getCategoryById(Integer category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryMapper.toCategoryRespone(category);
    }

    public List<CategoryRespone> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::toCategoryRespone).collect(Collectors.toList());
    }

    public Category addCategory(CategoryRequest categoryRequest) {
        // Check if the category already exists
        boolean categoryExists = categoryRepository.existsByCategoryName(categoryRequest.getCategory_name());
        if (categoryExists) {
            //throw new AppException("This category already exists");
            //throw new AppException(CategoryError.CATEGORY_CONFLICT, CategoryError.CATEGORY_CONFLICT.getCategoryHttpStatus());
        }

        Category category = CategoryMapper.toCategory(categoryRequest);
        return categoryRepository.save(category);
    }

    public boolean updateCategory(Integer category_id, CategoryRequest request) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("Category not found"));


        category.setCategory_name(request.getCategory_name());
        category.setDescription(request.getDescription());

        Category categoryUpdated = categoryRepository.save(category);

        return categoryUpdated != null;
    }

    public void deleteCategory(Integer category_id) {
        categoryRepository.deleteById(category_id);
    }
}
