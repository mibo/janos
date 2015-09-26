package de.mirb.olingo.janos.sampleds.ds;

import com.google.gson.Gson;
import de.mirb.olingo.janos.sampleds.model.Address;
import de.mirb.olingo.janos.sampleds.model.Person;
import org.apache.olingo.odata2.janos.processor.api.data.ReadOptions;
import org.apache.olingo.odata2.janos.processor.api.data.ReadResult;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStore;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Created by michael on 26.09.15.
 */
public class PersonStore implements DataStore<Person> {
  private AtomicLong currentId = new AtomicLong();
  private final File storeRoot;

  public PersonStore() {
    this(Paths.get(System.getProperty("java.io.tmpdir")));
  }

  public PersonStore(Path storePath) {
    try {
      if(storePath == null) {
        storeRoot = new File(Files.createTempDirectory("janos_ds_sample").toFile(), "persons");
      } else {
        storeRoot = new File(storePath.toFile(), "persons");
      }

      if(!storeRoot.exists()) {
        Files.createDirectory(storeRoot.toPath());
      } else if(storeRoot.isFile()) {
        throw new RuntimeException("Found file instead of directory at: " + storeRoot.getPath());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Class<Person> getDataTypeClass() {
    return Person.class;
  }

  public String getName() {
    return "PersonFileStore";
  }

  public Person createInstance() {
    return new Person();
  }

  public Person create(Person person) throws DataStoreException {
    Person result = copyPerson(person);
    result.setId(currentId.getAndIncrement());

    store(result);

    return result;
  }

  private Person copyPerson(Person person) {
    Person result = new Person();
    if(person != null) {
      result.setId(person.getId());
      result.setLastname(person.getLastname());
      result.setName(person.getName());
      if(person.getBirthdate() != null) {
        result.setBirthdate((Calendar) person.getBirthdate().clone());
      }
      if(person.getAddress() != null) {
        result.setAddress(new Address(person.getAddress()));
      }
    }
    return result;
  }


  public Person read(Person person) {
    return readFromPath(getPath(person));
  }

  private Person readFromPath(Path path) {
    try {
      String content = new String(Files.readAllBytes(path));
      return new Gson().fromJson(content.toString(), Person.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Path getPath(Person person) {
    return new File(storeRoot, "Person_" + person.getId()).toPath();
  }

  public Collection<Person> read() throws DataStoreException {
    try {
      Collection<Person> persons = new ArrayList<>();
      Stream<Path> files = Files.list(storeRoot.toPath());
      files.forEach(path -> persons.add(readFromPath(path)));
      return persons;
    } catch (IOException e) {
      throw new DataStoreException("Failure during read.", e);
    }
  }

  public ReadResult<Person> read(ReadOptions readOptions) throws DataStoreException {
    // currently no read options are supported
    return ReadResult.forResult(read()).build();
  }

  public Person update(Person person) throws DataStoreException {
    Person updated = copyPerson(person);
    store(updated);
    return updated;
  }

  public Person delete(Person person) {
    File file = getFile(person);
    Person p = read(person);
    file.delete();
    return p;
  }

  public boolean isKeyEqualChecked(Object first, Object second) throws DataStoreException {
    if(first instanceof Person && second instanceof Person) {
      return ((Person)first).getId().equals(((Person) second).getId());
    }
    return false;
  }

  private void store(Person result) throws DataStoreException {
    File file = getFile(result);
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
      String content = toJson(result);
      bw.write(content);
    } catch (IOException e) {
      throw new DataStoreException("Failure on storing a Person", e);
    }
  }

  private File getFile(Person result) {
    return new File(storeRoot, "Person_" + result.getId());
  }

  private String toJson(Person p) {
    return new Gson().toJson(p);
  }
}
