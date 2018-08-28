package api.dao;

import api.main.AppEnvironment;
import api.tools.FileReaderHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Leonardo Lana
 * Github: https://github.com/leonardodlana
 * <p>
 * Copyright 2018 Leonardo Lana
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public abstract class AbstractDAO<T> implements DAO<T> {

    private static final String URL = "url";
    private static final String DRIVER = "driver";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String MIN_IDLE = "minIdle";
    private static final String MAX_IDLE = "maxIdle";
    private static final String MAX_ACTIVE = "maxActive";
    private static final String MAX_WAIT = "maxWait";
    private static final String INITIAL_SIZE = "initialSize";
    private static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
    private static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
    private static final String VALIDATION_QUERY = "validationQuery";
    private static final String VALIDATION_INTERVAL = "validationInterval";
    private static final String TEST_ON_BORROW = "testOnBorrow";
    private static final String REMOVE_ABANDONED = "removeAbandoned";
    private static final String REMOVE_ABANDONED_TIME = "removeAbandonedTimeout";

    private final AppEnvironment mEnvironment;
    private DataSource mDataSource;
    private boolean mExists;

    protected abstract String[] getCreateTableSql();

    public AbstractDAO() {
        mEnvironment = AppEnvironment.current();
        Properties props = loadProperties();
        setDefaults(props);
        initializeDataSource(props);
        initializeDataBaseIfNecessary();
    }

    protected void initializeDataSource(Properties props) {
        if (mDataSource != null)
            return;

        BasicDataSource ds;
        ds = new BasicDataSource();
        ds.setUrl(props.getProperty(URL));
        ds.setDriverClassName(props.getProperty(DRIVER));

        if (props.containsKey(INITIAL_SIZE))
            ds.setInitialSize(Integer.parseInt(props.getProperty(INITIAL_SIZE)));

        if (props.containsKey(MAX_WAIT))
            ds.setMaxWaitMillis(Long.parseLong(props.getProperty(MAX_WAIT)));

        if (props.containsKey(MAX_ACTIVE))
            ds.setMaxTotal(Integer.parseInt(props.getProperty(MAX_ACTIVE)));

        if (props.containsKey(MAX_IDLE))
            ds.setMaxIdle(Integer.parseInt(props.getProperty(MAX_IDLE)));

        if (props.containsKey(MIN_IDLE))
            ds.setMinIdle(Integer.parseInt(props.getProperty(MIN_IDLE)));

        if (props.containsKey(TIME_BETWEEN_EVICTION_RUNS_MILLIS))
            ds.setTimeBetweenEvictionRunsMillis(Long.parseLong(props.getProperty(TIME_BETWEEN_EVICTION_RUNS_MILLIS)));

        if (props.containsKey(MIN_EVICTABLE_IDLE_TIME_MILLIS))
            ds.setMinEvictableIdleTimeMillis(Long.parseLong(props.getProperty(MIN_EVICTABLE_IDLE_TIME_MILLIS)));

        if (props.containsKey(VALIDATION_QUERY))
            ds.setValidationQuery(props.getProperty(VALIDATION_QUERY));

        if (props.containsKey(VALIDATION_INTERVAL))
            ds.setValidationQueryTimeout(Integer.parseInt(props.getProperty(VALIDATION_INTERVAL)));

        if (props.containsKey(TEST_ON_BORROW))
            ds.setTestOnBorrow(Boolean.parseBoolean(props.getProperty(TEST_ON_BORROW)));

        if (props.containsKey(REMOVE_ABANDONED))
            ds.setTestOnBorrow(Boolean.parseBoolean(props.getProperty(REMOVE_ABANDONED)));

        if (props.containsKey(REMOVE_ABANDONED_TIME))
            ds.setValidationQueryTimeout(Integer.parseInt(props.getProperty(REMOVE_ABANDONED_TIME)));

        if (props.containsKey(USER))
            ds.setUsername(props.getProperty(USER));

        if (props.containsKey(PASSWORD))
            ds.setPassword(props.getProperty(PASSWORD));

        try {
            Class.forName(ds.getDriverClassName());
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        mDataSource = ds;

        System.out.println("Datasource from apache");
    }

    protected void setDefaults(Properties result) {

        if (mEnvironment == AppEnvironment.PROD) {
            result.setProperty(DRIVER, "com.mysql.jdbc.Driver");
            result.setProperty(URL, "jdbc:mysql://127.0.0.1:3306/?allowPublicKeyRetrieval=true&useSSL=false");
            result.setProperty(USER, "pop_picture");
            result.setProperty(PASSWORD, "PopPicture182@"); // only local machine can access ;)
            return;
        }

        if (mEnvironment == AppEnvironment.DEV) {
            result.setProperty(DRIVER, "com.mysql.jdbc.Driver");
            result.setProperty(URL, "jdbc:mysql://127.0.0.1:3306/");
            result.setProperty(USER, "poppicture");
            result.setProperty(PASSWORD, "poppicture");
            return;
        }

        result.setProperty(DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
    }

    protected JdbcTemplate newJdbcTemplate() {
        JdbcTemplate create = instantiateJdbcTemplate();

        try {
            create.update("USE pop_picture");
            if (mExists)
                return create;

            create.update("CREATE DATABASE IF NOT EXISTS pop_picture");
            create.update("USE pop_picture");
        } catch (Throwable ex) {
            /* ignore */
            System.out.println(ex.toString());
        }
        return create;
    }

    protected JdbcTemplate instantiateJdbcTemplate() {
        return new JdbcTemplate(mDataSource);
    }

    protected final void initializeDataBaseIfNecessary() {
        if (mExists)
            return;

        JdbcTemplate create = newJdbcTemplate();
        String[] tables = getCreateTableSql();
        for (String table : tables)
            try {
                create.execute(table);

            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }

        mExists = true;
    }

    protected final Properties loadProperties() {
        InputStream in = null;
        Properties result = new Properties();
        try {
            setDefaults(result);
            if (mEnvironment != AppEnvironment.PROD)
                return result;

            in = new FileReaderHelper().readFileAsStream("dao.properties");
            result.load(in);

            return result;
        } catch (Throwable ex) {
            System.out.println("Error while loading properties 99");
            return result;

        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}