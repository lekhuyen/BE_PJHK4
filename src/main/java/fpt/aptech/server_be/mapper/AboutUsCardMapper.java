package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.entities.AboutUsCard;
import fpt.aptech.server_be.dto.request.AboutUsCardRequest;
import fpt.aptech.server_be.dto.response.AboutUsCardResponse;

public class AboutUsCardMapper {

    // Method to convert AboutUsCard entity to AboutUsCardResponse DTO
    public static AboutUsCardResponse toResponse(AboutUsCard aboutUsCard) {
        if (aboutUsCard == null) {
            return null;
        }

        return AboutUsCardResponse.builder()
                .title(aboutUsCard.getTitle())
                .description(aboutUsCard.getDescription())
                .aboutCardImage(aboutUsCard.getAboutCardImage())
                .build();
    }

    // Method to convert AboutUsCardRequest DTO to AboutUsCard entity
    public static AboutUsCard toEntity(AboutUsCardRequest aboutUsCardRequest) {
        if (aboutUsCardRequest == null) {
            return null;
        }

        return AboutUsCard.builder()
                .title(aboutUsCardRequest.getTitle())
                .description(aboutUsCardRequest.getDescription())
                .aboutCardImage(aboutUsCardRequest.getAboutCardImage())
                .build();
    }
}
