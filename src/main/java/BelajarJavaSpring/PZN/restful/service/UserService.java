package BelajarJavaSpring.PZN.restful.service;

import BelajarJavaSpring.PZN.restful.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.RegisterUserRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateUserRequest;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import java.util.Objects;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidatorService validatorService;

  @Transactional
  public void register(RegisterUserRequest request) {

    validatorService.validate(request);

    if (userRepository.existsById(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already registered");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    user.setName(request.getName());

    userRepository.save(user);
  }

  public UserResponse get(User user) {
    return UserResponse.builder().name(user.getName()).username(user.getUsername()).build();
  }

  @Transactional
  public UserResponse update(User user, UpdateUserRequest request) {
    validatorService.validate(request);
    if(Objects.nonNull(request.getName())) {
      user.setName(request.getName());
    }

    if(Objects.nonNull(request.getPassword())) {
      user.setPassword(BCrypt.hashpw(request.getPassword() , BCrypt.gensalt()));
    }

    userRepository.save(user);

    return UserResponse.builder()
        .name(user.getName())
        .username(user.getUsername())
        .build();
  }




}
