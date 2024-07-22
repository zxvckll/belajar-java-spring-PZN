package BelajarJavaSpring.PZN.restful.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import BelajarJavaSpring.PZN.restful.entity.Contact;
import BelajarJavaSpring.PZN.restful.entity.User;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ContactRepositoryTest {

  @Mock
  private ContactRepository contactRepository;

  @Test
  void testFindFirstByUserAndId() {
    User user = new User();
    user.setUsername("test");

    Contact contact = new Contact();
    contact.setId("1");
    contact.setUser(user);

    when(contactRepository.findFirstByUserAndId(user,contact.getId())).thenReturn(Optional.of(contact));

    Optional<Contact> foundContact = contactRepository.findFirstByUserAndId(user,"1");

    assertThat(foundContact).isPresent();
    assertThat(foundContact.get().getId()).isEqualTo("1");

  }
}