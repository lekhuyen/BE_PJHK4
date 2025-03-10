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
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
//phan quyen tren tung method
@EnableMethodSecurity
public class SecurityConfig {

    //private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    //    endpoint cho phep truy cap k can login
    private final String[] PUBLIC_ENDPOINTS_POST = {
            "/api/users",
            "/api/auth/login",
            "/api/auth/introspect",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/users/addImage/**",   // File upload
            "/api/auction/**",
    };

    private final String[] PUBLIC_ENDPOINTS_GET = {
            "/api/users/**",
            "/api/auction/**",
            "/api/bidding/**",
    };
    private final String[] PUBLIC_ENDPOINTS_DELETE = {
            "/api/users/**",
            "/api/auction/**",
    };
    private final String[] PUBLIC_ENDPOINTS_PUT = {
            "/api/users/**",
            "/api/auction/**",
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
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bidding/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/get-onhome").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/agora/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment/vn-pay").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment/vn-pay-callback").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/creator/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/contact/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/contact/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/aboutus/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/aboutus").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/aboutus/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/aboutuscard/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/aboutuscard").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/aboutuscard/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/aboutuscard/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviewitem/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviewitem").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/reviewitem/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chatroom/room/room/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/blog").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/blog/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/blog/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/auction/product").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/verify-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/auction/category/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/files").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/files/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/files/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/files/auctionItem/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/stripe/webhook").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stripe/balance").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stripe/payments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stripe/create-checkout-session/{productId}").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/remove-favorite-item").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/unfollow-auctioneer").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/favorites/add-favorite-item").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/favorites/follow-auctioneer").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-favorite-items/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-followed-auctioneers/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-followers-count/{auctioneerId}").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/bidding/statistics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bidding/successful-bidding").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bidding/admin-earnings").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-comments/{auctioneerId}").permitAll()

                        //t
                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/remove-favorite-item").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/unfollow-auctioneer").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/favorites/add-favorite-item").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/favorites/follow-auctioneer").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-favorite-items/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-followed-auctioneers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-followers-count/{auctioneerId}").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/v1/payment/bids/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment/won-items/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment/unwon-items/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/auction/upcoming").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/category/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/creator/{userId}/upcoming").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auction/creator/{userId}/active").permitAll()


                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/add-address").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/addImage/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/verify-citizen").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/add-address/{userId}").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users/user-citizen/**").permitAll()


                        .requestMatchers(HttpMethod.DELETE, "/api/users/delete/{userId}/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-followers-count/{auctioneerId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}/money").permitAll()


                        .requestMatchers(HttpMethod.POST, "/api/room-video-call").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/room-video-call/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/room-video-call").permitAll()


                        .requestMatchers(HttpMethod.POST, "/api/favorites/add-comment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/get-comments/{auctioneerId}").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/chatroom/room/get-room/**").permitAll()

                        .requestMatchers("/ws/**","/signaling/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/login/oauth2/code/google").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login/oauth2/authorization/google").permitAll()

                        .requestMatchers("/ws/**").permitAll()



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
        httpSecurity.cors(c -> c.configurationSource(corsConfigurationSource()));


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

//    public SecurityConfig(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) {
//        this.oAuth2UserService = oAuth2UserService;
//    }
//
//    @Bean
//    public SecurityFilterChain filterGoogleChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers("/login", "/oauth2/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home", true)
//                        .failureUrl("/login?error=true")
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(oAuth2UserService) // ✅ Injected correctly
//                        )
//                )
//                .csrf(csrf -> csrf.disable());
//
//        return httpSecurity.build();
//    }

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

//        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedOrigin("http://localhost:3000");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }

}