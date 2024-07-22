package BelajarJavaSpring.PZN.restful.integration;


import BelajarJavaSpring.PZN.restful.model.UserResponse;
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
import BelajarJavaSpring.PZN.restful.model.RegisterUserRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateUserRequest;
import BelajarJavaSpring.PZN.restful.model.WebResponse;
import BelajarJavaSpring.PZN.restful.repository.AddressRepository;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

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
  void testRegisterSuccess() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("rahasia");
    request.setName("Test");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertEquals("OK", response.getData());
    });
  }


  @Test
  void testRegisterBadRequest() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("");
    request.setPassword("");
    request.setName("");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });
  }



  @Test
  void testRegisterDuplicate() throws Exception {
    User user = new User();
    user.setName("test");
    user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
    user.setUsername("test");
    userRepository.save(user);

    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("rahasia");
    request.setName("test");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserUnauthorized() throws Exception{
      mockMvc.perform(
          get("/api/users/current")
              .accept(MediaType.APPLICATION_JSON)
              .header("X-API-TOKEN","notfound")
      ).andExpectAll(
          status().isUnauthorized()
      ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
          });
  }

  @Test
  void getUserUnauthorizedTokenNotSent() throws Exception{
    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserSuccess() throws Exception{
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
    userRepository.save(user);

    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN","test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals("test",response.getData().getUsername());
      assertEquals("test",response.getData().getName());
    });
  }

  @Test
  void getUserTokenExpired() throws Exception{
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() - 100000000L);
    userRepository.save(user);

    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN","test")
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());

    });
  }

  @Test
  void updateUserUnauthorized() throws Exception{
    UpdateUserRequest request = new UpdateUserRequest();

    mockMvc.perform(
        patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
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
  void updateUserSuccess() throws Exception{
    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("rahasia",BCrypt.gensalt()));
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);
    userRepository.save(user);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("sam");
    request.setPassword("sam");

    mockMvc.perform(
        patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN","test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals("sam",response.getData().getName());
      assertEquals("test",response.getData().getUsername());

      User userdb = userRepository.findById(response.getData().getUsername()).orElse(null);
      assertNotNull(userdb);
      assertTrue(BCrypt.checkpw("sam",userdb.getPassword()));

    });
  }



}



