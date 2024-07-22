package BelajarJavaSpring.PZN.restful.repository;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import BelajarJavaSpring.PZN.restful.entity.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import BelajarJavaSpring.PZN.restful.entity.Contact;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AddressRepositoryTest {

  @Mock
  private AddressRepository addressRepository;

  @Test
  void testFindFirstByContactAndId() {
    Contact contact = new Contact();
    contact.setId("1");

    Address address = new Address();
    address.setId("123");
    address.setContact(contact);

    when(addressRepository.findFirstByContactAndId(contact, "123"))
        .thenReturn(Optional.of(address));

    Optional<Address> foundAddress = addressRepository.findFirstByContactAndId(contact, "123");

    assertThat(foundAddress).isPresent();
    assertThat(foundAddress.get().getId()).isEqualTo("123");
  }

  @Test
  void testFindAllByContact() {
    Contact contact = new Contact();
    contact.setId("1");

    Address address1 = new Address();
    address1.setId("123");
    address1.setContact(contact);

    Address address2 = new Address();
    address2.setId("124");
    address2.setContact(contact);

    List<Address> addresses = Arrays.asList(address1, address2);

    when(addressRepository.findAllByContact(contact))
        .thenReturn(addresses);

    List<Address> foundAddresses = addressRepository.findAllByContact(contact);

    assertThat(foundAddresses).hasSize(2);
    assertThat(foundAddresses).extracting(Address::getId).containsExactlyInAnyOrder("123", "124");
  }
}