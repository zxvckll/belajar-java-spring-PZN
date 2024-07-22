package BelajarJavaSpring.PZN.restful.service;

import BelajarJavaSpring.PZN.restful.model.ContactResponse;
import BelajarJavaSpring.PZN.restful.model.CreateContactRequest;
import BelajarJavaSpring.PZN.restful.model.SearchContactRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateContactRequest;
import BelajarJavaSpring.PZN.restful.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import BelajarJavaSpring.PZN.restful.entity.Contact;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
  @Mock
  private ContactRepository contactRepository;

  @InjectMocks
  private ContactService contactService;

  @Mock
  private ValidatorService validatorService;


  private User user;


  private Contact contact;

  private Contact contact1;


  private CreateContactRequest createContactRequest;


  private UpdateContactRequest updateContactRequest;

  private SearchContactRequest searchContactRequest;

  @BeforeEach
  void setUp() {
    searchContactRequest = new SearchContactRequest();
    searchContactRequest.setName("Doe");
    searchContactRequest.setPage(0);
    searchContactRequest.setSize(10);

    user = new User();
    user.setUsername("testuser");
    user.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));
    user.setName("Test User");

    contact = new Contact();
    contact.setId("test");
    contact.setUser(user);
    contact.setFirstName("Doe");
    contact.setLastName("test");
    contact.setEmail("test@gmail.com");
    contact.setPhone("0989898test");

    contact1 = new Contact();
    contact1.setId("test1");
    contact1.setUser(user);
    contact1.setFirstName("John");
    contact1.setLastName("Doe");
    contact1.setEmail("john.doe@example.com");
    contact1.setPhone("1234567890");

    createContactRequest = new CreateContactRequest();
    createContactRequest.setFirstName("firstName");
    createContactRequest.setLastName("lastName");
    createContactRequest.setEmail("email@gmail.com");
    createContactRequest.setPhone("0989898");

    updateContactRequest = new UpdateContactRequest();
    updateContactRequest.setId("test");
    updateContactRequest.setFirstName("firstName");
    updateContactRequest.setLastName("lastName");
    updateContactRequest.setEmail("email@gmail.com");
    updateContactRequest.setPhone("0989898");

  }


  @Test
  void create() {

    contactService.create(createContactRequest,user);
    verify(contactRepository, times(1)).save(any(Contact.class));

  }



  @Test
  void get() {
    when(contactRepository.findFirstByUserAndId(user,contact.getId())).thenReturn(Optional.ofNullable(contact));

    ContactResponse response = contactService.get(user, contact.getId());

    assertEquals(contact.getLastName(), response.getLastName());
    assertEquals(contact.getEmail(), response.getEmail());

  }

  @Test
  void update() {
    when(contactRepository.findFirstByUserAndId(user,contact.getId())).thenReturn(Optional.ofNullable(contact));

    ContactResponse response = contactService.update(user, updateContactRequest);
    verify(contactRepository, times(1)).save(any(Contact.class));
    assertEquals(contact.getLastName(), response.getLastName());
    assertEquals(contact.getEmail(), response.getEmail());

  }

  @Test
  void delete() {
    when(contactRepository.findFirstByUserAndId(user,contact.getId())).thenReturn(Optional.ofNullable(contact));
    contactService.delete(user,contact.getId());
    verify(contactRepository, times(1)).delete(any(Contact.class));
  }

  @Test
  void search() {

    List<Contact> contacts = Arrays.asList(contact, contact1);
    Page<Contact> contactPage = new PageImpl<>(contacts);

    when(contactRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(contactPage);

    Page<ContactResponse> result = contactService.search(user, searchContactRequest);

    assertEquals(2, result.getTotalElements());
    assertEquals("Doe", result.getContent().get(0).getFirstName());
    assertEquals("John", result.getContent().get(1).getFirstName());
    assertEquals("test@gmail.com", result.getContent().get(0).getEmail());
    assertEquals("john.doe@example.com", result.getContent().get(1).getEmail());

  }
}