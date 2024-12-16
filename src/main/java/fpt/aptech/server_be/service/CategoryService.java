package fpt.aptech.server_be.service;


import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.dto.response.PageResponse;
import fpt.aptech.server_be.entities.Category;


import fpt.aptech.server_be.exception.AppException;
import fpt.aptech.server_be.exception.CategoryAlreadyExistsException;
import fpt.aptech.server_be.exception.ErrorCode;
import fpt.aptech.server_be.mapper.CategoryMapper;

import fpt.aptech.server_be.repositories.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import java.util.Comparator;
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

    public PageResponse<CategoryResponse> getAllCategories(int page, int size) {

        if (page == 0 && size == 0){
            List<Category> allCategories = categoryRepository.findAll();
            return PageResponse.<CategoryResponse>builder()
                    .currentPage(1)
                    .pageSize(allCategories.size())
                    .totalElements(allCategories.size())
                    .totalPages(1)
                    .data(allCategories.stream().map(CategoryMapper::toCategoryResponse).collect(Collectors.toList()))
                    .build();
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        PageRequest pageResponse = PageRequest.of(page - 1, size,sort);
        Page<Category> categories = categoryRepository.findAll(pageResponse);

        return PageResponse.<CategoryResponse> builder()
                .currentPage(page)
                .pageSize(categories.getSize())
                .totalPages(categories.getTotalPages())
                .totalElements(categories.getTotalElements())
                .data(categories.getContent().stream().map(CategoryMapper::toCategoryResponse).collect(Collectors.toList()))
                .build();
//                categories.stream()
//                .sorted(Comparator.comparing(Category::getUpdatedAt).reversed())
//                .map(CategoryMapper::toCategoryResponse)
//                .collect(Collectors.toList());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        // Check if the category already exists
        boolean categoryExists = categoryRepository.existsByCategoryName(categoryRequest.getCategory_name());
        if (categoryExists) {
//            throw new CategoryAlreadyExistsException("Category name already exists");
            throw new AppException(ErrorCode.CATEGORY_EXISTS);
        }

        Category category = CategoryMapper.toCategory(categoryRequest);
        categoryRepository.save(category);
        return CategoryMapper.toCategoryResponse(category);
    }

    public void updateCategory(CategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategory_id()).orElseThrow(() -> new RuntimeException("Category not found"));


        category.setCategoryName(request.getCategory_name());
        category.setDescription(request.getDescription());

        Category categoryUpdated = categoryRepository.save(category);

    }

    public void deleteCategory(int category_id) {
        categoryRepository.deleteById(category_id);
    }
}
