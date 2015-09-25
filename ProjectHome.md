# How to use it? #

## For maven projects ##
  * Add to your pom.xml:

```
<repositories>
    <repository>
        <id>generic-spatial-dao</id>
	<url>http://generic-spatial-dao.googlecode.com/svn/maven/repository/</url>
    </repository>
</repositories>
```
and
```
<dependency>
    <groupId>com.googlecode</groupId>
    <artifactId>generic-spatial-dao</artifactId>
    <version>2.3.0</version>
    <scope>compile</scope>
</dependency>
```

  * Create a file META-INF/persistence.xml in the resources folder of your project.

## For other projects ##
  * Download the last version of the jar-with-dependencies at http://code.google.com/p/generic-spatial-dao/source/browse/maven/repository/com/googlecode/generic-spatial-dao/2.3.0/generic-spatial-dao-2.3.0-jar-with-dependencies.jar;
  * Add the jar to the classpath;
  * Create a file META-INF/persistence.xml in the resources folder of your project.

# How to build it? #
  * Edit the file "build.properties" in folder generic-spatial-dao/properties. A more customizable option is to create a new file named "`<user`>.properties" (in `<user`> put your username) in that same folder and replace the different properties from build.properties as well as the file joaosavio.properties. This approach allows that different users can have custom builds;
  * Run "mvn" for default build or "mvn -P ci" for a build with reports.