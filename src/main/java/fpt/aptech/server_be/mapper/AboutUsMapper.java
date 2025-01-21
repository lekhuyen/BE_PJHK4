package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.AboutUsResponse;
import fpt.aptech.server_be.entities.AboutUs;
import fpt.aptech.server_be.dto.request.AboutUsRequest;
import org.mapstruct.Mapper;

@Mapper
public class AboutUsMapper {

    // Convert AboutUsRequest DTO to AboutUs entity
    public static AboutUs toEntity(AboutUsRequest request) {
        if (request == null) {
            return null;
        }

        AboutUs aboutUs = AboutUs.builder()
                .id(request.getId())  // The ID can be present when updating
                .title(request.getTitle())
                .description(request.getDescription())
                .aboutImage1(request.getAboutImage1())
                .aboutImage2(request.getAboutImage2())
                .build();

        return aboutUs;
    }

    // Convert AboutUs entity to AboutUsResponse DTO
    public static AboutUsResponse toResponse(AboutUs aboutUs) {
        if (aboutUs == null) {
            return null;
        }

        return AboutUsResponse.builder()
                .id(aboutUs.getId())
                .title(aboutUs.getTitle())
                .description(aboutUs.getDescription())
                .aboutImage1(aboutUs.getAboutImage1())
                .aboutImage2(aboutUs.getAboutImage2())
                .build();
    }
}
