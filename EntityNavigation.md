# Navigation between entities

The class `AnnotationHelper.AnnotatedNavInfo` is constructed by the helper method
`Annotation#getCommonNavigationInfo` to describe the annotated navigation between
two entities.

## Example code fragments

```java
@EdmEntityType(name = "Building")
@EdmEntitySet(name = "Buildings")
public class Building {
  @EdmKey @EdmProperty(name = "ID")
  private String id;
  @EdmProperty(name = "Name")
  private String name;
  @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY)
  private List<Room> rooms = new ArrayList<>();
  // etc.
}
```

```java
@EdmEntityType(name = "Room")
@EdmEntitySet(name = "Rooms")
public class Room {
  @EdmKey @EdmProperty(name = "ID")
  private String id;
  @EdmProperty(name = "Name")
  private String name;
  @EdmNavigationProperty
  private Building building;
  // etc.
}
```

```java
@EdmEntityType(name = "Closet")
@EdmEntitySet(name = "Closets")
public class Closet {
  @EdmKey @EdmProperty(name = "ID")
  private String id;
  @EdmProperty(name = "Name")
  private String name;
  @EdmNavigationProperty()
  private Building building;
  // etc.
}
```

```java
@EdmEntityType(name = "Task")
@EdmEntitySet(name = "Tasks")
public class Task {
  @EdmKey @EdmProperty(name = "ID")
  private String id;
  @EdmProperty(name = "Name")
  private String name;
  @EdmNavigationProperty(multiplicity = Multiplicity.MANY)
  private List<Task> subTasks = new ArrayList<>();
  // etc.
}
```

## Bi-directional navigation

A class has a navigation property to another class and the second class
has a navigation property back to the first.

In the above code fragments, the relationship between `Building` and `Room` is
bi-directional. A room has a link to the building where it is found and a
building maintains a list of its rooms.

A call to `AnnotationHelper.getCommonNavigation(Room.class, Building.class)`
returns an `AnnotatedNavInfo` instance with:

* `isBiDirectional()` = `true`
* `getFromField()` = `building`
* `getToMultiplicity()` = `ONE`
* `getToField()` = `rooms`
* `getFromMultiplicity()` = `MANY`

A call to `AnnotationHelper.getCommonNavigation(Building.class, Room.class)`
returns an `AnnotatedNavInfo` instance with:

* `isBiDirectional()` = `true`
* `getFromField()` = `rooms`
* `getToMultiplicity()` = `MANY`
* `getToField()` = `building`
* `getFromMultiplicity()` = `ONE`

## Uni-directional navigation

A class has a navigation property to another class but the second class
does not have a navigation property back to the first.

In the above code fragments, the relationship between `Building` and `Closet` is
uni-directional. A closet has a link to the building where it is found but a
building does not have a list of any closets.

A call to `AnnotationHelper.getCommonNavigation(Closet.class, Building.class)`
returns an `AnnotatedNavInfo` instance with:

* `isBiDirectional()` = `false`
* `getFromField()` = `building`
* `getFromMultiplicity()` = `ONE`
* `getToField()` = `null`
* `getToMultiplicity()` = `ONE`

A call to `AnnotationHelper.getCommonNavigation(Building.class, Closet.class)`
returns an `AnnotatedNavInfo` instance with:

* `isBiDirectional()` = `false`
* `getFromField()` = `null`
* `getFromMultiplicity()` = `ONE`
* `getToField()` = `closet`
* `getToMultiplicity()` = `ONE`

## Self-navigation

Self-navigation is a special case of uni-directional navigation.

A call to `AnnotationHelper.getCommonNavigation(Task.class, Task.class)`
returns an `AnnotatedNavInfo` instance with:

* `isBiDirectional()` = `false`
* `getFromField()` = `subTasks`
* `getToMultiplicity()` = `ONE`
* `getToField()` = `null`
* `getFromMultiplicity()` = `ONE`
