package BelajarJavaSpring.PZN.restful.controller;

import BelajarJavaSpring.PZN.restful.model.ContactResponse;
import BelajarJavaSpring.PZN.restful.model.CreateContactRequest;
import BelajarJavaSpring.PZN.restful.model.SearchContactRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateContactRequest;
import BelajarJavaSpring.PZN.restful.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import BelajarJavaSpring.PZN.restful.entity.User;
import programmerzamannow.restful.model.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ContactControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ContactService contactService;

  @InjectMocks
  private ContactController contactController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
  }

  @Test
  void testCreateContact() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    ContactResponse mockResponse = new ContactResponse(); // Mock your expected response

    when(contactService.create(any(CreateContactRequest.class), any(User.class)))
        .thenReturn(mockResponse);

    mockMvc.perform(post("/api/contacts")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"firstName\": \"John Doe\", \"email\": \"john.doe@example.com\" }"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.firstName").value(mockResponse.getFirstName()));

    verify(contactService, times(1)).create(any(CreateContactRequest.class), any(User.class));
  }

  @Test
  void testGetContact() throws Exception {
    String contactId = "123";
    ContactResponse mockResponse = new ContactResponse(); // Mock your expected response

    when(contactService.get(any(User.class), eq(contactId)))
        .thenReturn(mockResponse);

    mockMvc.perform(get("/api/contacts/{contactId}", contactId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.firstName").value(mockResponse.getFirstName()));

    verify(contactService, times(1)).get(any(User.class), eq(contactId));
  }

  @Test
  void testUpdateContact() throws Exception {
    String contactId = "123";
    UpdateContactRequest request = new UpdateContactRequest();
    request.setId(contactId);
    ContactResponse mockResponse = new ContactResponse(); // Mock your expected response

    when(contactService.update(any(User.class), any(UpdateContactRequest.class)))
        .thenReturn(mockResponse);

    mockMvc.perform(put("/api/contacts/{contactId}", contactId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"Updated Name\", \"email\": \"updated.email@example.com\" }"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.firstName").value(mockResponse.getFirstName()));

    verify(contactService, times(1)).update(any(User.class), any(UpdateContactRequest.class));
  }

  @Test
  void testDeleteContact() throws Exception {
    String contactId = "123";

    mockMvc.perform(delete("/api/contacts/{contactId}", contactId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").value("OK"));

    verify(contactService, times(1)).delete(any(User.class), eq(contactId));
  }

  @Test
  void testSearchContacts() throws Exception {
    String name = "John";
    String email = "john@example.com";
    String phone = "1234567890";
    Integer page = 0;
    Integer size = 10;
    SearchContactRequest searchRequest = SearchContactRequest.builder()
        .name(name)
        .email(email)
        .phone(phone)
        .page(page)
        .size(size)
        .build();
    List<ContactResponse> mockResponses = Arrays.asList(new ContactResponse(), new ContactResponse()); // Mock your expected responses
    PageImpl<ContactResponse> pageResponse = new PageImpl<>(mockResponses, PageRequest.of(page, size), mockResponses.size());

    when(contactService.search(any(User.class), any(SearchContactRequest.class)))
        .thenReturn(pageResponse);

    mockMvc.perform(get("/api/contacts")
            .param("name", name)
            .param("email", email)
            .param("phone", phone)
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(mockResponses.size()))
        .andExpect(jsonPath("$.paging.currentPage").value(page))
        .andExpect(jsonPath("$.paging.totalPage").value(pageResponse.getTotalPages()))
        .andExpect(jsonPath("$.paging.size").value(size));

    verify(contactService, times(1)).search(any(User.class), any(SearchContactRequest.class));
  }
}
