/*
 * Copyright 2010 Impetus Infotech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.impetus.kundera.classreading;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The Class ClasspathReader.
 *
 * @author animesh.kumar
 */
public class ClasspathReader extends Reader {

    /**
     * The filter.
     */
    private Filter filter;
    private String basePackagetoScan;

    /**
     * Instantiates a new classpath reader.
     */
    public ClasspathReader() {
        filter = new FilterImpl();
    }

    public ClasspathReader(String basePackagetoScan) {
        this.basePackagetoScan = basePackagetoScan;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.impetus.kundera.classreading.Reader#read()
     */

    @Override
    public final void read() {
        URL[] resources = findResources();
        for (URL resource : resources) {
            try {
                ResourceIterator itr = getResourceIterator(resource, getFilter());

                InputStream is = null;
                while ((is = itr.next()) != null) {
                    scanClass(is);
                }
            } catch (IOException e) {
                // TODO: Do something with this exception
                e.printStackTrace();
            }
        }
    }

    /**
     * Uses the java.class.path system property to obtain a list of URLs that
     * represent the CLASSPATH
     *
     * @return the UR l[]
     */
    @SuppressWarnings("deprecation")
    public final URL[] findResourcesByClasspath() {
        List<URL> list = new ArrayList<URL>();
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);

        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();

            File fp = new File(path);
            if (!fp.exists())
                throw new RuntimeException("File in java.class.path does not exist: " + fp);
            try {
                list.add(fp.toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return list.toArray(new URL[list.size()]);
    }

    /**
     * Scan class resources into a basePackagetoScan path 
     *
     * @return list of class path included in the base package
     */
    public final URL[] findResourcesByContextLoader() {
        List<URL> list = new ArrayList<URL>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = basePackagetoScan.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                list.add(resource);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan base package", e);
        }

        return list.toArray(new URL[list.size()]);
    }


    @Override
    public URL[] findResources() {
        URL[] result = null;

        if (basePackagetoScan != null) {
             result = findResourcesByContextLoader();
        } else {
            result = findResourcesByClasspath();
        }

        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.impetus.kundera.classreading.Reader#getFilter()
     */

    public final Filter getFilter() {
        return filter;
    }


    /**
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public final void setFilter(Filter filter) {
        this.filter = filter;
    }
}
