## Features ##
There are multiple artifacts deployed to the moten-util maven repository (see below). These artifacts and/or svn source code collectively offer a number of features including:
  * [UML2 Class Diagram Generator for Eclipse 3.5](http://code.google.com/p/moten-util/wiki/UmlXmiGenerator)
  * [Oracle Ant tasks](http://code.google.com/p/moten-util/wiki/OracleAntTasks) (including `LoadJava` and `SqlPlus`)
  * kml 2.1 and 2.2 jaxb generated classes
  * jaxb generated classes for XMLSchema.xsd (xsd-jaxb)
  * SOWPODS word list lookup maven artifact
  * Mandelbrot fractal viewer and movie generator using `DoubleDouble` for extra precision
  * Sample maven project for [deploying scala apps to android](http://code.google.com/p/moten-util/source/browse/#svn%2Fandroid-scala-sample)

## Other projects ##
Other projects are kept in this same source repository including:

  * tv recorder (gwt web ui with mencoder scripts, customised for my use only)
  * tv schedule downloader (ozlist)
  * entity tracking system (dev)
  * fluent wrappers for guava collections, java equivalents of some scala collection methods and classes (guavax)
  * svg generation utility
  * other mucking around!

## Download ##
Non-maven users can get the jar from [here](http://moten-util.googlecode.com/svn/repo/org/moten/david/util/moten-util).

Maven users can add this project as a dependency with the following additions to a pom.xml file:
```
<dependencies>
  . . .
  <dependency>
    <groupId>org.moten.david.util</groupId>
    <artifactId>moten-util</artifactId>
    <version>0.0.6</version>
  </dependency>
  . . .
</dependencies>

<repositories>
  . . .
  <repository>
    <id>googlecode-moten-util</id>
    <url>http://moten-util.googlecode.com/svn/repo</url>       
  </repository>
  . . .
</repositories>


```

Kml dependencies:
```
<dependency>
  <groupId>org.moten.david.util.kml</groupId>
  <artifactId>kml-2-1<artifactId>
  <version>1.0.1</version>
</dependency>
or
<dependency>
  <groupId>org.moten.david.util.kml</groupId>
  <artifactId>kml-2-2<artifactId>
  <version>1.0.1</version>
</dependency>
```