package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.AboutUsCard;
import fpt.aptech.server_be.dto.request.AboutUsCardRequest;
import fpt.aptech.server_be.dto.response.AboutUsCardResponse;
import fpt.aptech.server_be.mapper.AboutUsCardMapper;
import fpt.aptech.server_be.repositories.AboutUsCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AboutUsCardService {

    private final AboutUsCardRepository aboutUsCardRepository;

    public AboutUsCardService(AboutUsCardRepository aboutUsCardRepository) {
        this.aboutUsCardRepository = aboutUsCardRepository;
    }

    // Get all AboutUsCard records
    public List<AboutUsCardResponse> getAll() {
        List<AboutUsCard> aboutUsCards = aboutUsCardRepository.findAll();
        return aboutUsCards.stream()
                .map(AboutUsCardMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get an AboutUsCard by ID
    public AboutUsCardResponse getById(int id) {
        AboutUsCard aboutUsCard = aboutUsCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with id: " + id));
        return AboutUsCardMapper.toResponse(aboutUsCard); // Corrected method call
    }

    // Create a new AboutUsCard
    public AboutUsCardResponse create(AboutUsCardRequest aboutUsCardRequest) {
        // Convert request to entity
        AboutUsCard aboutUsCard = AboutUsCardMapper.toEntity(aboutUsCardRequest);

        // Save the entity to the database
        AboutUsCard savedAboutUsCard = aboutUsCardRepository.save(aboutUsCard);

        // Convert entity back to response DTO
        return AboutUsCardMapper.toResponse(savedAboutUsCard);
    }

    // Edit an existing AboutUsCard by ID
    public AboutUsCardResponse edit(int id, AboutUsCardRequest aboutUsCardRequest) {  // Changed title to id
        // Check if the AboutUsCard exists by ID
        AboutUsCard existingAboutUsCard = aboutUsCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with id: " + id));

        // Update fields
        existingAboutUsCard.setTitle(aboutUsCardRequest.getTitle());  // You can now update the title as well
        existingAboutUsCard.setDescription(aboutUsCardRequest.getDescription());
        existingAboutUsCard.setAboutCardImage(aboutUsCardRequest.getAboutCardImage());

        // Save the updated entity
        AboutUsCard updatedAboutUsCard = aboutUsCardRepository.save(existingAboutUsCard);

        // Return updated response DTO
        return AboutUsCardMapper.toResponse(updatedAboutUsCard);
    }

    // Delete an AboutUsCard by ID
    public void delete(int id) {  // Changed title to id
        // Check if the AboutUsCard exists by ID
        AboutUsCard aboutUsCard = aboutUsCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with id: " + id));

        // Delete the entity
        aboutUsCardRepository.delete(aboutUsCard);
    }
}
