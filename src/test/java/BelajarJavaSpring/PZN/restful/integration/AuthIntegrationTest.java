package BelajarJavaSpring.PZN.restful.integration;

import BelajarJavaSpring.PZN.restful.repository.ContactRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.LoginUserRequest;
import BelajarJavaSpring.PZN.restful.model.TokenResponse;
import BelajarJavaSpring.PZN.restful.model.WebResponse;
import BelajarJavaSpring.PZN.restful.repository.AddressRepository;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private ContactRepository contactRepository;

  @BeforeEach
  void setUp() {

    addressRepository.deleteAll();
    contactRepository.deleteAll();

    userRepository.deleteAll();
  }

  @Test
  public void loginFailedUserNotFound() throws Exception {
    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc.perform(
        post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  public void loginFailedWrongPassword() throws Exception {

    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test1");

    mockMvc.perform(
        post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  public void loginSuccess() throws Exception {

    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setUsername("test");
    request.setPassword("test");

    mockMvc.perform(
        post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertNotNull(response.getData().getToken());
      assertNotNull(response.getData().getExpiredAt());

      User userDb = userRepository.findById("test").orElse(null);
      assertNotNull(userDb);
      assertEquals(userDb.getToken(),response.getData().getToken());
      assertEquals(userDb.getTokenExpiredAt(),response.getData().getExpiredAt());
    });
  }

  @Test
  void logoutFailed() throws Exception{
    mockMvc.perform(
        delete("/api/auth/logout")
            .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void logoutSuccess() throws Exception{
    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword(BCrypt.hashpw("test",BCrypt.gensalt()));
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000l);
    userRepository.save(user);


    mockMvc.perform(
        delete("/api/auth/logout")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN","test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      User userdb = userRepository.findById("test").orElse(null);
      assertNull(userdb.getToken());
      assertNull(userdb.getTokenExpiredAt());
    });
  }
}