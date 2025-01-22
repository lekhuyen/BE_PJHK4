package fpt.aptech.server_be.service;

import fpt.aptech.server_be.entities.AboutUsCard;
import fpt.aptech.server_be.dto.request.AboutUsCardRequest;
import fpt.aptech.server_be.dto.response.AboutUsCardResponse;
import fpt.aptech.server_be.mapper.AboutUsCardMapper;
import fpt.aptech.server_be.repositories.AboutUsCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    // Get an AboutUsCard by title (ID)
    public AboutUsCardResponse getById(String title) {
        Optional<AboutUsCard> aboutUsCard = aboutUsCardRepository.findById(title);
        return aboutUsCard.map(AboutUsCardMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with title: " + title));
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

    // Edit an existing AboutUsCard by title
    public AboutUsCardResponse edit(String title, AboutUsCardRequest aboutUsCardRequest) {
        // Check if the AboutUsCard exists
        AboutUsCard existingAboutUsCard = aboutUsCardRepository.findById(title)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with title: " + title));

        // Update fields
        existingAboutUsCard.setDescription(aboutUsCardRequest.getDescription());
        existingAboutUsCard.setAboutCardImage(aboutUsCardRequest.getAboutCardImage());

        // Save the updated entity
        AboutUsCard updatedAboutUsCard = aboutUsCardRepository.save(existingAboutUsCard);

        // Return updated response DTO
        return AboutUsCardMapper.toResponse(updatedAboutUsCard);
    }

    // Delete an AboutUsCard by title
    public void delete(String title) {
        // Check if the AboutUsCard exists
        AboutUsCard aboutUsCard = aboutUsCardRepository.findById(title)
                .orElseThrow(() -> new RuntimeException("AboutUsCard not found with title: " + title));

        // Delete the entity
        aboutUsCardRepository.delete(aboutUsCard);
    }
}
