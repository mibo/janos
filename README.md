# Janos - Java Annotation extension for Apache Olingo V2

This extension provides *Java Annotations* for defining the OData (V2) EDM and Interfaces (Hooks) for attaching generic data sources providing the data.
The extension is based (forked) from the *Apache Olingo Annotation Processor* (see [Olingo Homepage](http://olingo.apache.org/doc/odata2/tutorials/AnnotationProcessorExtension.html) and [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22olingo-odata2-annotation-processor%22)).


## Current Features <small>(for stable version)</small>

  * Create your *EDM (Entity Data Model)* with using *Java Annotations* (the *EDM-(Java)-Annotations* from the [*Apache Olingo OData Library (v2)*](http://olingo.apache.org/doc/odata2/index.html) (or in [maven central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22olingo-odata2-api-annotation%22)))
  * Data Access Layer with...
    * ...`DataSource` for providing the *Data* from the view of an *OData Service*
    * ...`DataStore` for providing the *Data* from the view of an *Data Storage Service (e.g. a Database)*
  * Generic `DataSource` and `DataStore` implementations for...
    * ...using the *EDM-(Java)-Annotations* in combination with an *In-Memory-Store (none-persistent)*
    * ...using the *EDM-(Java)-Annotations* in combination with an *JPA-Based-Store*
  * `ReadOptions` and `ReadResult` for optimized data access (read) in the `DataStore` layer

## New and Noteworthy

  * `Version 2.3/3.0` (current development version - release *upcoming*)
    * Enhancement from **[Janos#10]**: More customization over generic (default) data processing (see also in *Roadmap* at the end of this readme)
    * Updated `Apache Olingo` to version `2.0.9`
  * `Version 2.2` (current stable version - release *21.02.2016*)
    * **[Janos#9](https://github.com/mibo/janos/issues/9):** Add method to create an `EdmProvider` based on `Annotations` (accordingly annotated classes)
    * **[Janos#8](https://github.com/mibo/janos/issues/8):** All fields of an POJO are handled as `@EdmProperty` by default (if no
    fields with annotations (beside `@EdmKey`) is found)
    * Updated `Apache Olingo` to version `2.0.6` and because Olingo updated to `GSon 2.4` also updated `Gson`.
    * Switch to *real* [Semantic Versioning](http://semver.org/). Based on this decision it is possible that next version will be `3
    .0` instead of `2.2`, however the API did not changed, hence the release version is now `2.2.0`.
  * `Version 2.1` (current stable version - released *07.10.2015*)
    * Added `ReadOptions` and `ReadResult` for optimization of data access (read) in the `DataStore` layer
    * Refactored API packages (incompatible changes to `2.0`)
    * Refactored (changed) API methods
      * `org.apache.olingo.odata2.janos.processor.api.data.source.DataSource` `createData(...)` method now returns the new created
      instance instead of modifying the given data parameter instance.
      * `org.apache.olingo.odata2.janos.processor.api.data.store.DataStore` all data access methods can now throw
      a `DataStoreException`
    * Updated to newest Apache Olingo version `2.0.5`
  * `Version 2.0` (released *03.09.2015*)
    * Made `DataStore` and `DataSource` part of the public API
    * Provide a generic `JpaAnnotationDataStore` and a `DualDataStoreManager` which can handle *Model classes* with *JPA annotations*
    * Added support for *FunctionImports* via the `@EdmFunctionImport` annotation and the `FunctionExecutor` interface
    * First stable version based on fork of the *Apache Olingo Annotation Processor* (see [Olingo Homepage](http://olingo.apache.org/doc/odata2/tutorials/AnnotationProcessorExtension.html) and [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22olingo-odata2-annotation-processor%22)).

## Roadmap

  * Abstract Topics
    * Provide more Extension points for customization of generic (default) data processing (e.g. start/end of transaction). Beside the actual only possibility to use the ReadOptions and ReadResult for performance optimization. -> created enhancement [Janos#10](https://github.com/mibo/janos/issues/10)
    * Provide more documentation (e.g. tutorials) as just the source code
  * Specific enhancements
    * Support `ReadOptions` and `ReadResult` for `FunctionSource` (*FunctionImports*)
