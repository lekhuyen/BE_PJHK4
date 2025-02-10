package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.response.BlogResponse;
import fpt.aptech.server_be.entities.Blog;
import fpt.aptech.server_be.mapper.BlogMapper;
import fpt.aptech.server_be.repositories.BlogRepository;
import fpt.aptech.server_be.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogService blogService;

    // Set the absolute path to store images
    private static final String UPLOAD_DIR = "images/";
    private final BlogRepository blogRepository;

    @Autowired
    public BlogController(BlogService blogService, BlogRepository blogRepository) {
        this.blogService = blogService;
        this.blogRepository = blogRepository;
    }

    // Endpoint to get all blog posts
    @GetMapping
    public List<BlogResponse> getAllBlogs() {
        return blogService.getAllBlogs();
    }


    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getById(@PathVariable("id") int id) {
        // Retrieve the Blog entity from the service
        Blog blog = blogService.getById(id);

        if (blog == null) {
            // If not found, return a Not Found response (404)
            return ResponseEntity.notFound().build();
        }

        // Use the BlogMapper to convert Blog to BlogResponse
        BlogResponse response = BlogMapper.blogToBlogResponse(blog);

        // Return the response with status OK (200)
        return ResponseEntity.ok(response);
    }

    // Endpoint to create a new blog post with image upload
    @PostMapping()
    public ResponseEntity<BlogResponse> createBlog(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("content") String content,
            @RequestParam("blogImages") List<MultipartFile> files) { // Accept multiple files

        // List to store the file names
        List<String> imageFileNames = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                    Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

                    try {
                        // Ensure the upload directory exists
                        File uploadDir = new File(UPLOAD_DIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs(); // Create directory if it doesn't exist
                        }

                        // Save the image file to the target location
                        file.transferTo(targetLocation);

                        // Add the unique image file name to the list
                        imageFileNames.add(uniqueFileName);
                    } catch (IOException ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new BlogResponse(0, "Error saving one or more images", "", "", null, ""));
                    }
                }
            }
        }

        // Create a new Blog object and set the image file names (as a comma-separated string or a list)
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setAuthor(author);
        blog.setContent(content);
        blog.setBlogImage(String.join(",", imageFileNames)); // Store the image names as a comma-separated string or as needed
        blog.setBlogDate(new Date()); // Set the current date

        // Save the Blog entity
        BlogResponse blogResponse = blogService.createBlog(blog); // Assuming this method handles the creation

        return ResponseEntity.status(HttpStatus.CREATED).body(blogResponse); // Return the created blog response
    }


    // Endpoint to update an existing blog post with optional image upload
    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> updateBlog(
            @PathVariable("id") int id,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("content") String content,
            @RequestParam(value = "blogDate", required = false) String blogDateString, // Accept blogDate as String
            @RequestParam(value = "blogImages", required = false) MultipartFile[] files) {

        // Find the existing Blog entity
        Blog existingBlog = blogService.getById(id);
        if (existingBlog == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Update Blog fields with new data
        existingBlog.setTitle(title);
        existingBlog.setAuthor(author);
        existingBlog.setContent(content);

        // Handle blogDate if provided
        if (blogDateString != null && !blogDateString.isEmpty()) {
            try {
                // Convert the blogDate string to Date (assuming a format like yyyy-MM-dd)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date blogDate = sdf.parse(blogDateString);
                existingBlog.setBlogDate(blogDate); // Set the new blogDate
            } catch (ParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BlogResponse(0, "Invalid date format", "", "", null, ""));
            }
        }

        // Handle image update if new files are provided
        if (files != null && files.length > 0) {
            // Prepare to store all image file names
            StringBuilder newImageNames = new StringBuilder();

            // Delete old images if they exist
            String oldImages = existingBlog.getBlogImage();
            if (oldImages != null && !oldImages.isEmpty()) {
                // Split the old images list and delete them one by one
                String[] oldImageArray = oldImages.split(",");
                for (String oldImage : oldImageArray) {
                    File oldImageFile = new File(UPLOAD_DIR, oldImage);
                    if (oldImageFile.exists()) {
                        oldImageFile.delete(); // Delete the old image file
                    }
                }
            }

            // Process new uploaded image files
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                    Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

                    try {
                        // Ensure the upload directory exists
                        File uploadDir = new File(UPLOAD_DIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }

                        // Save the new image to the target location
                        file.transferTo(targetLocation);

                        // Append the new image name to the list
                        if (newImageNames.length() > 0) {
                            newImageNames.append(",");
                        }
                        newImageNames.append(uniqueFileName);

                    } catch (IOException ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new BlogResponse(0, "Error updating images", "", "", null, ""));
                    }
                }
            }

            // Update the blog with the new images
            existingBlog.setBlogImage(newImageNames.toString());
        }

        // Save the updated Blog entity
        BlogResponse blogResponse = blogService.updateBlog(existingBlog);

        // Return the updated blog response
        return ResponseEntity.status(HttpStatus.OK).body(blogResponse);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        // Retrieve the blog by ID
        Blog blog = blogRepository.findById(id).orElse(null);

        if (blog == null) {
            // If the blog is not found, return a Not Found response (404)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Handle deleting the blog image
        String blogImage = blog.getBlogImage();
        if (blogImage != null && !blogImage.isEmpty()) {
            File imageFile = new File(UPLOAD_DIR, blogImage);
            if (imageFile.exists()) {
                // Delete the image file from the file system
                imageFile.delete();
            }
        }

        // Delete the Blog entity from the database
        blogRepository.delete(blog);

        // Return a successful response with status NO_CONTENT (204)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/BlogImages/{imageName}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get(UPLOAD_DIR).resolve(imageName).normalize();
            Resource resource = new FileSystemResource(imagePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type for the image (optional, but recommended)
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType)) // Dynamically set the correct media type
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
