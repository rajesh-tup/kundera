#Maven usage.

The best way of using kundera is to build from code by downloading it and using -
`mvn clean install`.

The problem is that Cassandra, Thrift and Pelops are still not available on a public library. So these libraries have added to kundera svn hosted maven repository. The other solution is to pick these from lib folder in kundera subversion which contains all the libraries required for building kundera and use system scope as dependency.

Any project using maven can easily use kundera by adding the following dependency-
```
		<dependency>
			<groupId>com.impetus.kundera</groupId>
			<artifactId>kundera</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
```

and repository entry-

```
	<repositories>
		<repository>
			<id>kundera-snapshot</id>
			<name>Kundera Snapshot Repository</name>
			<url>http://kundera.googlecode.com/svn/maven2/maven-repo-snapshot/</url>
			<releases>
				<enabled>false</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
		<repository>
			<id>kundera-release</id>
			<name>Kundera Release Repository</name>
			<url>http://kundera.googlecode.com/svn/maven2/maven-repo-release/</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
                <repository>
			<id>kundera-missing</id>
			<name>Kundera Public Missing Resources Repository</name>
			<url>http://kundera.googlecode.com/svn/maven2/maven-missing-resources/</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>
```

The only remaining thing is adding the maven artifacts not available in public repositories. The dependencies for these libraries are-
```
                <dependency>
			<groupId>org.apache.cassandra</groupId>
			<artifactId>apache-cassandra</artifactId>
			<version>0.6.3</version>
		</dependency>
		<dependency>
			<groupId>libthrift</groupId>
			<artifactId>libthrift</artifactId>
			<version>r917130</version>
		</dependency>
		<dependency>
			<groupId>pelops</groupId>
			<artifactId>pelops</artifactId>
			<version>UNKNOWN</version>
			</dependency>
		<!-- Required for running  tests  for embedded cassandra mode-->
		<dependency>
			<groupId>org.cliffc</groupId>
			<artifactId>high-scale-lib</artifactId>
			<version>UNKNOWN</version>
		</dependency>
		<dependency>
			<groupId>com.reardencommerce</groupId>
			<artifactId>clhm-production</artifactId>
			<version>UNKNOWN</version>
		</dependency>
		<dependency>
			<groupId>javassist</groupId>
			<artifactId>javassist</artifactId>
			<version>3.9.0.GA</version>
		</dependency>
		<dependency>
			<groupId>lucandra</groupId>
			<artifactId>lucandra</artifactId>
			<version>UNKNOWN</version>
		</dependency>
```