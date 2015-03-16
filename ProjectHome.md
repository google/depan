# DepAn: Dependency visualization and analysis #

DepAn is a direct manipulation tool for visualization, analysis, and refactoring of dependencies in large applications.

### Releases ###

The 05-Oct-2009 release of DepAn is available for [download](http://google-depan.googlecode.com/files/DepAn_linux.gtk_20091005.zip).
This release should run on most Linux/GTK platforms that run Eclipse with Java 1.6.
Features and capabilities are covered in our [Release Notes](http://docs.google.com/Doc?docid=0AZqpPv-oAWxJZGdkcDdoM2NfMjBnZmJ0cGZnNQ).

If your engineering workstation does not meet these standards,
you will need to build your own version.
At a minimum, this requires the Eclipse Plugin Development Tools (PDT).
The [Engineering Advice](http://docs.google.com/Doc?id=dgdp7h3c_4f6mpmfhc) page should help you configure your build environment.

We welcome pre-packaged, downloadable versions for other platforms (Windows, anyone?).

### Features ###

  * Direct manipulation of heterogeneous dependency information in an Eclipse RCP environment.
  * Analysis and visualization of very large applications.
  * For Java, dependency discovery at the class member level.
  * Import of FileSystems as source of dependency information.
  * Collapse child dependency into parent entities to reveal class level interactions.
  * Selection of nodes by type, edge-count, and paths.

### Documentation ###

  * [DepAn Users Guide](http://docs.google.com/Doc?id=dgdp7h3c_8gdfm86fz):  Still pretty primitive.
  * Use cases:
    * [Are there any uses of a specific class?](http://docs.google.com/Doc?id=dgdp7h3c_9dcdxcdf5)
  * [EngineeringAdvice](http://docs.google.com/Doc?id=dgdp7h3c_4f6mpmfhc): How to build your own DepAn.

### Contributions ###

Contributions of all types: new analysis forms, new languages, new dependency models, and especially bug fixes are welcome.  If you're interested, the [Engineering Advice](http://docs.google.com/Doc?id=dgdp7h3c_4f6mpmfhc) page should help you get started.  Please note that all contributions are [code reviewed](http://docs.google.com/View?docid=dgdp7h3c_6gcrb65fg) before they are integrated into the trunk.