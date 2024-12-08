package fpt.aptech.server_be.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CloundinaeyConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dktxmtewo",
                "api_key", "615455396349343",
                "api_secret", "pNUKCDMNAmSc1-u4lMnpIWFj-x4"
        ));
    }

}
