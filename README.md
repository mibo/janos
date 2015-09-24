# Janos - Java Annotation extension for Apache Olingo V2

This extension provides *Java Annotations* for defining the OData (V2) EDM and Interfaces (Hooks) for attaching generic data sources providing the data.
The extension is based (forked) from the *Apache Olingo Annotation Processor* (see [Olingo Homepage](http://olingo.apache.org/doc/odata2/tutorials/AnnotationProcessorExtension.html) and [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22olingo-odata2-annotation-processor%22)).


## Current Features <small>(of stable version)</small>

  * Create your *EDM (Entity Data Model)* with using *Java Annotations* (the *EDM-(Java)-Annotations* from the [*Apache Olingo OData Library (v2)*](http://olingo.apache.org/doc/odata2/index.html) (or in [maven central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22olingo-odata2-api-annotation%22)))
  * Data Access Layer with...
    * ...`DataSource` for providing the *Data* from the view of an *OData Service*
    * ...`DataStore` for providing the *Data* from the view of an *Data Storage Service (e.g. a Database)*
  * Generic `DataSource` and `DataStore` implementations for...
    * ...using the *EDM-(Java)-Annotations* in combination with an *In-Memory-Store (none-persistent)*
    * ...using the *EDM-(Java)-Annotations* in combination with an *JPA-Based-Store*

## New and Noteworthy

  * `Version 2.1` (upcoming)
    * Added `ReadOptions` and `ReadResult` for optimization of data access (read) in the `DataStore` layer
    * Refactored API packages (incompatible changes to `2.0`)
  * `Version 2.0` (current stable version)
    * Made `DataStore` and `DataSource` part of the public API
    * Provide a generic `JpaAnnotationDataStore` and a `DualDataStoreManager` which can handle *Model classes* with *JPA annotations*
    * Added support for *FunctionImports* via the `@EdmFunctionImport` annotation and the `FunctionExecutor` interface
    * First stable version based on fork of the *Apache Olingo Annotation Processor* (see [Olingo Homepage](http://olingo.apache.org/doc/odata2/tutorials/AnnotationProcessorExtension.html) and [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22olingo-odata2-annotation-processor%22)).

## Roadmap

  * Abstract Topics
    * Provide more *Hooks* for customization of generic (default) data processing (e.g. *start/end of transaction*)
    * Provide more documentation (e.g. tutorials) as just the source code
  * Specific enhancements
    * Support `ReadOptions` and `ReadResult` for `FunctionSource` (*FunctionImports*)
