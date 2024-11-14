package fpt.aptech.server_be.configuration;

import fpt.aptech.server_be.enums.Role;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
//phan quyen tren tung method
@EnableMethodSecurity
public class SecurityConfig {

//    endpoint cho phep truy cap k can login
    private final String[] PUBLIC_ENDPOINTS_POST = {
            "/users",
            "/auth/login",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refresh",
            "/users/addImage/**",
    };

    private final String[] PUBLIC_ENDPOINTS_GET = {

    };
    private final String[] PUBLIC_ENDPOINTS_DELETE = {
            "/users/**"
    };
    private final String[] PUBLIC_ENDPOINTS_PUT = {
            "/users/**"
    };

//    @NonFinal
//    @Value("${jwt.signerKey}")
//    protected String SIGNER_KEY;

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                //cho phep truy cap
                request
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_POST).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_GET).permitAll()
                        .requestMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINTS_DELETE).permitAll()
                        .requestMatchers(HttpMethod.PUT, PUBLIC_ENDPOINTS_PUT).permitAll()

                        //user co role admin moi truy cap dc
//                        .requestMatchers(HttpMethod.GET,"/users")
//                        .hasRole(Role.ADMIN.name())
//                        .hasAuthority("ROLE_ADMIN")

                        //k cho phep truy cap
                        .anyRequest().authenticated());
//authen voi token, neu hop le se cho rest api
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                            .decoder(customJwtDecoder)
//                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                        //    custom SCOPE_ADMIN -> ROLE_ADMIN
                        .jwtAuthenticationConverter(jwtConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

//        httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
//        tat csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

//    custom SCOPE_ADMIN -> ROLE_ADMIN
    @Bean
    JwtAuthenticationConverter jwtConverter() {

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }



//    decode token xem co hop le k,
//    @Bean
//    JwtDecoder jwtDecoder() {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}