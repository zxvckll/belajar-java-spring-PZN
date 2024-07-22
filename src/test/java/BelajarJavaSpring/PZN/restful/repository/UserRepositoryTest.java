package BelajarJavaSpring.PZN.restful.repository;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import BelajarJavaSpring.PZN.restful.entity.User;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

  @Mock
  private UserRepository userRepository;

  @Test
  void testFindFirstByToken() {
    User user = new User();
    user.setUsername("test");
    user.setToken("token");

    when(userRepository.findFirstByToken(user.getToken())).thenReturn(Optional.of(user));

    Optional<User> foundUser = userRepository.findFirstByToken(user.getToken());
    assertThat(foundUser.isPresent());
    assertThat(foundUser.get().getUsername().equals("token"));

  }
}