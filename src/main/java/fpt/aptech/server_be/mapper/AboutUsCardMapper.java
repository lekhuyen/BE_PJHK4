package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.entities.AboutUsCard;
import fpt.aptech.server_be.dto.request.AboutUsCardRequest;
import fpt.aptech.server_be.dto.response.AboutUsCardResponse;
import org.mapstruct.Mapper;

@Mapper
public class AboutUsCardMapper {

    // Convert AboutUsCard entity to AboutUsCardResponse
    public static AboutUsCardResponse toResponse(AboutUsCard aboutUsCard) {
        if (aboutUsCard == null) {
            return null;
        }
        return AboutUsCardResponse.builder()
                .id(aboutUsCard.getId())
                .title(aboutUsCard.getTitle())
                .description(aboutUsCard.getDescription())
                .aboutCardImage(aboutUsCard.getAboutCardImage())
                .build();
    }

    // Convert AboutUsCardRequest to AboutUsCard entity
    public static AboutUsCard toEntity(AboutUsCardRequest aboutUsCardRequest) {
        if (aboutUsCardRequest == null) {
            return null;
        }
        return AboutUsCard.builder()
                .id(aboutUsCardRequest.getId())
                .title(aboutUsCardRequest.getTitle())
                .description(aboutUsCardRequest.getDescription())
                .aboutCardImage(aboutUsCardRequest.getAboutCardImage())
                .build();
    }

    // Convert AboutUsCardRequest to AboutUsCardResponse (optional, just in case you want to map to response directly)
    public static AboutUsCardResponse toResponseFromRequest(AboutUsCardRequest aboutUsCardRequest) {
        if (aboutUsCardRequest == null) {
            return null;
        }
        return AboutUsCardResponse.builder()
                .id(aboutUsCardRequest.getId())
                .title(aboutUsCardRequest.getTitle())
                .description(aboutUsCardRequest.getDescription())
                .aboutCardImage(aboutUsCardRequest.getAboutCardImage())
                .build();
    }
}