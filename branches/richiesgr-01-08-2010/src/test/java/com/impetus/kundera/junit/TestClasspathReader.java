package com.impetus.kundera.junit;

import com.impetus.kundera.classreading.ClasspathReader;
import junit.framework.TestCase;

import java.net.URL;

/**
 * User: Richard Grossman
 * Date: Jul 31, 2010
 * Time: 4:03:00 PM
 */
public class TestClasspathReader extends TestCase {


    public void testClasspathDiscoverProperties() {
        ClasspathReader classpathDiscoverer = new ClasspathReader();
        URL[] res = classpathDiscoverer.findResources();

        assertEquals(36, res.length);
    }


    public void testClasspathDiscoverContextLoader() {
        ClasspathReader classpathDiscoverer = new ClasspathReader("com.impetus.kundera.testpack");
        URL[] res = classpathDiscoverer.findResources();

        assertEquals(1, res.length);
        assertEquals("file:/C:/Users/Butterfly/IdeaProjects/CassandraProjects/kundera/" +
                "target/test-classes/com/impetus/kundera/testpack", res[0].toString());
    }


}