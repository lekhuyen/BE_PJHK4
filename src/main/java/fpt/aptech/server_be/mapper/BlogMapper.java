package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.AboutUsRequest;
import fpt.aptech.server_be.dto.request.BlogRequest;
import fpt.aptech.server_be.dto.response.BlogResponse;
import fpt.aptech.server_be.entities.Blog;
import org.mapstruct.Mapper;

@Mapper
public class BlogMapper {

    // Method to map Blog entity to BlogResponse DTO
    public static BlogResponse blogToBlogResponse(Blog blog) {
        if (blog == null) {
            return null; // Return null if Blog entity is null
        }

        // Return the BlogResponse object by mapping fields from Blog entity
        return new BlogResponse(
                blog.getId(),
                blog.getTitle(),
                blog.getAuthor(),
                blog.getContent(),
                blog.getBlogDate(),
                blog.getBlogImage() != null ? blog.getBlogImage() : "" // Handle null case for image
        );
    }


    // Method to map BlogResponse DTO to Blog entity
    public Blog blogResponseToBlog(BlogResponse blogResponse) {
        if (blogResponse == null) {
            return null; // Return null if BlogResponse is null
        }

        Blog blog = new Blog();
        blog.setId(blogResponse.getId());
        blog.setTitle(blogResponse.getTitle());
        blog.setAuthor(blogResponse.getAuthor());
        blog.setContent(blogResponse.getContent());
        blog.setBlogDate(blogResponse.getBlogDate());
        blog.setBlogImage(blogResponse.getBlogImage());

        return blog;
    }
}
