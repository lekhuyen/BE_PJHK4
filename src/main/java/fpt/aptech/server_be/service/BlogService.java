package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.response.BlogResponse;
import fpt.aptech.server_be.entities.Blog;
import fpt.aptech.server_be.mapper.BlogMapper;
import fpt.aptech.server_be.repositories.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;

    }

    // Method to create a new blog post
    public BlogResponse createBlog(Blog blog) {
        // Save the Blog entity to the database using BlogRepository
        Blog savedBlog = blogRepository.save(blog);
        // Convert the saved Blog entity to BlogResponse DTO
        return BlogMapper.blogToBlogResponse(savedBlog);
    }

    // Method to get all blogs
    public List<BlogResponse> getAllBlogs() {
        // Fetch all blogs from the database
        List<Blog> blogs = blogRepository.findAll();
        // Convert List<Blog> to List<BlogResponse>
        return blogs.stream()
                .map(BlogMapper::blogToBlogResponse)
                .collect(Collectors.toList());
    }

    // Method to get a blog by ID
    public Blog getById(int id) {
        return blogRepository.findById(id).orElse(null); // Return Blog or null if not found
    }

    // Update blog
    public BlogResponse updateBlog(Blog blog) {
        Blog updatedBlog = blogRepository.save(blog); // Save the updated blog
        return new BlogResponse(updatedBlog.getId(), updatedBlog.getTitle(), updatedBlog.getAuthor(),
                updatedBlog.getContent(), updatedBlog.getBlogDate(), updatedBlog.getBlogImage());
    }

    // Method to delete a blog by ID
    public void deleteBlog(int id) {
        // Check if the blog exists
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
        } else {
            // Handle the case when the blog is not found
            // Maybe throw an exception if necessary
        }
    }
}

