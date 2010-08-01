package com.impetus.kundera.testpack;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: Butterfly
 * Date: Jul 31, 2010
 * Time: 4:04:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class TestAnnotatedClass {

    @Column()
    private String aFieldWithAnnotation;

    @SuppressWarnings({"unchecked"})
    private String aMethodeWithAnnotation() {
        return null;
    }

}