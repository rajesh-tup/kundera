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
package com.impetus.kundera.db.accessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.impetus.kundera.CassandraClient;
import com.impetus.kundera.Constants;
import com.impetus.kundera.db.DataAccessor;
import com.impetus.kundera.metadata.EntityMetadata;
import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.property.PropertyAccessorFactory;

/**
 * The Class SuperColumnDataAccessor.
 * 
 * @author animesh.kumar
 */
public final class SuperColumnFamilyDataAccessor extends BaseDataAccessor<SuperColumn> implements DataAccessor {

    /** The Constant log. */
    private static final Log log = LogFactory.getLog(SuperColumnFamilyDataAccessor.class);

    /*
     * (non-Javadoc)
     * 
     * @seecom.impetus.kundera.db.DataAccessor#delete(com.impetus.kundera.
     * CassandraClient, com.impetus.kundera.metadata.EntityMetadata,
     * java.lang.String)
     */
    @Override
    public void delete(CassandraClient client, EntityMetadata metadata, String key) throws Exception {
        log.debug("Deleting from cassandra @Entity[" + metadata.getEntityClazz().getName() + "] for key:" + key);
        client.delete(metadata.getColumnFamilyName(), key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.impetus.kundera.db.DataAccessor#read(com.impetus.kundera.CassandraClient
     * , com.impetus.kundera.metadata.EntityMetadata, java.lang.Class,
     * java.lang.String)
     */
    @Override
    public <C> C read(CassandraClient client, EntityMetadata metadata, Class<C> clazz, String key) throws Exception {
        log.debug("Reading from cassandra @Entity[" + clazz.getName() + "] for key:" + key);
        List<SuperColumn> superColumns = client.loadSuperColumns(metadata.getColumnFamilyName(), key, metadata.getSuperColumnFieldNames().toArray(new String[0]));
        if (null == superColumns || superColumns.size() == 0) {
            throw new PersistenceException("Entity not found for key: " + key);
        }

        BaseDataAccessor<SuperColumn>.CassandraRow tf = this.new CassandraRow(key, metadata.getColumnFamilyName(), superColumns);
        return cassandraRowToEntity(clazz, metadata, tf);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.impetus.kundera.db.DataAccessor#read(com.impetus.kundera.CassandraClient
     * , com.impetus.kundera.metadata.EntityMetadata, java.lang.Class,
     * java.lang.String[])
     */
    @Override
    public <C> List<C> read(CassandraClient client, EntityMetadata metadata, Class<C> clazz, String... keys) throws Exception {
        log.debug("Reading from cassandra @Entity[" + clazz.getName() + "] for keys:" + Arrays.asList(keys));
        List<C> entities = new ArrayList<C>();

        Map<String, List<SuperColumn>> map = client.loadSuperColumns(metadata.getColumnFamilyName(), keys);
        for (Map.Entry<String, List<SuperColumn>> entry : map.entrySet()) {
            if (entry.getValue().size() == 0) {
                continue;
            }

            BaseDataAccessor<SuperColumn>.CassandraRow tf = this.new CassandraRow(entry.getKey(), metadata.getColumnFamilyName(), entry.getValue());
            entities.add(cassandraRowToEntity(clazz, metadata, tf));
        }
        return entities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.impetus.kundera.db.DataAccessor#write(com.impetus.kundera.CassandraClient
     * , com.impetus.kundera.metadata.EntityMetadata, java.lang.Object)
     */
    @Override
    public void write(CassandraClient client, EntityMetadata metadata, Object object) throws Exception {
        log.debug("Writing to cassandra @Entity[" + object.getClass().getName() + "] " + object);
        BaseDataAccessor<SuperColumn>.CassandraRow tf = entityToCassandraRow(metadata, object);
        client.writeSuperColumns(tf.getColumnFamilyName(), // columnFamily
                tf.getKey(), // row id
                tf.getColumns().toArray(new SuperColumn[0]) // list of
                // supercolumns
                );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.impetus.kundera.db.accessor.BaseDataAccessor#cassandraRowToEntity
     * (java.lang.Class, com.impetus.kundera.metadata.EntityMetadata,
     * com.impetus.kundera.db.accessor.BaseDataAccessor.CassandraRow)
     */
    @Override
    protected <C> C cassandraRowToEntity(Class<C> clazz, EntityMetadata metadata, BaseDataAccessor<SuperColumn>.CassandraRow cassandraRow) throws Exception {

        // instantiate a new instance
        C target = clazz.newInstance();

        // set row-key. Note: @Id is always String.
        PropertyAccessorFactory.setStringProperty(target, metadata.getIdProperty(), cassandraRow.getKey());

        // get a name->field map for super-columns
        Map<String, Field> columnNameToFieldMap = new HashMap<String, Field>();
        for (Map.Entry<String, EntityMetadata.SuperColumn> entry : metadata.getSuperColumnsMap().entrySet()) {
            for (EntityMetadata.Column cMetadata : entry.getValue().getColumns()) {
                columnNameToFieldMap.put(cMetadata.getName(), cMetadata.getField());
            }
        }

        for (SuperColumn sc : cassandraRow.getColumns()) {

            for (Column column : sc.getColumns()) {
                String name = PropertyAccessorFactory.STRING.fromBytes(column.getName());
                byte[] value = column.getValue();

                String colName = name.split(Constants.SEPARATOR)[0];

                // set value of the field in the bean
                try {
                    if (null == value) {
                        log.debug("Null value for name: " + name);
                        continue;
                    }

                    PropertyAccessorFactory.setProperty(target, columnNameToFieldMap.get(colName), value, colName, name);
                } catch (Exception e) {
                    log.error(e);
                    e.printStackTrace();
                }
            }
        }
        return target;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.impetus.kundera.db.accessor.BaseDataAccessor#entityToCassandraRow
     * (com.impetus.kundera.metadata.EntityMetadata, java.lang.Object)
     */
    @Override
    protected BaseDataAccessor<SuperColumn>.CassandraRow entityToCassandraRow(EntityMetadata metadata, Object bean) throws Exception {

        // timestamp to use in thrift column objects
        long timestamp = System.currentTimeMillis();

        BaseDataAccessor<SuperColumn>.CassandraRow cassandraRow = this.new CassandraRow();

        // set column-family name
        cassandraRow.setColumnFamilyName(metadata.getColumnFamilyName());

        // set row key
        cassandraRow.setKey(PropertyAccessorFactory.getStringProperty(bean, metadata.getIdProperty()));

        for (EntityMetadata.SuperColumn superColumn : metadata.getSuperColumnsAsList()) {
            String superColumnName = superColumn.getName();

            List<Column> columns = new ArrayList<Column>();

            for (EntityMetadata.Column column : superColumn.getColumns()) {
                Field field = column.getField();
                String name = column.getName();

                Map<String, byte[]> map = PropertyAccessorFactory.getPropertyAccessor(field).readAsByteArray(bean, field, name);

                for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                    try {
                        columns.add(new Column(PropertyAccessorFactory.STRING.toBytes(entry.getKey()), entry.getValue(), timestamp));
                    } catch (PropertyAccessException e) {
                        log.warn("Property: " + field.getName() + " of Class " + bean.getClass().getName() + " could not be set.", e);
                    }
                }
            }

            cassandraRow.addColumn(new SuperColumn(PropertyAccessorFactory.STRING.toBytes(superColumnName), columns));
        }

        return cassandraRow;
    }

}
