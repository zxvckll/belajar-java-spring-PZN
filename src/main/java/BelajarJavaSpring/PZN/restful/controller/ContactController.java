package BelajarJavaSpring.PZN.restful.controller;

import BelajarJavaSpring.PZN.restful.model.*;
import BelajarJavaSpring.PZN.restful.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import BelajarJavaSpring.PZN.restful.entity.User;
import programmerzamannow.restful.model.*;

import java.util.List;

@RestController
public class ContactController {

  @Autowired
  private ContactService contactService;

  @PostMapping(path = "/api/contacts",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ContactResponse> create(@RequestBody CreateContactRequest request, User user) {
    ContactResponse response = contactService.create(request, user);

    return WebResponse.<ContactResponse>builder().data(response).build();
  }

  @GetMapping(path = "/api/contacts/{contactId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ContactResponse> get(@PathVariable("contactId") String contactId, User user) {
    ContactResponse response = contactService.get(user, contactId);

    return WebResponse.<ContactResponse>builder().data(response).build();
  }

  @PutMapping(path = "/api/contacts/{contactId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<ContactResponse> update(@RequestBody UpdateContactRequest request,
                                             User user,
                                             @PathVariable("contactId") String contactId) {

    request.setId(contactId);
    ContactResponse response = contactService.update(user, request);

    return WebResponse.<ContactResponse>builder().data(response).build();
  }

  @DeleteMapping(path = "/api/contacts/{contactId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> delete(@PathVariable("contactId") String contactId, User user) {
    contactService.delete(user, contactId);

    return WebResponse.<String>builder().data("OK").build();
  }

  @GetMapping(path = "/api/contacts",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<List<ContactResponse>> search(User user,
                                                   @RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "email", required = false) String email,
                                                   @RequestParam(value = "phone", required = false) String phone,
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
    SearchContactRequest request = SearchContactRequest.builder()
        .name(name)
        .email(email)
        .phone(phone)
        .page(page)
        .size(size)
        .build();

    Page<ContactResponse> responses = contactService.search(user, request);
    return WebResponse.<List<ContactResponse>>builder()
        .data(responses.getContent())
        .paging(PagingResponse.builder()
            .currentPage(responses.getNumber())
            .totalPage(responses.getTotalPages())
            .size(responses.getSize())
            .build())
        .build();

  }
}
