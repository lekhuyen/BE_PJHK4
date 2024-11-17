package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.CategoryRequest;

import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.entities.Category;
import org.mapstruct.Mapper;


@Mapper
public class CategoryMapper {

    public static Category toCategory(CategoryRequest request){
        Category category = new Category();
        category.setCategory_id(request.getCategory_id());
        category.setCategoryName(request.getCategory_name());
        category.setDescription(request.getDescription());

        return category;
    }
    public static CategoryResponse toCategoryResponse(Category category){

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategory_id(category.getCategory_id());
        categoryResponse.setCategory_name(category.getCategoryName());
        categoryResponse.setDescription(category.getDescription());

        return categoryResponse;
    }
}
