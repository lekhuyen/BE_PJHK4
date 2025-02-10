package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.AboutUsRequest;
import fpt.aptech.server_be.dto.response.AboutUsResponse;
import fpt.aptech.server_be.entities.AboutUs;
import fpt.aptech.server_be.service.AboutUsService;
import fpt.aptech.server_be.mapper.AboutUsMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/aboutus")
public class AboutUsController {
    private final AboutUsService aboutUsService;

    // Set the absolute path to store images
    private static final String UPLOAD_DIR = "images/";

    public AboutUsController(AboutUsService aboutUsService) {
        this.aboutUsService = aboutUsService;
    }

    // Create AboutUs with image upload
    @PostMapping()
    public ResponseEntity<AboutUsResponse> create(@RequestParam("file1") MultipartFile file1,
                                                  @RequestParam(value = "file2", required = false) MultipartFile file2,
                                                  @RequestParam("title") String title,
                                                  @RequestParam("description") String description) {

        AboutUsRequest aboutUsRequest = new AboutUsRequest();
        aboutUsRequest.setTitle(title);
        aboutUsRequest.setDescription(description);



        // Handle first image file (aboutImage1)
        if (!file1.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file1.getOriginalFilename()));
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

            try {
                // Ensure the upload directory exists
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Save the first image file to the target location
                file1.transferTo(targetLocation);
                aboutUsRequest.setAboutImage1(uniqueFileName);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AboutUsResponse(0, "Error saving the first image", "", "", ""));
            }
        }

        // Handle second image file (aboutImage2), if provided
        if (file2 != null && !file2.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file2.getOriginalFilename()));
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

            try {
                // Save the second image file to the target location
                file2.transferTo(targetLocation);
                aboutUsRequest.setAboutImage2(uniqueFileName);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AboutUsResponse(0, "Error saving the second image", "", "", ""));
            }
        }

        // Save the AboutUs entity using AboutUsService and convert it to the response DTO
        AboutUsResponse response = aboutUsService.save(aboutUsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get AboutUs by ID
    @GetMapping("/{id}")
    public ResponseEntity<AboutUsResponse> getById(@PathVariable("id") int id) {
        // Retrieve the AboutUs entity from the service
        AboutUs aboutUs = aboutUsService.getById(id);

        if (aboutUs == null) {
            // If not found, return a Not Found response (404)
            return ResponseEntity.notFound().build();
        }

        // Use the AboutUsMapper to convert AboutUs entity to AboutUsResponse DTO
        AboutUsResponse response = AboutUsMapper.toResponse(aboutUs);

        // Return the response with status OK (200)
        return ResponseEntity.ok(response);
    }

    // Edit AboutUs with image replacement only if a new image is provided
    @PutMapping("/{id}")
    public ResponseEntity<AboutUsResponse> edit(@PathVariable("id") int id,
                                                @RequestParam(value = "file1", required = false) MultipartFile file1,
                                                @RequestParam(value = "file2", required = false) MultipartFile file2,
                                                @RequestParam("title") String title,
                                                @RequestParam("description") String description) {

        // Create AboutUsRequest to update
        AboutUsRequest aboutUsRequest = new AboutUsRequest();
        aboutUsRequest.setTitle(title);
        aboutUsRequest.setDescription(description);

        // Find the existing AboutUs entity
        AboutUs aboutUs = aboutUsService.getById(id);
        if (aboutUs == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Handle the first image (aboutImage1)
        if (file1 != null && !file1.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file1.getOriginalFilename()));
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

            // Delete old image if it exists
            String oldImage1 = aboutUs.getAboutImage1();
            if (oldImage1 != null && !oldImage1.isEmpty()) {
                File oldImageFile = new File(UPLOAD_DIR, oldImage1);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }

            try {
                // Save the new first image to the target location
                file1.transferTo(targetLocation);
                aboutUsRequest.setAboutImage1(uniqueFileName);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AboutUsResponse(0, "Error updating the first image", "", "", ""));
            }
        }

        // Handle the second image (aboutImage2)
        if (file2 != null && !file2.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file2.getOriginalFilename()));
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

            // Delete old image if it exists
            String oldImage2 = aboutUs.getAboutImage2();
            if (oldImage2 != null && !oldImage2.isEmpty()) {
                File oldImageFile = new File(UPLOAD_DIR, oldImage2);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }

            try {
                // Save the new second image to the target location
                file2.transferTo(targetLocation);
                aboutUsRequest.setAboutImage2(uniqueFileName);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AboutUsResponse(0, "Error updating the second image", "", "", ""));
            }
        }

        // Save the updated AboutUs entity and convert it to the response DTO
        AboutUsResponse response = aboutUsService.update(id, aboutUsRequest);
        return ResponseEntity.ok(response);
    }

    // Get all AboutUs entries
    @GetMapping()
    public ResponseEntity<List<AboutUsResponse>> getAll() {
        // Call the service to get the list of AboutUsResponse
        List<AboutUsResponse> response = aboutUsService.getAll();

        // Return the list with status OK (200)
        return ResponseEntity.ok(response);
    }

    @GetMapping("/AboutUsImages/{imageName}")
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
