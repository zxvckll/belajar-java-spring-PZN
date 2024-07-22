package BelajarJavaSpring.PZN.restful.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.AddressResponse;
import BelajarJavaSpring.PZN.restful.model.CreateAddressRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateAddressRequest;
import BelajarJavaSpring.PZN.restful.service.AddressService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AddressControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AddressService addressService;

  @InjectMocks
  private AddressController addressController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(addressController).build();
  }

  @Test
  void testCreateAddress() throws Exception {
    String contactId = "123";
    CreateAddressRequest request = new CreateAddressRequest();
    request.setContactId(contactId);
    AddressResponse mockResponse = new AddressResponse(); // Mock your expected response

    when(addressService.create(any(User.class), any(CreateAddressRequest.class)))
        .thenReturn(mockResponse);

    mockMvc.perform(post("/api/contacts/{contactId}/addresses", contactId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"street\": \"123 Street\" }"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.street").value(mockResponse.getStreet()));

    verify(addressService, times(1)).create(any(User.class), any(CreateAddressRequest.class));
  }

  @Test
  void testGetAddress() throws Exception {
    String contactId = "123";
    String addressId = "456";
    AddressResponse mockResponse = new AddressResponse(); // Mock your expected response

    when(addressService.get(any(User.class), eq(contactId), eq(addressId)))
        .thenReturn(mockResponse);

    mockMvc.perform(get("/api/contacts/{contactId}/addresses/{addressId}", contactId, addressId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.street").value(mockResponse.getStreet()));

    verify(addressService, times(1)).get(any(User.class), eq(contactId), eq(addressId));
  }

  @Test
  void testUpdateAddress() throws Exception {
    String contactId = "123";
    String addressId = "456";
    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setContactId(contactId);
    request.setId(addressId);
    AddressResponse mockResponse = new AddressResponse(); // Mock your expected response

    when(addressService.update(any(User.class), any(UpdateAddressRequest.class)))
        .thenReturn(mockResponse);

    mockMvc.perform(put("/api/contacts/{contactId}/addresses/{addressId}", contactId, addressId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"address\": \"456 Avenue\" }"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.street").value(mockResponse.getStreet()));

    verify(addressService, times(1)).update(any(User.class), any(UpdateAddressRequest.class));
  }

  @Test
  void testRemoveAddress() throws Exception {
    String contactId = "123";
    String addressId = "456";

    mockMvc.perform(delete("/api/contacts/{contactId}/addresses/{addressId}", contactId, addressId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").value("OK"));

    verify(addressService, times(1)).remove(any(User.class), eq(contactId), eq(addressId));
  }

  @Test
  void testListAddresses() throws Exception {
    String contactId = "123";
    List<AddressResponse> mockResponses = Arrays.asList(new AddressResponse(), new AddressResponse()); // Mock your expected responses

    when(addressService.list(any(User.class), eq(contactId)))
        .thenReturn(mockResponses);

    mockMvc.perform(get("/api/contacts/{contactId}/addresses", contactId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(mockResponses.size()));

    verify(addressService, times(1)).list(any(User.class), eq(contactId));
  }
}
