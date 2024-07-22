package BelajarJavaSpring.PZN.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.RegisterUserRequest;
import BelajarJavaSpring.PZN.restful.model.UpdateUserRequest;
import BelajarJavaSpring.PZN.restful.model.UserResponse;
import BelajarJavaSpring.PZN.restful.model.WebResponse;
import programmerzamannow.restful.repository.*;
import BelajarJavaSpring.PZN.restful.service.UserService;

@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping(path = "/api/users",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
    userService.register(request);
    return WebResponse.<String>builder().data("OK").build();
  }

  @GetMapping(
      path = "/api/users/current",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<UserResponse> get(User user) {
    UserResponse userResponse = userService.get(user);

    return WebResponse.<UserResponse>builder().data(userResponse).build();
  }

  @PatchMapping(path = "/api/users/current",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<UserResponse> update(@RequestBody UpdateUserRequest request, User user) {
    UserResponse response = userService.update(user, request);

    return WebResponse.<UserResponse>builder().data(response).build();
  }


}
