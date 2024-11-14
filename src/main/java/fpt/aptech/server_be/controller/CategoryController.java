package fpt.aptech.server_be.controller;


import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.CategoryRequest;
import fpt.aptech.server_be.dto.response.CategoryRespone;
import fpt.aptech.server_be.entities.Category;
import fpt.aptech.server_be.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/auction_item/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;


    public ApiResponse<List<Category>> getAllCategories() {
        ApiResponse<List<Category>> apiResponse = new ApiResponse<>();
        List<CategoryRespone> categories = categoryService.getAllCategories();
        return apiResponse;
    }

    @PostMapping
    ApiResponse<Category> createCategory(@RequestBody CategoryRequest request) {
        ApiResponse<Category> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.addCategory(request));
        return apiResponse;
    }


}
