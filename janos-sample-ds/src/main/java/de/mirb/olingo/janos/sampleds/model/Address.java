package de.mirb.olingo.janos.sampleds.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmComplexType;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

/**
 * Created by michael on 26.09.15.
 */
@EdmComplexType
public class Address {
  @EdmProperty(name="ZipCode")
  private String postalCode;
  private String city;
  private String street;

  public Address() { /* empty ctor necessary for Janos */  }

  public Address(Address address) {
    postalCode = address.getPostalCode();
    city = address.getCity();
    street = address.getStreet();
  }

  public Address(String postalCode, String city, String street) {
    this.postalCode = postalCode;
    this.city = city;
    this.street = street;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCity() {
    return city;
  }

  public String getStreet() {
    return street;
  }
}
