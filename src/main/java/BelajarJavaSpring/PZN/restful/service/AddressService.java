package BelajarJavaSpring.PZN.restful.service;

import BelajarJavaSpring.PZN.restful.entity.Address;
import BelajarJavaSpring.PZN.restful.model.UpdateAddressRequest;
import BelajarJavaSpring.PZN.restful.repository.AddressRepository;
import BelajarJavaSpring.PZN.restful.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import BelajarJavaSpring.PZN.restful.entity.Contact;
import BelajarJavaSpring.PZN.restful.entity.User;
import BelajarJavaSpring.PZN.restful.model.AddressResponse;
import BelajarJavaSpring.PZN.restful.model.CreateAddressRequest;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private ValidatorService validatorService;

  @Autowired
  private ContactRepository contactRepository;

  @Transactional
  public AddressResponse create(User user,CreateAddressRequest request){
    validatorService.validate(request);

    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found")
    );

    Address address = new Address();
    address.setContact(contact);
    address.setId(UUID.randomUUID().toString());
    address.setStreet(request.getStreet());
    address.setCity(request.getCity());
    address.setProvince(request.getProvince());
    address.setCountry(request.getCountry());
    address.setPostalCode(request.getPostalCode());
    addressRepository.save(address);

    return toAddressResponse(address);

  }

  @Transactional(readOnly = true)
  public AddressResponse get(User user, String contactId, String addressId){

    Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found")
    );

    Address address = addressRepository.findFirstByContactAndId(contact,addressId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found")
    );

    return toAddressResponse(address);

  }

  @Transactional
  public AddressResponse update(User user, UpdateAddressRequest request){

    validatorService.validate(request);
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found")
    );

    Address address = addressRepository.findFirstByContactAndId(contact, request.getId()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found")
    );

    address.setStreet(request.getStreet());
    address.setCity(request.getCity());
    address.setProvince(request.getProvince());
    address.setCountry(request.getCountry());
    address.setPostalCode(request.getPostalCode());
    addressRepository.save(address);

    return toAddressResponse(address);

  }
  @Transactional
  public void remove(User user,String contactId,String addressId){
    Contact contact = contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found")
    );

    Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found")
    );

    addressRepository.delete(address);
  }

  @Transactional(readOnly = true)
  public List<AddressResponse> list(User user,String contactId) {
    Contact contact = contactRepository.findFirstByUserAndId(user,contactId).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found")
    );

    List<Address> addresses = addressRepository.findAllByContact(contact);

    return addresses.stream().map(this::toAddressResponse).toList();
  }

  private AddressResponse toAddressResponse(Address address) {
    return AddressResponse.builder()
        .id(address.getId())
        .street(address.getStreet())
        .city(address.getCity())
        .province(address.getProvince())
        .country(address.getCountry())
        .postalCode(address.getPostalCode())
        .build();
  }
}


