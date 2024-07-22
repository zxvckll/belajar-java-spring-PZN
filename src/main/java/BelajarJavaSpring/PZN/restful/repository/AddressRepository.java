package BelajarJavaSpring.PZN.restful.repository;

import BelajarJavaSpring.PZN.restful.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import BelajarJavaSpring.PZN.restful.entity.Contact;

import java.util.Optional;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,String> {

  Optional<Address> findFirstByContactAndId(Contact contact, String id);

  List<Address> findAllByContact(Contact contact);

}
