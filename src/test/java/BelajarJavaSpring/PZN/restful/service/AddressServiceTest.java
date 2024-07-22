package BelajarJavaSpring.PZN.restful.service;

import BelajarJavaSpring.PZN.restful.entity.Address;
import BelajarJavaSpring.PZN.restful.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import BelajarJavaSpring.PZN.restful.entity.Contact;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.AddressResponse;
import BelajarJavaSpring.PZN.restful.model.CreateAddressRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateAddressRequest;
import BelajarJavaSpring.PZN.restful.repository.AddressRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

  @Mock
  private ValidatorService validatorService;

  @InjectMocks
  private AddressService addressService;

  @Mock
  private ContactRepository contactRepository;

  @Mock
  private AddressRepository addressRepository;

  private User user;

  private Contact contact;

  private Address address;


  private CreateAddressRequest createAddressRequest;

  private UpdateAddressRequest updateAddressRequest;


  @BeforeEach
  void setUp() {
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

    address = new Address();
    address.setId("test");
    address.setStreet("street");
    address.setCity("city");
    address.setProvince("province");
    address.setCountry("country");
    address.setPostalCode("postal code");
    address.setContact(contact);

    createAddressRequest = new CreateAddressRequest();
    createAddressRequest.setStreet("street");
    createAddressRequest.setCity("city");
    createAddressRequest.setProvince("province");
    createAddressRequest.setCountry("country");
    createAddressRequest.setPostalCode("postal code");
    createAddressRequest.setContactId(contact.getId());

    updateAddressRequest = new UpdateAddressRequest();
    updateAddressRequest.setId("test");
    updateAddressRequest.setStreet("street1");
    updateAddressRequest.setCity("city1");
    updateAddressRequest.setProvince("province1");
    updateAddressRequest.setCountry("country1");
    updateAddressRequest.setPostalCode("postal code1");
    updateAddressRequest.setContactId(contact.getId());


  }

  @Test
  void create() {
    when(contactRepository.findFirstByUserAndId(user, contact.getId())).thenReturn(Optional.of(contact));

    AddressResponse response = addressService.create(user, createAddressRequest);
    verify(addressRepository, times(1)).save(any(Address.class));
  }

  @Test
  void get() {
    when(contactRepository.findFirstByUserAndId(user, contact.getId())).thenReturn(Optional.of(contact));
    when(addressRepository.findFirstByContactAndId(contact, address.getId())).thenReturn(Optional.of(address));

    AddressResponse response = addressService.get(user, contact.getId(), address.getId());

    assertEquals(response.getCountry(), address.getCountry());


  }

  @Test
  void update() {
    when(contactRepository.findFirstByUserAndId(user, contact.getId())).thenReturn(Optional.of(contact));
    when(addressRepository.findFirstByContactAndId(contact, address.getId())).thenReturn(Optional.of(address));

    addressService.update(user, updateAddressRequest);

    assertEquals(address.getCity(), updateAddressRequest.getCity());
    assertEquals(address.getStreet(), updateAddressRequest.getStreet());
    assertEquals(address.getProvince(), updateAddressRequest.getProvince());

  }

  @Test
  void remove() {
    when(contactRepository.findFirstByUserAndId(user, contact.getId())).thenReturn(Optional.of(contact));
    when(addressRepository.findFirstByContactAndId(contact, address.getId())).thenReturn(Optional.of(address));

    addressService.remove(user, contact.getId(), address.getId());

    verify(addressRepository, times(1)).delete(any(Address.class));

  }

  @Test
  void list() {
    List<Address> addresses = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Address address1 = new Address();
      address1.setId("test" + i);
      address1.setStreet("street" + i);
      address1.setCity("city" + i);
      address1.setProvince("province" + i);
      address1.setCountry("country" + i);
      address1.setPostalCode("postal code" + i);
      address1.setContact(contact);
      addresses.add(address1);
    }
    when(contactRepository.findFirstByUserAndId(user, contact.getId())).thenReturn(Optional.of(contact));
    when(addressRepository.findAllByContact(contact)).thenReturn(addresses);

    List<AddressResponse> responses = addressService.list(user, contact.getId());

    assertNotNull(responses);
    assertEquals(5, responses.size());

    for (int i = 0; i < responses.size(); i++) {
      AddressResponse response = responses.get(i);
      assertEquals("test" + i, response.getId());
      assertEquals("street" + i, response.getStreet());
      assertEquals("city" + i, response.getCity());
      assertEquals("province" + i, response.getProvince());
      assertEquals("country" + i, response.getCountry());
      assertEquals("postal code" + i, response.getPostalCode());
    }

  }
}