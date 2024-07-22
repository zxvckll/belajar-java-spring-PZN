package BelajarJavaSpring.PZN.restful.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import BelajarJavaSpring.PZN.restful.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

  Optional<User>  findFirstByToken(String token);
}
