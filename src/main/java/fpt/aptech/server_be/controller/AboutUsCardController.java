package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.AboutUsCardRequest;
import fpt.aptech.server_be.dto.response.AboutUsCardResponse;
import fpt.aptech.server_be.entities.AboutUsCard;
import fpt.aptech.server_be.mapper.AboutUsCardMapper;
import fpt.aptech.server_be.repositories.AboutUsCardRepository;
import fpt.aptech.server_be.service.AboutUsCardService;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aboutuscard")
public class AboutUsCardController {

    private final AboutUsCardService aboutUsCardService;

    private static final String UPLOAD_DIR = "images/";  // Directory for uploaded images
    private final AboutUsCardRepository aboutUsCardRepository;

    public AboutUsCardController(AboutUsCardService aboutUsCardService, AboutUsCardRepository aboutUsCardRepository) {
        this.aboutUsCardService = aboutUsCardService;
        this.aboutUsCardRepository = aboutUsCardRepository;
    }

    @GetMapping()
    // Get all AboutUsCard records
    public List<AboutUsCardResponse> getAll() {
        // Fetch all AboutUsCard entities from the database
        List<AboutUsCard> aboutUsCards = aboutUsCardRepository.findAll();

        // Convert the entities to response DTOs
        return aboutUsCards.stream()
                .map(AboutUsCardMapper::toResponse)  // Use the mapper to convert to DTO
                .collect(Collectors.toList());  // Collect into a list and return
    }

    @GetMapping("/{title}")
// Get an AboutUsCard by title (ID)
    public AboutUsCardResponse getById(@PathVariable("title") String title) {
        Optional<AboutUsCard> aboutUsCard = aboutUsCardRepository.findById(title);
        return aboutUsCard.map(AboutUsCardMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with title: " + title));
    }

    // Create AboutUsCard with image upload
    @PostMapping()
    public ResponseEntity<AboutUsCardResponse> create(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {

        AboutUsCardRequest aboutUsCardRequest = new AboutUsCardRequest();
        aboutUsCardRequest.setTitle(title);
        aboutUsCardRequest.setDescription(description);

        // Handle image file (file1)
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

                // Save the image file to the target location
                file1.transferTo(targetLocation);
                aboutUsCardRequest.setAboutCardImage(uniqueFileName);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AboutUsCardResponse("Error saving the image", "", ""));
            }
        }

        // Create the AboutUsCard entity using the service
        AboutUsCardResponse response = aboutUsCardService.create(aboutUsCardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Edit AboutUsCard with image update
    @PutMapping("/{title}")
    public ResponseEntity<AboutUsCardResponse> edit(
            @PathVariable("title") String title,
            @RequestParam(value = "file1", required = false) MultipartFile file1,
            @RequestParam("description") String description) {

        // Create AboutUsCardRequest for update
        AboutUsCardRequest aboutUsCardRequest = new AboutUsCardRequest();
        aboutUsCardRequest.setTitle(title);
        aboutUsCardRequest.setDescription(description);

        // Find the existing AboutUsCard by title
        AboutUsCardResponse existingCard = aboutUsCardService.getById(title);
        if (existingCard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Handle image (file1) upload
        if (file1 != null && !file1.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file1.getOriginalFilename()));
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFileName);

            // Delete old image if it exists
            String oldImage = existingCard.getAboutCardImage();
            if (oldImage != null && !oldImage.isEmpty()) {
                File oldImageFile = new File(UPLOAD_DIR, oldImage);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }

            try {
                // Save the new image to the target location
                file1.transferTo(targetLocation);
                aboutUsCardRequest.setAboutCardImage(uniqueFileName);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AboutUsCardResponse("Error updating the image",  "", ""));
            }
        }

        // Update the AboutUsCard using the service
        AboutUsCardResponse response = aboutUsCardService.edit(title, aboutUsCardRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{title}")
    // Delete an AboutUsCard by title
    public ResponseEntity<Void> delete(@PathVariable("title") String title) {
        // Find the AboutUsCard entity by title
        AboutUsCard aboutUsCard = aboutUsCardRepository.findById(title)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with title: " + title));

        // Delete the image if it exists
        String aboutCardImage = aboutUsCard.getAboutCardImage();
        if (aboutCardImage != null && !aboutCardImage.isEmpty()) {
            File imageFile = new File(UPLOAD_DIR, aboutCardImage);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }

        // Delete the AboutUsCard entity from the database
        aboutUsCardRepository.delete(aboutUsCard);

        // Return a successful response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
