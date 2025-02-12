package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.AboutUs;
import fpt.aptech.server_be.dto.request.AboutUsRequest;
import fpt.aptech.server_be.dto.response.AboutUsResponse;
import fpt.aptech.server_be.mapper.AboutUsMapper;
import fpt.aptech.server_be.repositories.AboutUsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AboutUsService {
    private final AboutUsRepository aboutUsRepository;

    public AboutUsService(AboutUsRepository aboutUsRepository) {
        this.aboutUsRepository = aboutUsRepository;
    }

    // Get all AboutUs entries
    public List<AboutUsResponse> getAll() {
        // Fetch all AboutUs entities from the repository
        List<AboutUs> aboutUsList = aboutUsRepository.findAll();

        // Convert the list of AboutUs entities to AboutUsResponse DTOs
        return aboutUsList.stream()
                .map(AboutUsMapper::toResponse)  // Convert each AboutUs entity to AboutUsResponse
                .collect(Collectors.toList());   // Collect the result as a list
    }

    // Get AboutUs by ID
    public AboutUs getById(int id) {
        return aboutUsRepository.findById(id).orElse(null);  // Returns null if not found
    }

    // Save new AboutUs or update existing AboutUs
    public AboutUsResponse save(AboutUsRequest aboutUsRequest) {
        AboutUs aboutUs = AboutUsMapper.toEntity(aboutUsRequest);  // Convert request DTO to entity
        AboutUs savedAboutUs = aboutUsRepository.save(aboutUs);    // Save the entity to the database
        return AboutUsMapper.toResponse(savedAboutUs);  // Convert saved entity to response DTO
    }

    // Update an existing AboutUs entity
    public AboutUsResponse update(int id, AboutUsRequest aboutUsRequest) {
        Optional<AboutUs> existingAboutUs = aboutUsRepository.findById(id);
        if (existingAboutUs.isPresent()) {
            AboutUs aboutUs = existingAboutUs.get();
            aboutUs.setTitle(aboutUsRequest.getTitle());
            aboutUs.setDescription(aboutUsRequest.getDescription());
            aboutUs.setAboutImage1(aboutUsRequest.getAboutImage1());
            aboutUs.setAboutImage2(aboutUsRequest.getAboutImage2());
            AboutUs updatedAboutUs = aboutUsRepository.save(aboutUs);  // Update the entity in DB
            return AboutUsMapper.toResponse(updatedAboutUs);  // Convert the updated entity to DTO
        }
        return null;  // Return null if the entity with the given ID does not exist
    }
}
