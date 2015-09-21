# How to use Kundera with Spring #

## Introduction ##
We'll present here how to use Kundera with Spring (from 2.5). Spring + Hibernate is one of the most powerful combination of framework to access Database code with a minimum java code to write. Using Spring give also a very simple way to write unit testing for your database operation.

Of course Spring support JPA so the combination with Kundera works great.

## Components ##
  1. **Spring 3.0 Configuration: `applicationContext.xml`**
We have to define a minimal FactoryBean to be ready to work with Kundera. Because he is compliant with JPA interface Kundera is usable directly by the `LocalContainerEntityManagerFactoryBean`.

**Here is the Bean definition**
```
    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="persistenceUnitName" value="myPersistenceUnit"/>
    </bean>
```

The only property to provide is the persistence unit name that is defined above in the persistence.xml file.

Exactly like working with hibernate the Spring Container will manage for us the creation of the Kundera EntityManager via the `LocalContainerEntityManagerFactoryBean`.

  1. **JPA Configuration: `persistence.xml`**
In Kundera there is multiple way to define the persistence configuration like written in the article [Coding is an act of faith.](http://anismiles.wordpress.com/2010/07/14/kundera-now-jpa-1-0-compatible)
Here I choose to stick to strict JPA persistence declaration via the file persistence.xml that are located in your META-INF package directory. The best advantage here is to have a completely transparent configuration (without a single line of code !!)

**Here is the persistence.xml**
```
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"
    version="1.0">
    <persistence-unit name="myPersistenceUnit">
        <provider>com.impetus.kundera.ejb.KunderaPersistence</provider>
        <properties>
            <!-- 2nd level cache  -->
            <property name="kundera.nodes" value="localhost" />
            <property name="kundera.port" value="9160" />
            <property name="kundera.keyspace" value="Keyspace1" />
            <property name="sessionless" value="false" />
            <property name="kundera.client" value="com.impetus.kundera.client.PelopsClient" />
            <property name="kundera.annotations.scan.package" value="com.mypackage" />            
        </properties>
    </persistence-unit>
</persistence>
```

Here the important thing is to give the provider class in our case `com.impetus.kundera.ejb.KunderaPersistence`. the other property value will give connection parameters to cassandra.
The property `kundera.annotations.scan.package` will indicate wich package need to be scanned to search for JPA annotations.

  1. **Entity Java class**
Now we need to define our entity exactly like we do it with JPA for database but using the kundera annotations to add the extra info for Cassandra.

Here is a simple entity sample
```
package com.wix.model;

import com.impetus.kundera.api.ColumnFamily;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@ColumnFamily(keyspace = "Keyspace1", family = "SimpleComment")
@XmlRootElement(name = "SimpleComment")
public class SimpleComment {

    @Id
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "comment")
    private String commentText;

    public SimpleComment() {
    }

    ...... 
}

```

  1. **DAO Service using Kunera**
Now we have defined our entity we've to manipulate it we can simply use the `@PersistenceContext` Spring annotation to inject the persistence context automatically. Like you see there is no autowiring.

```
package com.wix.cassandra;

import com.wix.model.SimpleComment;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class KuneraService {
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public SimpleComment addComment(String id, String userId, String commentText) {
        SimpleComment simpleComment = new SimpleComment();
        simpleComment.setId(id);
        simpleComment.setUserId(userId);
        simpleComment.setCommentText(commentText);

        entityManager.persist(simpleComment);
        return simpleComment;
    }

    public SimpleComment getCommentById(String Id) {
        SimpleComment simpleComment = entityManager.find(SimpleComment.class, Id);
        return simpleComment;
    }

    public List<SimpleComment> getAllComments() {
        Query query = entityManager.createQuery("SELECT c from SimpleComment c");
        List<SimpleComment> list = query.getResultList();

        return list;
    }

}

```

**It's very important to declare** `@PersistenceContext(type = PersistenceContextType.EXTENDED)` to prevent spring to close the Cassandra client at each transaction. But now you need to manage to close the client connection by yourself

## Conclusion ##
We've see how it's easy to work with Spring + Kundera via JPA. With almost no code we've managed in Spring Container a whole set of Cassandra operation like create new Columns, multiple types of queries to cassandra via HQL language.