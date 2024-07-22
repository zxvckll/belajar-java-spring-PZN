package BelajarJavaSpring.PZN.restful.controller;

import BelajarJavaSpring.PZN.restful.model.LoginUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.TokenResponse;
import BelajarJavaSpring.PZN.restful.model.WebResponse;
import BelajarJavaSpring.PZN.restful.service.AuthService;

@RestController
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping(path = "/api/auth/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
    TokenResponse tokenResponse = authService.login(request);
    return WebResponse.<TokenResponse>builder()
        .data(tokenResponse)
        .build();

  }

  @DeleteMapping(path = "/api/auth/logout",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> logout(User user) {
    authService.logout(user);
    return WebResponse.<String>builder().data("OK").build();
  }
}
