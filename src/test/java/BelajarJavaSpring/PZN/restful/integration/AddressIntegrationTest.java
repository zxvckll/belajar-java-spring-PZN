package BelajarJavaSpring.PZN.restful.integration;

import BelajarJavaSpring.PZN.restful.model.AddressResponse;
import BelajarJavaSpring.PZN.restful.model.CreateAddressRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateAddressRequest;
import BelajarJavaSpring.PZN.restful.model.WebResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import BelajarJavaSpring.PZN.restful.entity.Address;
import BelajarJavaSpring.PZN.restful.entity.Contact;
import BelajarJavaSpring.PZN.restful.entity.User;
import programmerzamannow.restful.model.*;
import BelajarJavaSpring.PZN.restful.repository.AddressRepository;
import BelajarJavaSpring.PZN.restful.repository.ContactRepository;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


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

    Contact contact = new Contact();
    contact.setId("test");
    contact.setUser(user);
    contact.setFirstName("sam");
    contact.setLastName("sandi");
    contact.setEmail("example@gmail.com");
    contact.setPhone("0899112121");
    contactRepository.save(contact);
  }

  @Test
  void createAddressBadRequest() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setContactId("test");
    request.setCity("city");
    request.setStreet("street");
    request.setProvince("");
    request.setCountry("");
    request.setPostalCode("postal code");

    mockMvc.perform(
        post("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });

  }


  @Test
  void createAddressSuccess() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setContactId("test");
    request.setCity("city");
    request.setStreet("street");
    request.setProvince("province");
    request.setCountry("country");
    request.setPostalCode("postal code");


    mockMvc.perform(
        post("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(request.getStreet(), response.getData().getStreet());
      assertEquals(request.getCity(), response.getData().getCity());
      assertEquals(request.getProvince(), response.getData().getProvince());
      assertEquals(request.getCountry(), response.getData().getCountry());
      assertEquals(request.getPostalCode(), response.getData().getPostalCode());

      assertTrue(addressRepository.existsById(response.getData().getId()));
    });

  }

  @Test
  void getAddressNotFound() throws Exception {

    mockMvc.perform(
        get("/api/contacts/test/addresses/test1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isNotFound()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });

  }

  @Test
  void getAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("test").orElseThrow();
    Address address = new Address();
    address.setContact(contact);
    address.setCity("city");
    address.setId("test");
    address.setStreet("street");
    address.setProvince("province");
    address.setCountry("country");
    address.setPostalCode("postal code");
    addressRepository.save(address);


    mockMvc.perform(
        get("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals(address.getStreet(), response.getData().getStreet());
      assertEquals(address.getCity(), response.getData().getCity());
      assertEquals(address.getProvince(), response.getData().getProvince());
      assertEquals(address.getCountry(), response.getData().getCountry());
      assertEquals(address.getPostalCode(), response.getData().getPostalCode());

      assertTrue(addressRepository.existsById(response.getData().getId()));

    });

  }

  @Test
  void updateAddressBadRequest() throws Exception {

    Contact contact = contactRepository.findById("test").orElseThrow();
    Address address = new Address();
    address.setContact(contact);
    address.setCity("city");
    address.setId("test");
    address.setStreet("street");
    address.setProvince("province");
    address.setCountry("country");
    address.setPostalCode("postal code");
    addressRepository.save(address);

    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCountry("");


    mockMvc.perform(
        put("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });

  }

  @Test
  void updateAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();
    Address address = new Address();
    address.setContact(contact);
    address.setCity("city");
    address.setId("test");
    address.setStreet("street");
    address.setProvince("province");
    address.setCountry("country");
    address.setPostalCode("postal code");
    addressRepository.save(address);

    UpdateAddressRequest request = new UpdateAddressRequest();

    request.setContactId("sam");
    request.setCity("sam");
    request.setStreet("sam");
    request.setProvince("sam");
    request.setCountry("sam");
    request.setPostalCode("sam code");


    mockMvc.perform(
        put("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(request.getStreet(), response.getData().getStreet());
      assertEquals(request.getCity(), response.getData().getCity());
      assertEquals(request.getProvince(), response.getData().getProvince());
      assertEquals(request.getCountry(), response.getData().getCountry());
      assertEquals(request.getPostalCode(), response.getData().getPostalCode());

      assertTrue(addressRepository.existsById(response.getData().getId()));
    });

  }

  @Test
  void deleteAddressNotFound() throws Exception {
    mockMvc.perform(
        delete("/api/contacts/test/addresses/test1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });

  }

  @Test
  void deleteAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();
    Address address = new Address();
    address.setContact(contact);
    address.setCity("city");
    address.setId("test");
    address.setStreet("street");
    address.setProvince("province");
    address.setCountry("country");
    address.setPostalCode("postal code");
    addressRepository.save(address);

    mockMvc.perform(
        delete("/api/contacts/test/addresses/test")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertFalse(addressRepository.existsById("test"));
    });

  }

  @Test
  void listAddressNotFound() throws Exception {

    mockMvc.perform(
        get("/api/contacts/tes1t/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isNotFound()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });

  }

  @Test
  void listAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("test").orElseThrow();

    for (int i = 0; i < 5; i++) {
      Address address = new Address();
      address.setContact(contact);
      address.setCity("city" + i);
      address.setId("test" + i);
      address.setStreet("street" + i);
      address.setProvince("province" + i);
      address.setCountry("country" + i);
      address.setPostalCode("postal code" + i);
      addressRepository.save(address);
    }



    mockMvc.perform(
        get("/api/contacts/test/addresses")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals(5,response.getData().size());




    });

  }


}