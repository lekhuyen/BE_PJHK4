package fpt.aptech.server_be.controller;


import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.entities.Category;
import fpt.aptech.server_be.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

//    @MessageMapping("/category")
//    @SendTo("/topic/category")
    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        List<CategoryResponse> category = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(category)
                .build();
    }
    @MessageMapping("/category")
    @SendTo("/topic/category")
//    @PostMapping("/")
    ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return  ApiResponse.<CategoryResponse>builder()
                .result(categoryService.addCategory(request))
                .build();
    }

    @GetMapping("/")
    public ApiResponse<List<CategoryResponse>> getCategory() {
        List<CategoryResponse> category = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(category)
                .build();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable("id") int id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ApiResponse.<CategoryResponse>builder()
                .result(category)
                .build()
                .getResult();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<String>builder()
                .message("Deleted category")
                .build();
    }

    @MessageMapping("/category/update")
    @SendTo("/topic/category")
//    @PutMapping("/")
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequest request) {
        categoryService.updateCategory(request);
        return ApiResponse.<String>builder()
                .message("Update category")
                .build();
    }

}
