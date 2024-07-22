package BelajarJavaSpring.PZN.restful.service;

import BelajarJavaSpring.PZN.restful.model.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.RegisterUserRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateUserRequest;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ValidatorService validatorService;

  @InjectMocks
  private UserService userService;

  private RegisterUserRequest registerRequest;
  private UpdateUserRequest updateRequest;
  private User existingUser;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterUserRequest();
    registerRequest.setUsername("testuser");
    registerRequest.setPassword("password123");
    registerRequest.setName("Test User");

    updateRequest = new UpdateUserRequest();
    updateRequest.setPassword("newpassword123");
    updateRequest.setName("Updated User");

    existingUser = new User();
    existingUser.setUsername("testuser");
    existingUser.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));
    existingUser.setName("Test User");
  }

  @Test
  void testRegisterUser_Success() {
    when(userRepository.existsById(registerRequest.getUsername())).thenReturn(false);

    userService.register(registerRequest);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterUser_UsernameExists() {
    when(userRepository.existsById(registerRequest.getUsername())).thenReturn(true);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.register(registerRequest);
    });

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void testGetUser_Success() {
    UserResponse response = userService.get(existingUser);

    assertEquals(existingUser.getUsername(), response.getUsername());
    assertEquals(existingUser.getName(), response.getName());
  }

  @Test
  void testUpdateUser_Success() {
    when(userRepository.save(any(User.class))).thenReturn(existingUser);

    UserResponse response = userService.update(existingUser, updateRequest);

    assertEquals(updateRequest.getName(), response.getName());

    assertTrue(BCrypt.checkpw(updateRequest.getPassword(), existingUser.getPassword()));
  }
}
