package de.mirb.olingo.janos.sampleds.model;

import java.util.Calendar;
import org.apache.olingo.odata2.api.annotation.edm.*;

/**
 * Created by michael on 26.09.15.
 */
@EdmEntityType(name = "Person")
@EdmEntitySet(name = "Persons")
public class Person {
  @EdmKey
  @EdmProperty
  private Long id;
  @EdmProperty
  private String name;
  @EdmProperty
  private String lastname;
  @EdmProperty
  private Calendar birthdate;
  @EdmProperty
  private Address address = new Address();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public Calendar getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(Calendar birthdate) {
    this.birthdate = birthdate;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
