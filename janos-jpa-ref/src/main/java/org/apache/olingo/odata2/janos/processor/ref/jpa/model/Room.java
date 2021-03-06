/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.janos.processor.ref.jpa.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Entity
@EdmEntityType(name = "Room", namespace = ModelSharedConstants.NAMESPACE_1)
@EdmEntitySet(name = "Rooms")
public class Room extends RefBase {

  private Integer seats;
  private Integer version;
  @EdmNavigationProperty(name = "nr_Building", association = "BuildingRooms")
  @OneToOne
  private Building building;
  @OneToMany
  @EdmNavigationProperty(name = "nr_Employees")
  private List<Employee> employees = new ArrayList<>();

  public void setSeats(final int seats) {
    this.seats = seats;
  }

  public int getSeats() {
    return seats;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  public void setBuilding(final Building building) {
    this.building = building;
  }

  public Building getBuilding() {
    return building;
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj
        || obj != null && getClass() == obj.getClass() && id == ((Room) obj).id;
  }

  @Override
  public String toString() {
    return "{\"Id\":\"" + id + "\",\"Name\":\"" + name + "\",\"Seats\":" + seats + ",\"Version\":" + version + "}";
  }
}
