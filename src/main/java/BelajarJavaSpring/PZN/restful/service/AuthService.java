package BelajarJavaSpring.PZN.restful.service;

import BelajarJavaSpring.PZN.restful.model.LoginUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.TokenResponse;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;
import BelajarJavaSpring.PZN.restful.security.BCrypt;

import java.util.UUID;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidatorService validatorService;




  @Transactional
  public TokenResponse login(LoginUserRequest request ) {
    validatorService.validate(request);

    User user = userRepository.findById(request.getUsername())
        .orElseThrow(() ->
          new ResponseStatusException(HttpStatus.UNAUTHORIZED,"username or password wrong"));

    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {

        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpiredAt(next30Days());
        userRepository.save(user);

        return TokenResponse.builder()
            .token(user.getToken())
            .expiredAt(user.getTokenExpiredAt())
            .build();
    } else {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"username or password wrong");
    }
  }

  @Transactional
  public void logout(User user) {
    user.setToken(null);
    user.setTokenExpiredAt(null);
    userRepository.save(user);
  }


  public Long next30Days(){
    return System.currentTimeMillis() + (1000*60*60*24*30);
  }

}
