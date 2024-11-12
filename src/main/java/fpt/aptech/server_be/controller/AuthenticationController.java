package fpt.aptech.server_be.controller;

import com.nimbusds.jose.JOSEException;
import fpt.aptech.server_be.dto.request.ApiResponse;
import fpt.aptech.server_be.dto.request.AuthenticationRequest;
import fpt.aptech.server_be.dto.request.IntrospectRequest;
import fpt.aptech.server_be.dto.response.AuthenticationResponse;
import fpt.aptech.server_be.dto.response.IntrospectResponse;
import fpt.aptech.server_be.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
       var result = authenticationService.authenticate(request);

       return ApiResponse.<AuthenticationResponse>builder()
               .result(result)
               .build();
    }

    //verify token
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {

        var result = authenticationService.introspect(request);

       return ApiResponse.<IntrospectResponse>builder()
               .code(1000)
               .result(result)
               .build();
    }

}
