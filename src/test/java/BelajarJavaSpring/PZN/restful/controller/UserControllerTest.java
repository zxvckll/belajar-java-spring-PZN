package BelajarJavaSpring.PZN.restful.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.RegisterUserRequest;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserRepository userRepository;


  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUsername("testuser");
    user.setPassword("password123");
    user.setName("Test User");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
  }

  @Test
  void testRegister() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("testuser");
    request.setPassword("password123");
    request.setName("Test User");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value("OK"));
  }


}
