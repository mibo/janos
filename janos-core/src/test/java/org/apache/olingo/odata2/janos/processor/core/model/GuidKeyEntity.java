package org.apache.olingo.odata2.janos.processor.core.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

import java.util.UUID;

@EdmEntityType(name = "GuidKeyEntity", namespace = ModelSharedConstants.NAMESPACE_1)
@EdmEntitySet(name = GuidKeyEntity.GUID_KEY_ENTITIES)
public class GuidKeyEntity {

  public static final String GUID_KEY_ENTITIES = "GuidKeyEntities";
  @EdmProperty(name = "Id", type = EdmType.GUID, facets = @EdmFacets(nullable = false))
  @EdmKey
  protected UUID id;

  @EdmProperty(name = "Name")
  protected String name;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
