package BelajarJavaSpring.PZN.restful.integration;

import BelajarJavaSpring.PZN.restful.model.ContactResponse;
import BelajarJavaSpring.PZN.restful.model.CreateContactRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateContactRequest;
import BelajarJavaSpring.PZN.restful.model.WebResponse;
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
import BelajarJavaSpring.PZN.restful.entity.Contact;
import BelajarJavaSpring.PZN.restful.entity.User;
import programmerzamannow.restful.model.*;
import BelajarJavaSpring.PZN.restful.repository.AddressRepository;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactIntegrationTest {

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AddressRepository addressRepository;

  @BeforeEach
  void setUp() {
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setName("test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
    userRepository.save(user);
  }

  @Test
  void createContactBadRequest() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("");
    request.setEmail("salah");

    mockMvc.perform(
        post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void createContactSuccess() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("sam");
    request.setLastName("sandi");
    request.setEmail("example@gmail.com");
    request.setPhone("0899112121");

    mockMvc.perform(
        post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertNotNull(response);
      assertEquals("sam", response.getData().getFirstName());
      assertEquals("sandi", response.getData().getLastName());
      assertEquals("example@gmail.com", response.getData().getEmail());
      assertEquals("0899112121", response.getData().getPhone());

      assertTrue(contactRepository.existsById(response.getData().getId()));

    });
  }

  @Test
  void getContactNotFound() throws Exception {

    mockMvc.perform(
        get("/api/contacts/1123123123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isNotFound()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("sam");
    contact.setLastName("sandi");
    contact.setEmail("example@gmail.com");
    contact.setPhone("0899112121");

    contactRepository.save(contact);

    mockMvc.perform(
        get("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertNotNull(response);
      assertEquals(contact.getId(), response.getData().getId());
      assertEquals(contact.getFirstName(), response.getData().getFirstName());
      assertEquals(contact.getLastName(), response.getData().getLastName());
      assertEquals(contact.getEmail(), response.getData().getEmail());
      assertEquals(contact.getPhone(), response.getData().getPhone());
    });
  }

  @Test
  void updateContactBadRequest() throws Exception {
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("");
    request.setEmail("salah");

    mockMvc.perform(
        put("/api/contacts/123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("sam");
    contact.setLastName("sandi");
    contact.setEmail("example@gmail.com");
    contact.setPhone("0899112121");

    contactRepository.save(contact);

    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("sam1");
    request.setLastName("sandi2");
    request.setEmail("example3@gmail.com");
    request.setPhone("08991121214");

    mockMvc.perform(
        put("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertNotNull(response);
      assertEquals(request.getFirstName(), response.getData().getFirstName());
      assertEquals(request.getLastName(), response.getData().getLastName());
      assertEquals(request.getEmail(), response.getData().getEmail());
      assertEquals(request.getPhone(), response.getData().getPhone());

      assertTrue(contactRepository.existsById(response.getData().getId()));

    });
  }

  @Test
  void deleteContactNotFound() throws Exception {

    mockMvc.perform(
        delete("/api/contacts/1123123123")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isNotFound()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void deleteContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("sam");
    contact.setLastName("sandi");
    contact.setEmail("example@gmail.com");
    contact.setPhone("0899112121");

    contactRepository.save(contact);

    mockMvc.perform(
        delete("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertFalse(contactRepository.existsById(contact.getId()));

    });
  }

  @Test
  void searchNotFound() throws Exception {

    mockMvc.perform(
        get("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(0, response.getData().size());
      assertEquals(0, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });
  }

  @Test
  void searchSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);
    for (int i = 0; i < 100; i++) {
      Contact contact = new Contact();
      contact.setId(UUID.randomUUID().toString());
      contact.setUser(user);
      contact.setFirstName("sam" + i);
      contact.setLastName("sandi" + i);
      contact.setEmail("example@gmail.com");
      contact.setPhone("0899112121");
      contactRepository.save(contact);
    }

    mockMvc.perform(
        get("/api/contacts")
            .queryParam("name","sam")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    mockMvc.perform(
        get("/api/contacts")
            .queryParam("name","sandi")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    mockMvc.perform(
        get("/api/contacts")
            .queryParam("email","gmail.com")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    mockMvc.perform(
        get("/api/contacts")
            .queryParam("phone","91121")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    mockMvc.perform(
        get("/api/contacts")
            .queryParam("phone","91121")
            .queryParam("page", "1000")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(0, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(1000, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

  }


}