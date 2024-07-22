package BelajarJavaSpring.PZN.restful.resolver;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.repository.UserRepository;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

  @Autowired
  private UserRepository userRepository;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return User.class.equals(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
    String token = servletRequest.getHeader("X-API-Token");
    if(token == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized");
    }

    User user = userRepository.findFirstByToken(token).orElseThrow(() -> {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized");
    });

    if (user.getTokenExpiredAt() < System.currentTimeMillis()){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized");
    }

    return user;
  }
}
