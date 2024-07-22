package BelajarJavaSpring.PZN.restful.controller;

import BelajarJavaSpring.PZN.restful.model.LoginUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import BelajarJavaSpring.PZN.restful.model.TokenResponse;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuthService authService;

  @MockBean
  private UserRepository userRepository;

  private LoginUserRequest loginUserRequest;

  private TokenResponse tokenResponse;

  @BeforeEach
  void setUp() {
    loginUserRequest = new LoginUserRequest();
    loginUserRequest.setUsername("testuser");
    loginUserRequest.setPassword("password");

    tokenResponse = new TokenResponse();
    tokenResponse.setToken("sample-token");
  }

  @Test
  void testLogin() throws Exception {
    when(authService.login(any(LoginUserRequest.class))).thenReturn(tokenResponse);

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"testuser\", \"password\":\"password\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"data\":{\"token\":\"sample-token\"}}"));
  }


}
