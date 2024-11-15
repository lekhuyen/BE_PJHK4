package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.CategoryRequest;

import fpt.aptech.server_be.dto.response.CategoryRespone;
import fpt.aptech.server_be.entities.Category;


@Mapper
public class CategoryMapper {

    public static Category toCategory(CategoryRequest request){
        Category category = new Category();
        category.setCategory_id(request.getCategory_id());
        category.setCategory_name(request.getCategory_name());
        category.setDescription(request.getDescription());

        return category;
    }
    public static CategoryRespone toCategoryRespone(Category category){

        CategoryRespone categoryRespone = new CategoryRespone();
        categoryRespone.setCategory_id(category.getCategory_id());
        categoryRespone.setCategory_name(category.getCategory_name());
        categoryRespone.setDescription(category.getDescription());

        return categoryRespone;
    }
}