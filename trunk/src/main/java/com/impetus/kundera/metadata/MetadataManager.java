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
package com.impetus.kundera.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.impetus.kundera.classreading.AnnotationDiscoveryListener;
import com.impetus.kundera.metadata.processor.ColumnFamilyProcessor;
import com.impetus.kundera.metadata.processor.IndexProcessor;
import com.impetus.kundera.metadata.processor.SuperColumnFamilyProcessor;

/**
 * Concrete implementation of IMetadataManager.
 * 
 * @author animesh.kumar
 */
public class MetadataManager implements AnnotationDiscoveryListener {

	/** the log used by this class. */
	private static Log log = LogFactory.getLog(MetadataManager.class);

	/** cache for Metadata. */
	private Map<Class<?>, EntityMetadata> metadataCache = new ConcurrentHashMap<Class<?>, EntityMetadata>();
	
	private Map<String, Class<?>> entityNameToClassMap = new ConcurrentHashMap<String, Class<?>>();

	private List<MetadataProcessor> metadataProcessors;

	/** The Validator. */
	private Validator validator;

	// intentionally unused!
	@SuppressWarnings("unused")
	private EntityManagerFactory factory;
	
	/**
	 * Instantiates a new metadata manager.
	 */
	public MetadataManager(EntityManagerFactory factory) {
		this.factory = factory; 
		
		validator = new ValidatorImpl();

		metadataProcessors = new ArrayList<MetadataProcessor>();
		
		// add processors to chain.
		metadataProcessors.add(new SuperColumnFamilyProcessor());
		metadataProcessors.add(new ColumnFamilyProcessor());
		metadataProcessors.add(new IndexProcessor());
	}

	public void validate(Class<?> clazz) throws PersistenceException {
		validator.validate(clazz);
	}

	public boolean isColumnFamily(Class<?> clazz) throws PersistenceException {
		return getEntityMetadata(clazz).getType().equals(EntityMetadata.Type.COLUMN_FAMILY);
	}

	public boolean isSuperColumnFamily(Class<?> clazz) throws PersistenceException {
		return getEntityMetadata(clazz).getType().equals(EntityMetadata.Type.SUPER_COLUMN_FAMILY);
	}

	public EntityMetadata getEntityMetadata(Class<?> clazz) throws PersistenceException {

		EntityMetadata metadata = metadataCache.get(clazz);
		if (null == metadata) {
			log.debug("Metadata not found in cache for " + clazz.getName());
			// double check locking.
			synchronized (clazz) {
				if (null == metadata) {
					metadata = process(clazz);
					cacheMetadata (clazz, metadata);
				}
			}
		}
		return metadata;
	}

	public EntityMetadata process(Class<?> clazz) throws PersistenceException {

		EntityMetadata metadata = new EntityMetadata(clazz);
		validate(clazz);
		
		log.debug("Processing @Entity: " + clazz);
		
		for (MetadataProcessor processor : metadataProcessors) {
			processor.process(clazz, metadata);
		}

		return metadata;
	}
	
	private void cacheMetadata (Class<?> clazz, EntityMetadata metadata) {
		metadataCache.put(clazz, metadata);
		
		// save name to class map.
		if (entityNameToClassMap.containsKey(clazz.getSimpleName())) { 
			throw new PersistenceException("Name conflict between classes " + entityNameToClassMap.get(clazz.getSimpleName()).getName() + " and " + clazz.getName());
		}
		entityNameToClassMap.put(clazz.getSimpleName(), clazz);
	}
	
	public Class<?> getEntityClassByName (String name) {
		return entityNameToClassMap.get(name);
	}

	@Override
	// called whenever a class with @Entity annotation is encountered in the classpath.
	public void discovered(String className, String[] annotations) {
		try {
			Class<?> clazz = Class.forName(className);
			
			// process for Metadata
			EntityMetadata metadata = process(clazz);
			cacheMetadata (clazz, metadata);
			log.info("Added @Entity " + clazz.getName());
		} catch (ClassNotFoundException e) {
			throw new PersistenceException(e.getMessage());
		}
	}
}
