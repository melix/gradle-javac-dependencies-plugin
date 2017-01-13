# Compile-time dependencies finder

This is an experimental plugin for `javac` which analyzes the sources being
compiled and generates an output file containing the list of dependencies
for each file. The dependencies are meant in terms of class dependencies.

# Building

`./gradlew jar`

# Testing

This requires JDK 8+.

```
javac -Xplugin:ClassDependenciesPlugin -processorpath $PLUGIN_HOME/build/libs/gradle-javac-dependencies-plugin-0.1.0.jar *.java
```

This will generate an `analysis.txt` file containing, for each compiled class, its dependencies:

```
$ cat analysis.txt
A:java.util.List,I,I2,java.util.ArrayList
B:C
C:I2
I2:I
```

It is possible to set the path to the generated analysis file using plugin arguments:


```
javac -Xplugin:"ClassDependenciesPlugin build/foo/txt" -processorpath $PLUGIN_HOME/build/libs/g$
```
