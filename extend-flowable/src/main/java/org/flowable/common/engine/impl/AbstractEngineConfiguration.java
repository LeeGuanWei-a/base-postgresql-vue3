package org.flowable.common.engine.impl;

/**
 * @description TODO (/适配人大金仓)
 * @author lgw
 * @date 2024/12/11
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.ArrayTypeHandler;
import org.apache.ibatis.type.BigDecimalTypeHandler;
import org.apache.ibatis.type.BlobInputStreamTypeHandler;
import org.apache.ibatis.type.BlobTypeHandler;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.ByteTypeHandler;
import org.apache.ibatis.type.ClobTypeHandler;
import org.apache.ibatis.type.DateOnlyTypeHandler;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.DoubleTypeHandler;
import org.apache.ibatis.type.FloatTypeHandler;
import org.apache.ibatis.type.IntegerTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LongTypeHandler;
import org.apache.ibatis.type.NClobTypeHandler;
import org.apache.ibatis.type.NStringTypeHandler;
import org.apache.ibatis.type.ShortTypeHandler;
import org.apache.ibatis.type.SqlxmlTypeHandler;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TimeOnlyTypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.engine.EngineLifecycleListener;
import org.flowable.common.engine.impl.agenda.AgendaOperationRunner;
import org.flowable.common.engine.impl.cfg.CommandExecutorImpl;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.common.engine.impl.cfg.TransactionContextFactory;
import org.flowable.common.engine.impl.cfg.standalone.StandaloneMybatisTransactionContextFactory;
import org.flowable.common.engine.impl.db.CommonDbSchemaManager;
import org.flowable.common.engine.impl.db.DbSqlSessionFactory;
import org.flowable.common.engine.impl.db.LogSqlExecutionTimePlugin;
import org.flowable.common.engine.impl.db.MybatisTypeAliasConfigurator;
import org.flowable.common.engine.impl.db.MybatisTypeHandlerConfigurator;
import org.flowable.common.engine.impl.db.SchemaManager;
import org.flowable.common.engine.impl.event.EventDispatchAction;
import org.flowable.common.engine.impl.event.FlowableEventDispatcherImpl;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandConfig;
import org.flowable.common.engine.impl.interceptor.CommandContextFactory;
import org.flowable.common.engine.impl.interceptor.CommandContextInterceptor;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.common.engine.impl.interceptor.CommandInterceptor;
import org.flowable.common.engine.impl.interceptor.CrDbRetryInterceptor;
import org.flowable.common.engine.impl.interceptor.DefaultCommandInvoker;
import org.flowable.common.engine.impl.interceptor.LogInterceptor;
import org.flowable.common.engine.impl.interceptor.SessionFactory;
import org.flowable.common.engine.impl.interceptor.TransactionContextInterceptor;
import org.flowable.common.engine.impl.lock.LockManager;
import org.flowable.common.engine.impl.lock.LockManagerImpl;
import org.flowable.common.engine.impl.logging.LoggingListener;
import org.flowable.common.engine.impl.logging.LoggingSession;
import org.flowable.common.engine.impl.logging.LoggingSessionFactory;
import org.flowable.common.engine.impl.persistence.GenericManagerFactory;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.common.engine.impl.persistence.cache.EntityCache;
import org.flowable.common.engine.impl.persistence.cache.EntityCacheImpl;
import org.flowable.common.engine.impl.persistence.entity.ByteArrayEntityManager;
import org.flowable.common.engine.impl.persistence.entity.ByteArrayEntityManagerImpl;
import org.flowable.common.engine.impl.persistence.entity.Entity;
import org.flowable.common.engine.impl.persistence.entity.PropertyEntityManager;
import org.flowable.common.engine.impl.persistence.entity.PropertyEntityManagerImpl;
import org.flowable.common.engine.impl.persistence.entity.TableDataManager;
import org.flowable.common.engine.impl.persistence.entity.TableDataManagerImpl;
import org.flowable.common.engine.impl.persistence.entity.data.ByteArrayDataManager;
import org.flowable.common.engine.impl.persistence.entity.data.PropertyDataManager;
import org.flowable.common.engine.impl.persistence.entity.data.impl.MybatisByteArrayDataManager;
import org.flowable.common.engine.impl.persistence.entity.data.impl.MybatisPropertyDataManager;
import org.flowable.common.engine.impl.runtime.Clock;
import org.flowable.common.engine.impl.service.CommonEngineServiceImpl;
import org.flowable.common.engine.impl.util.DefaultClockImpl;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.common.engine.impl.util.ReflectUtil;
import org.flowable.eventregistry.api.EventRegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEngineConfiguration {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String NO_TENANT_ID = "";
    public static final String DB_SCHEMA_UPDATE_FALSE = "false";
    public static final String DB_SCHEMA_UPDATE_CREATE = "create";
    public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";
    public static final String DB_SCHEMA_UPDATE_DROP_CREATE = "drop-create";
    public static final String DB_SCHEMA_UPDATE_TRUE = "true";
    protected boolean forceCloseMybatisConnectionPool = true;
    protected String databaseType;
    protected String jdbcDriver = "org.h2.Driver";
    protected String jdbcUrl = "jdbc:h2:tcp://localhost/~/flowable";
    protected String jdbcUsername = "sa";
    protected String jdbcPassword = "";
    protected String dataSourceJndiName;
    protected int jdbcMaxActiveConnections = 16;
    protected int jdbcMaxIdleConnections = 8;
    protected int jdbcMaxCheckoutTime;
    protected int jdbcMaxWaitTime;
    protected boolean jdbcPingEnabled;
    protected String jdbcPingQuery;
    protected int jdbcPingConnectionNotUsedFor;
    protected int jdbcDefaultTransactionIsolationLevel;
    protected DataSource dataSource;
    protected SchemaManager commonSchemaManager;
    protected SchemaManager schemaManager;
    protected Command<Void> schemaManagementCmd;
    protected String databaseSchemaUpdate = "false";
    protected boolean useLockForDatabaseSchemaUpdate = false;
    protected String xmlEncoding = "UTF-8";
    protected CommandExecutor commandExecutor;
    protected Collection<? extends CommandInterceptor> defaultCommandInterceptors;
    protected CommandConfig defaultCommandConfig;
    protected CommandConfig schemaCommandConfig;
    protected CommandContextFactory commandContextFactory;
    protected CommandInterceptor commandInvoker;
    protected AgendaOperationRunner agendaOperationRunner = (commandContext, runnable) -> runnable.run();
    protected List<CommandInterceptor> customPreCommandInterceptors;
    protected List<CommandInterceptor> customPostCommandInterceptors;
    protected List<CommandInterceptor> commandInterceptors;
    protected Map<String, AbstractEngineConfiguration> engineConfigurations = new HashMap();
    protected Map<String, AbstractServiceConfiguration> serviceConfigurations = new HashMap();
    protected ClassLoader classLoader;
    protected boolean useClassForNameClassLoading = true;
    protected List<EngineLifecycleListener> engineLifecycleListeners;
    protected Map<String, EventRegistryEventConsumer> eventRegistryEventConsumers = new HashMap();
    protected boolean isDbHistoryUsed = true;
    protected DbSqlSessionFactory dbSqlSessionFactory;
    protected SqlSessionFactory sqlSessionFactory;
    protected TransactionFactory transactionFactory;
    protected TransactionContextFactory transactionContextFactory;
    protected boolean isBulkInsertEnabled = true;
    protected int maxNrOfStatementsInBulkInsert = 100;
    public int DEFAULT_MAX_NR_OF_STATEMENTS_BULK_INSERT_SQL_SERVER = 55;
    protected String mybatisMappingFile;
    protected Set<Class<?>> customMybatisMappers;
    protected Set<String> customMybatisXMLMappers;
    protected List<Interceptor> customMybatisInterceptors;
    protected Set<String> dependentEngineMyBatisXmlMappers;
    protected List<MybatisTypeAliasConfigurator> dependentEngineMybatisTypeAliasConfigs;
    protected List<MybatisTypeHandlerConfigurator> dependentEngineMybatisTypeHandlerConfigs;
    protected List<SessionFactory> customSessionFactories;
    protected Map<Class<?>, SessionFactory> sessionFactories;
    protected boolean enableEventDispatcher = true;
    protected FlowableEventDispatcher eventDispatcher;
    protected List<FlowableEventListener> eventListeners;
    protected Map<String, List<FlowableEventListener>> typedEventListeners;
    protected List<EventDispatchAction> additionalEventDispatchActions;
    protected LoggingListener loggingListener;
    protected boolean transactionsExternallyManaged;
    protected boolean usingRelationalDatabase = true;
    protected boolean usingSchemaMgmt = true;
    protected String databaseTablePrefix = "";
    protected String databaseWildcardEscapeCharacter;
    protected String databaseCatalog = "";
    protected String databaseSchema;
    protected boolean tablePrefixIsSchema;
    protected boolean alwaysLookupLatestDefinitionVersion;
    protected boolean fallbackToDefaultTenant;
    protected DefaultTenantProvider defaultTenantProvider = (tenantId, scope, scopeKey) -> "";
    protected boolean enableLogSqlExecutionTime;
    protected Properties databaseTypeMappings = getDefaultDatabaseTypeMappings();
    protected Duration lockPollRate = Duration.ofSeconds(10L);
    protected Duration schemaLockWaitTime = Duration.ofMinutes(5L);
    protected PropertyDataManager propertyDataManager;
    protected ByteArrayDataManager byteArrayDataManager;
    protected TableDataManager tableDataManager;
    protected PropertyEntityManager propertyEntityManager;
    protected ByteArrayEntityManager byteArrayEntityManager;
    protected List<EngineDeployer> customPreDeployers;
    protected List<EngineDeployer> customPostDeployers;
    protected List<EngineDeployer> deployers;
    protected boolean enableConfiguratorServiceLoader = true;
    protected List<EngineConfigurator> configurators;
    protected List<EngineConfigurator> allConfigurators;
    protected EngineConfigurator idmEngineConfigurator;
    protected EngineConfigurator eventRegistryConfigurator;
    public static final String PRODUCT_NAME_POSTGRES = "PostgreSQL";
    public static final String PRODUCT_NAME_CRDB = "CockroachDB";
    public static final String DATABASE_TYPE_H2 = "h2";
    public static final String DATABASE_TYPE_HSQL = "hsql";
    public static final String DATABASE_TYPE_MYSQL = "mysql";
    public static final String DATABASE_TYPE_ORACLE = "oracle";
    public static final String DATABASE_TYPE_POSTGRES = "postgres";
    public static final String DATABASE_TYPE_MSSQL = "mssql";
    public static final String DATABASE_TYPE_DB2 = "db2";
    public static final String DATABASE_TYPE_COCKROACHDB = "cockroachdb";
    protected Map<Object, Object> beans;
    protected IdGenerator idGenerator;
    protected boolean usePrefixId;
    protected Clock clock;
    protected ObjectMapper objectMapper;
    public static final int DEFAULT_GENERIC_MAX_LENGTH_STRING = 4000;
    public static final int DEFAULT_ORACLE_MAX_LENGTH_STRING = 2000;
    protected int maxLengthStringVariableType = -1;

    public AbstractEngineConfiguration() {
    }

    public static Properties getDefaultDatabaseTypeMappings() {
        Properties databaseTypeMappings = new Properties();
        databaseTypeMappings.setProperty("KingbaseES", "postgres");
        databaseTypeMappings.setProperty("H2", "h2");
        databaseTypeMappings.setProperty("HSQL Database Engine", "hsql");
        databaseTypeMappings.setProperty("MySQL", "mysql");
        databaseTypeMappings.setProperty("MariaDB", "mysql");
        databaseTypeMappings.setProperty("Oracle", "oracle");
        databaseTypeMappings.setProperty("PostgreSQL", "postgres");
        databaseTypeMappings.setProperty("Microsoft SQL Server", "mssql");
        databaseTypeMappings.setProperty("db2", "db2");
        databaseTypeMappings.setProperty("DB2", "db2");
        databaseTypeMappings.setProperty("DB2/NT", "db2");
        databaseTypeMappings.setProperty("DB2/NT64", "db2");
        databaseTypeMappings.setProperty("DB2 UDP", "db2");
        databaseTypeMappings.setProperty("DB2/LINUX", "db2");
        databaseTypeMappings.setProperty("DB2/LINUX390", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXX8664", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXZ64", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXPPC64", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXPPC64LE", "db2");
        databaseTypeMappings.setProperty("DB2/400 SQL", "db2");
        databaseTypeMappings.setProperty("DB2/6000", "db2");
        databaseTypeMappings.setProperty("DB2 UDB iSeries", "db2");
        databaseTypeMappings.setProperty("DB2/AIX64", "db2");
        databaseTypeMappings.setProperty("DB2/HPUX", "db2");
        databaseTypeMappings.setProperty("DB2/HP64", "db2");
        databaseTypeMappings.setProperty("DB2/SUN", "db2");
        databaseTypeMappings.setProperty("DB2/SUN64", "db2");
        databaseTypeMappings.setProperty("DB2/PTX", "db2");
        databaseTypeMappings.setProperty("DB2/2", "db2");
        databaseTypeMappings.setProperty("DB2 UDB AS400", "db2");
        databaseTypeMappings.setProperty("CockroachDB", "cockroachdb");
        return databaseTypeMappings;
    }

    protected void initEngineConfigurations() {
        this.addEngineConfiguration(this.getEngineCfgKey(), this.getEngineScopeType(), this);
    }

    protected void initDataSource() {
        if (this.dataSource == null) {
            if (this.dataSourceJndiName != null) {
                try {
                    this.dataSource = (DataSource)(new InitialContext()).lookup(this.dataSourceJndiName);
                } catch (Exception e) {
                    throw new FlowableException("couldn't lookup datasource from " + this.dataSourceJndiName + ": " + e.getMessage(), e);
                }
            } else if (this.jdbcUrl != null) {
                if (this.jdbcDriver == null || this.jdbcUsername == null) {
                    throw new FlowableException("DataSource or JDBC properties have to be specified in a process engine configuration");
                }

                this.logger.debug("initializing datasource to db: {}", this.jdbcUrl);
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Configuring Datasource with following properties (omitted password for security)");
                    this.logger.info("datasource driver : {}", this.jdbcDriver);
                    this.logger.info("datasource url : {}", this.jdbcUrl);
                    this.logger.info("datasource user name : {}", this.jdbcUsername);
                }

                PooledDataSource pooledDataSource = new PooledDataSource(this.getClass().getClassLoader(), this.jdbcDriver, this.jdbcUrl, this.jdbcUsername, this.jdbcPassword);
                if (this.jdbcMaxActiveConnections > 0) {
                    pooledDataSource.setPoolMaximumActiveConnections(this.jdbcMaxActiveConnections);
                }

                if (this.jdbcMaxIdleConnections > 0) {
                    pooledDataSource.setPoolMaximumIdleConnections(this.jdbcMaxIdleConnections);
                }

                if (this.jdbcMaxCheckoutTime > 0) {
                    pooledDataSource.setPoolMaximumCheckoutTime(this.jdbcMaxCheckoutTime);
                }

                if (this.jdbcMaxWaitTime > 0) {
                    pooledDataSource.setPoolTimeToWait(this.jdbcMaxWaitTime);
                }

                if (this.jdbcPingEnabled) {
                    pooledDataSource.setPoolPingEnabled(true);
                    if (this.jdbcPingQuery != null) {
                        pooledDataSource.setPoolPingQuery(this.jdbcPingQuery);
                    }

                    pooledDataSource.setPoolPingConnectionsNotUsedFor(this.jdbcPingConnectionNotUsedFor);
                }

                if (this.jdbcDefaultTransactionIsolationLevel > 0) {
                    pooledDataSource.setDefaultTransactionIsolationLevel(this.jdbcDefaultTransactionIsolationLevel);
                }

                this.dataSource = pooledDataSource;
            }
        }

        if (this.databaseType == null) {
            this.initDatabaseType();
        }

    }

    public void initDatabaseType() {
        Connection connection = null;

        try {
            connection = this.dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            this.logger.debug("database product name: '{}'", databaseProductName);
            if ("PostgreSQL".equalsIgnoreCase(databaseProductName) || "KingbaseES".equalsIgnoreCase(databaseProductName)) {
                PreparedStatement preparedStatement = connection.prepareStatement("select version() as version;");
                Throwable var5 = null;

                try {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    Throwable var7 = null;

                    try {
                        String version = null;
                        if (resultSet.next()) {
                            version = resultSet.getString("version");
                        }

                        if (StringUtils.isNotEmpty(version) && version.toLowerCase().startsWith("CockroachDB".toLowerCase())) {
                            databaseProductName = "CockroachDB";
                            this.logger.info("CockroachDB version '{}' detected", version);
                        }
                    } catch (Throwable var51) {
                        var7 = var51;
                        throw var51;
                    } finally {
                        if (resultSet != null) {
                            if (var7 != null) {
                                try {
                                    resultSet.close();
                                } catch (Throwable var50) {
                                    var7.addSuppressed(var50);
                                }
                            } else {
                                resultSet.close();
                            }
                        }

                    }
                } catch (Throwable var53) {
                    var5 = var53;
                    throw var53;
                } finally {
                    if (preparedStatement != null) {
                        if (var5 != null) {
                            try {
                                preparedStatement.close();
                            } catch (Throwable var49) {
                                var5.addSuppressed(var49);
                            }
                        } else {
                            preparedStatement.close();
                        }
                    }

                }
            }

            this.databaseType = this.databaseTypeMappings.getProperty(databaseProductName);
            if (this.databaseType == null) {
                throw new FlowableException("couldn't deduct database type from database product name '" + databaseProductName + "'");
            }

            this.logger.debug("using database type: {}", this.databaseType);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while initializing Database connection", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                this.logger.error("Exception while closing the Database connection", e);
            }

        }

        if ("mssql".equals(this.databaseType)) {
            this.maxNrOfStatementsInBulkInsert = this.DEFAULT_MAX_NR_OF_STATEMENTS_BULK_INSERT_SQL_SERVER;
        }

    }

    public void initSchemaManager() {
        if (this.commonSchemaManager == null) {
            this.commonSchemaManager = new CommonDbSchemaManager();
        }

    }

    public void addSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactories.put(sessionFactory.getSessionType(), sessionFactory);
    }

    public void initCommandContextFactory() {
        if (this.commandContextFactory == null) {
            this.commandContextFactory = new CommandContextFactory();
        }

    }

    public void initTransactionContextFactory() {
        if (this.transactionContextFactory == null) {
            this.transactionContextFactory = new StandaloneMybatisTransactionContextFactory();
        }

    }

    public void initCommandExecutors() {
        this.initDefaultCommandConfig();
        this.initSchemaCommandConfig();
        this.initCommandInvoker();
        this.initCommandInterceptors();
        this.initCommandExecutor();
    }

    public void initDefaultCommandConfig() {
        if (this.defaultCommandConfig == null) {
            this.defaultCommandConfig = new CommandConfig();
        }

    }

    public void initSchemaCommandConfig() {
        if (this.schemaCommandConfig == null) {
            this.schemaCommandConfig = new CommandConfig();
        }

    }

    public void initCommandInvoker() {
        if (this.commandInvoker == null) {
            this.commandInvoker = new DefaultCommandInvoker();
        }

    }

    public void initCommandInterceptors() {
        if (this.commandInterceptors == null) {
            this.commandInterceptors = new ArrayList();
            if (this.customPreCommandInterceptors != null) {
                this.commandInterceptors.addAll(this.customPreCommandInterceptors);
            }

            this.commandInterceptors.addAll(this.getDefaultCommandInterceptors());
            if (this.customPostCommandInterceptors != null) {
                this.commandInterceptors.addAll(this.customPostCommandInterceptors);
            }

            this.commandInterceptors.add(this.commandInvoker);
        }

    }

    public Collection<? extends CommandInterceptor> getDefaultCommandInterceptors() {
        if (this.defaultCommandInterceptors == null) {
            List<CommandInterceptor> interceptors = new ArrayList();
            interceptors.add(new LogInterceptor());
            if ("cockroachdb".equals(this.databaseType)) {
                interceptors.add(new CrDbRetryInterceptor());
            }

            CommandInterceptor transactionInterceptor = this.createTransactionInterceptor();
            if (transactionInterceptor != null) {
                interceptors.add(transactionInterceptor);
            }

            if (this.commandContextFactory != null) {
                String engineCfgKey = this.getEngineCfgKey();
                CommandContextInterceptor commandContextInterceptor = new CommandContextInterceptor(this.commandContextFactory, this.classLoader, this.useClassForNameClassLoading, this.clock, this.objectMapper);
                this.engineConfigurations.put(engineCfgKey, this);
                commandContextInterceptor.setEngineCfgKey(engineCfgKey);
                commandContextInterceptor.setEngineConfigurations(this.engineConfigurations);
                interceptors.add(commandContextInterceptor);
            }

            if (this.transactionContextFactory != null) {
                interceptors.add(new TransactionContextInterceptor(this.transactionContextFactory));
            }

            List<CommandInterceptor> additionalCommandInterceptors = this.getAdditionalDefaultCommandInterceptors();
            if (additionalCommandInterceptors != null) {
                interceptors.addAll(additionalCommandInterceptors);
            }

            this.defaultCommandInterceptors = interceptors;
        }

        return this.defaultCommandInterceptors;
    }

    public abstract String getEngineCfgKey();

    public abstract String getEngineScopeType();

    public List<CommandInterceptor> getAdditionalDefaultCommandInterceptors() {
        return null;
    }

    public void initCommandExecutor() {
        if (this.commandExecutor == null) {
            CommandInterceptor first = this.initInterceptorChain(this.commandInterceptors);
            this.commandExecutor = new CommandExecutorImpl(this.getDefaultCommandConfig(), first);
        }

    }

    public CommandInterceptor initInterceptorChain(List<CommandInterceptor> chain) {
        if (chain != null && !chain.isEmpty()) {
            for(int i = 0; i < chain.size() - 1; ++i) {
                ((CommandInterceptor)chain.get(i)).setNext((CommandInterceptor)chain.get(i + 1));
            }

            return (CommandInterceptor)chain.get(0);
        } else {
            throw new FlowableException("invalid command interceptor chain configuration: " + chain);
        }
    }

    public abstract CommandInterceptor createTransactionInterceptor();

    public void initBeans() {
        if (this.beans == null) {
            this.beans = new HashMap();
        }

    }

    public void initIdGenerator() {
        if (this.idGenerator == null) {
            this.idGenerator = new StrongUuidGenerator();
        }

    }

    public void initObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
            this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }

    }

    public void initClock() {
        if (this.clock == null) {
            this.clock = new DefaultClockImpl();
        }

    }

    public void initDataManagers() {
        if (this.propertyDataManager == null) {
            this.propertyDataManager = new MybatisPropertyDataManager(this.idGenerator);
        }

        if (this.byteArrayDataManager == null) {
            this.byteArrayDataManager = new MybatisByteArrayDataManager(this.idGenerator);
        }

    }

    public void initEntityManagers() {
        if (this.propertyEntityManager == null) {
            this.propertyEntityManager = new PropertyEntityManagerImpl(this, this.propertyDataManager);
        }

        if (this.byteArrayEntityManager == null) {
            this.byteArrayEntityManager = new ByteArrayEntityManagerImpl(this.byteArrayDataManager, this.getEngineCfgKey(), this::getEventDispatcher);
        }

        if (this.tableDataManager == null) {
            this.tableDataManager = new TableDataManagerImpl(this);
        }

    }

    protected void initService(Object service) {
        if (service instanceof CommonEngineServiceImpl) {
            ((CommonEngineServiceImpl)service).setCommandExecutor(this.commandExecutor);
        }

    }

    public void initSessionFactories() {
        if (this.sessionFactories == null) {
            this.sessionFactories = new HashMap();
            if (this.usingRelationalDatabase) {
                this.initDbSqlSessionFactory();
            }

            this.addSessionFactory(new GenericManagerFactory(EntityCache.class, EntityCacheImpl.class));
            if (this.isLoggingSessionEnabled() && !this.sessionFactories.containsKey(LoggingSession.class)) {
                LoggingSessionFactory loggingSessionFactory = new LoggingSessionFactory();
                loggingSessionFactory.setLoggingListener(this.loggingListener);
                loggingSessionFactory.setObjectMapper(this.objectMapper);
                this.sessionFactories.put(LoggingSession.class, loggingSessionFactory);
            }

            this.commandContextFactory.setSessionFactories(this.sessionFactories);
        } else if (this.usingRelationalDatabase) {
            this.initDbSqlSessionFactoryEntitySettings();
        }

        if (this.customSessionFactories != null) {
            for(SessionFactory sessionFactory : this.customSessionFactories) {
                this.addSessionFactory(sessionFactory);
            }
        }

    }

    public void initDbSqlSessionFactory() {
        if (this.dbSqlSessionFactory == null) {
            this.dbSqlSessionFactory = this.createDbSqlSessionFactory();
        }

        this.dbSqlSessionFactory.setDatabaseType(this.databaseType);
        this.dbSqlSessionFactory.setSqlSessionFactory(this.sqlSessionFactory);
        this.dbSqlSessionFactory.setDbHistoryUsed(this.isDbHistoryUsed);
        this.dbSqlSessionFactory.setDatabaseTablePrefix(this.databaseTablePrefix);
        this.dbSqlSessionFactory.setTablePrefixIsSchema(this.tablePrefixIsSchema);
        this.dbSqlSessionFactory.setDatabaseCatalog(this.databaseCatalog);
        this.dbSqlSessionFactory.setDatabaseSchema(this.databaseSchema);
        this.dbSqlSessionFactory.setMaxNrOfStatementsInBulkInsert(this.maxNrOfStatementsInBulkInsert);
        this.initDbSqlSessionFactoryEntitySettings();
        this.addSessionFactory(this.dbSqlSessionFactory);
    }

    public DbSqlSessionFactory createDbSqlSessionFactory() {
        return new DbSqlSessionFactory(this.usePrefixId);
    }

    protected abstract void initDbSqlSessionFactoryEntitySettings();

    protected void defaultInitDbSqlSessionFactoryEntitySettings(List<Class<? extends Entity>> insertOrder, List<Class<? extends Entity>> deleteOrder) {
        if (insertOrder != null) {
            for(Class<? extends Entity> clazz : insertOrder) {
                this.dbSqlSessionFactory.getInsertionOrder().add(clazz);
                if (this.isBulkInsertEnabled) {
                    this.dbSqlSessionFactory.getBulkInserteableEntityClasses().add(clazz);
                }
            }
        }

        if (deleteOrder != null) {
            for(Class<? extends Entity> clazz : deleteOrder) {
                this.dbSqlSessionFactory.getDeletionOrder().add(clazz);
            }
        }

    }

    public void initTransactionFactory() {
        if (this.transactionFactory == null) {
            if (this.transactionsExternallyManaged) {
                this.transactionFactory = new ManagedTransactionFactory();
                Properties properties = new Properties();
                properties.put("closeConnection", "false");
                this.transactionFactory.setProperties(properties);
            } else {
                this.transactionFactory = new JdbcTransactionFactory();
            }
        }

    }

    public void initSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            InputStream inputStream = null;

            try {
                inputStream = this.getMyBatisXmlConfigurationStream();
                Environment environment = new Environment("default", this.transactionFactory, this.dataSource);
                Reader reader = new InputStreamReader(inputStream);
                Properties properties = new Properties();
                properties.put("prefix", this.databaseTablePrefix);
                String wildcardEscapeClause = "";
                if (this.databaseWildcardEscapeCharacter != null && this.databaseWildcardEscapeCharacter.length() != 0) {
                    wildcardEscapeClause = " escape '" + this.databaseWildcardEscapeCharacter + "'";
                }

                properties.put("wildcardEscapeClause", wildcardEscapeClause);
                properties.put("limitBefore", "");
                properties.put("limitAfter", "");
                properties.put("limitBetween", "");
                properties.put("limitBeforeNativeQuery", "");
                properties.put("limitAfterNativeQuery", "");
                properties.put("blobType", "BLOB");
                properties.put("boolValue", "TRUE");
                if (this.databaseType != null) {
                    properties.load(this.getResourceAsStream(this.pathToEngineDbProperties()));
                }

                Configuration configuration = this.initMybatisConfiguration(environment, reader, properties);
                this.sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
            } catch (Exception e) {
                throw new FlowableException("Error while building ibatis SqlSessionFactory: " + e.getMessage(), e);
            } finally {
                IoUtil.closeSilently(inputStream);
            }
        } else {
            this.applyCustomMybatisCustomizations(this.sqlSessionFactory.getConfiguration());
        }

    }

    public String pathToEngineDbProperties() {
        return "org/flowable/common/db/properties/" + this.databaseType + ".properties";
    }

    public Configuration initMybatisConfiguration(Environment environment, Reader reader, Properties properties) {
        XMLConfigBuilder parser = new XMLConfigBuilder(reader, "", properties);
        Configuration configuration = parser.getConfiguration();
        if (this.databaseType != null) {
            configuration.setDatabaseId(this.databaseType);
        }

        configuration.setEnvironment(environment);
        this.initMybatisTypeHandlers(configuration);
        this.initCustomMybatisInterceptors(configuration);
        if (this.isEnableLogSqlExecutionTime()) {
            this.initMyBatisLogSqlExecutionTimePlugin(configuration);
        }

        configuration = this.parseMybatisConfiguration(parser);
        return configuration;
    }

    public void initCustomMybatisMappers(Configuration configuration) {
        if (this.getCustomMybatisMappers() != null) {
            for(Class<?> clazz : this.getCustomMybatisMappers()) {
                if (!configuration.hasMapper(clazz)) {
                    configuration.addMapper(clazz);
                }
            }
        }

    }

    public void initMybatisTypeHandlers(Configuration configuration) {
        TypeHandlerRegistry handlerRegistry = configuration.getTypeHandlerRegistry();
        handlerRegistry.register(Object.class, JdbcType.BOOLEAN, new BooleanTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.BIT, new BooleanTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.TINYINT, new ByteTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.SMALLINT, new ShortTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.INTEGER, new IntegerTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.FLOAT, new FloatTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.DOUBLE, new DoubleTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.CHAR, new StringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.CLOB, new ClobTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.VARCHAR, new StringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NVARCHAR, new NStringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NCHAR, new NStringTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NCLOB, new NClobTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.ARRAY, new ArrayTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.BIGINT, new LongTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.REAL, new BigDecimalTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.DECIMAL, new BigDecimalTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.NUMERIC, new BigDecimalTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.BLOB, new BlobInputStreamTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.LONGVARBINARY, new BlobTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.DATE, new DateOnlyTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.TIME, new TimeOnlyTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.TIMESTAMP, new DateTypeHandler());
        handlerRegistry.register(Object.class, JdbcType.SQLXML, new SqlxmlTypeHandler());
    }

    public void initCustomMybatisInterceptors(Configuration configuration) {
        if (this.customMybatisInterceptors != null) {
            for(Interceptor interceptor : this.customMybatisInterceptors) {
                configuration.addInterceptor(interceptor);
            }
        }

    }

    public void initMyBatisLogSqlExecutionTimePlugin(Configuration configuration) {
        configuration.addInterceptor(new LogSqlExecutionTimePlugin());
    }

    public Configuration parseMybatisConfiguration(XMLConfigBuilder parser) {
        Configuration configuration = parser.parse();
        this.applyCustomMybatisCustomizations(configuration);
        return configuration;
    }

    protected void applyCustomMybatisCustomizations(Configuration configuration) {
        this.initCustomMybatisMappers(configuration);
        if (this.dependentEngineMybatisTypeAliasConfigs != null) {
            for(MybatisTypeAliasConfigurator typeAliasConfig : this.dependentEngineMybatisTypeAliasConfigs) {
                typeAliasConfig.configure(configuration.getTypeAliasRegistry());
            }
        }

        if (this.dependentEngineMybatisTypeHandlerConfigs != null) {
            for(MybatisTypeHandlerConfigurator typeHandlerConfig : this.dependentEngineMybatisTypeHandlerConfigs) {
                typeHandlerConfig.configure(configuration.getTypeHandlerRegistry());
            }
        }

        this.parseDependentEngineMybatisXMLMappers(configuration);
        this.parseCustomMybatisXMLMappers(configuration);
    }

    public void parseCustomMybatisXMLMappers(Configuration configuration) {
        if (this.getCustomMybatisXMLMappers() != null) {
            for(String resource : this.getCustomMybatisXMLMappers()) {
                this.parseMybatisXmlMapping(configuration, resource);
            }
        }

    }

    public void parseDependentEngineMybatisXMLMappers(Configuration configuration) {
        if (this.getDependentEngineMyBatisXmlMappers() != null) {
            for(String resource : this.getDependentEngineMyBatisXmlMappers()) {
                this.parseMybatisXmlMapping(configuration, resource);
            }
        }

    }

    protected void parseMybatisXmlMapping(Configuration configuration, String resource) {
        XMLMapperBuilder mapperParser = new XMLMapperBuilder(this.getResourceAsStream(resource), configuration, resource, configuration.getSqlFragments());
        mapperParser.parse();
    }

    protected InputStream getResourceAsStream(String resource) {
        ClassLoader classLoader = this.getClassLoader();
        return classLoader != null ? this.getClassLoader().getResourceAsStream(resource) : this.getClass().getClassLoader().getResourceAsStream(resource);
    }

    public void setMybatisMappingFile(String file) {
        this.mybatisMappingFile = file;
    }

    public String getMybatisMappingFile() {
        return this.mybatisMappingFile;
    }

    public abstract InputStream getMyBatisXmlConfigurationStream();

    public void initConfigurators() {
        this.allConfigurators = new ArrayList();
        this.allConfigurators.addAll(this.getEngineSpecificEngineConfigurators());
        if (this.configurators != null) {
            this.allConfigurators.addAll(this.configurators);
        }

        if (this.enableConfiguratorServiceLoader) {
            ClassLoader classLoader = this.getClassLoader();
            if (classLoader == null) {
                classLoader = ReflectUtil.getClassLoader();
            }

            ServiceLoader<EngineConfigurator> configuratorServiceLoader = ServiceLoader.load(EngineConfigurator.class, classLoader);
            int nrOfServiceLoadedConfigurators = 0;

            for(EngineConfigurator configurator : configuratorServiceLoader) {
                this.allConfigurators.add(configurator);
                ++nrOfServiceLoadedConfigurators;
            }

            if (nrOfServiceLoadedConfigurators > 0) {
                this.logger.info("Found {} auto-discoverable Process Engine Configurator{}", nrOfServiceLoadedConfigurators, nrOfServiceLoadedConfigurators > 1 ? "s" : "");
            }

            if (!this.allConfigurators.isEmpty()) {
                this.allConfigurators.sort(new Comparator<EngineConfigurator>() {
                    public int compare(EngineConfigurator configurator1, EngineConfigurator configurator2) {
                        int priority1 = configurator1.getPriority();
                        int priority2 = configurator2.getPriority();
                        if (priority1 < priority2) {
                            return -1;
                        } else {
                            return priority1 > priority2 ? 1 : 0;
                        }
                    }
                });
                this.logger.info("Found {} Engine Configurators in total:", this.allConfigurators.size());

                for(EngineConfigurator configurator : this.allConfigurators) {
                    this.logger.info("{} (priority:{})", configurator.getClass(), configurator.getPriority());
                }
            }
        }

    }

    public void close() {
        if (this.forceCloseMybatisConnectionPool && this.dataSource instanceof PooledDataSource) {
            ((PooledDataSource)this.dataSource).forceCloseAll();
        }

    }

    protected List<EngineConfigurator> getEngineSpecificEngineConfigurators() {
        return Collections.emptyList();
    }

    public void configuratorsBeforeInit() {
        for(EngineConfigurator configurator : this.allConfigurators) {
            this.logger.info("Executing beforeInit() of {} (priority:{})", configurator.getClass(), configurator.getPriority());
            configurator.beforeInit(this);
        }

    }

    public void configuratorsAfterInit() {
        for(EngineConfigurator configurator : this.allConfigurators) {
            this.logger.info("Executing configure() of {} (priority:{})", configurator.getClass(), configurator.getPriority());
            configurator.configure(this);
        }

    }

    public LockManager getLockManager(String lockName) {
        return new LockManagerImpl(this.commandExecutor, lockName, this.getLockPollRate(), this.getEngineCfgKey());
    }

    public abstract String getEngineName();

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public AbstractEngineConfiguration setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public boolean isUseClassForNameClassLoading() {
        return this.useClassForNameClassLoading;
    }

    public AbstractEngineConfiguration setUseClassForNameClassLoading(boolean useClassForNameClassLoading) {
        this.useClassForNameClassLoading = useClassForNameClassLoading;
        return this;
    }

    public void addEngineLifecycleListener(EngineLifecycleListener engineLifecycleListener) {
        if (this.engineLifecycleListeners == null) {
            this.engineLifecycleListeners = new ArrayList();
        }

        this.engineLifecycleListeners.add(engineLifecycleListener);
    }

    public List<EngineLifecycleListener> getEngineLifecycleListeners() {
        return this.engineLifecycleListeners;
    }

    public AbstractEngineConfiguration setEngineLifecycleListeners(List<EngineLifecycleListener> engineLifecycleListeners) {
        this.engineLifecycleListeners = engineLifecycleListeners;
        return this;
    }

    public String getDatabaseType() {
        return this.databaseType;
    }

    public AbstractEngineConfiguration setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
        return this;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public AbstractEngineConfiguration setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public SchemaManager getSchemaManager() {
        return this.schemaManager;
    }

    public AbstractEngineConfiguration setSchemaManager(SchemaManager schemaManager) {
        this.schemaManager = schemaManager;
        return this;
    }

    public SchemaManager getCommonSchemaManager() {
        return this.commonSchemaManager;
    }

    public AbstractEngineConfiguration setCommonSchemaManager(SchemaManager commonSchemaManager) {
        this.commonSchemaManager = commonSchemaManager;
        return this;
    }

    public Command<Void> getSchemaManagementCmd() {
        return this.schemaManagementCmd;
    }

    public AbstractEngineConfiguration setSchemaManagementCmd(Command<Void> schemaManagementCmd) {
        this.schemaManagementCmd = schemaManagementCmd;
        return this;
    }

    public String getJdbcDriver() {
        return this.jdbcDriver;
    }

    public AbstractEngineConfiguration setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
        return this;
    }

    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    public AbstractEngineConfiguration setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public String getJdbcUsername() {
        return this.jdbcUsername;
    }

    public AbstractEngineConfiguration setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
        return this;
    }

    public String getJdbcPassword() {
        return this.jdbcPassword;
    }

    public AbstractEngineConfiguration setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
        return this;
    }

    public int getJdbcMaxActiveConnections() {
        return this.jdbcMaxActiveConnections;
    }

    public AbstractEngineConfiguration setJdbcMaxActiveConnections(int jdbcMaxActiveConnections) {
        this.jdbcMaxActiveConnections = jdbcMaxActiveConnections;
        return this;
    }

    public int getJdbcMaxIdleConnections() {
        return this.jdbcMaxIdleConnections;
    }

    public AbstractEngineConfiguration setJdbcMaxIdleConnections(int jdbcMaxIdleConnections) {
        this.jdbcMaxIdleConnections = jdbcMaxIdleConnections;
        return this;
    }

    public int getJdbcMaxCheckoutTime() {
        return this.jdbcMaxCheckoutTime;
    }

    public AbstractEngineConfiguration setJdbcMaxCheckoutTime(int jdbcMaxCheckoutTime) {
        this.jdbcMaxCheckoutTime = jdbcMaxCheckoutTime;
        return this;
    }

    public int getJdbcMaxWaitTime() {
        return this.jdbcMaxWaitTime;
    }

    public AbstractEngineConfiguration setJdbcMaxWaitTime(int jdbcMaxWaitTime) {
        this.jdbcMaxWaitTime = jdbcMaxWaitTime;
        return this;
    }

    public boolean isJdbcPingEnabled() {
        return this.jdbcPingEnabled;
    }

    public AbstractEngineConfiguration setJdbcPingEnabled(boolean jdbcPingEnabled) {
        this.jdbcPingEnabled = jdbcPingEnabled;
        return this;
    }

    public int getJdbcPingConnectionNotUsedFor() {
        return this.jdbcPingConnectionNotUsedFor;
    }

    public AbstractEngineConfiguration setJdbcPingConnectionNotUsedFor(int jdbcPingConnectionNotUsedFor) {
        this.jdbcPingConnectionNotUsedFor = jdbcPingConnectionNotUsedFor;
        return this;
    }

    public int getJdbcDefaultTransactionIsolationLevel() {
        return this.jdbcDefaultTransactionIsolationLevel;
    }

    public AbstractEngineConfiguration setJdbcDefaultTransactionIsolationLevel(int jdbcDefaultTransactionIsolationLevel) {
        this.jdbcDefaultTransactionIsolationLevel = jdbcDefaultTransactionIsolationLevel;
        return this;
    }

    public String getJdbcPingQuery() {
        return this.jdbcPingQuery;
    }

    public AbstractEngineConfiguration setJdbcPingQuery(String jdbcPingQuery) {
        this.jdbcPingQuery = jdbcPingQuery;
        return this;
    }

    public String getDataSourceJndiName() {
        return this.dataSourceJndiName;
    }

    public AbstractEngineConfiguration setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
        return this;
    }

    public CommandConfig getSchemaCommandConfig() {
        return this.schemaCommandConfig;
    }

    public AbstractEngineConfiguration setSchemaCommandConfig(CommandConfig schemaCommandConfig) {
        this.schemaCommandConfig = schemaCommandConfig;
        return this;
    }

    public boolean isTransactionsExternallyManaged() {
        return this.transactionsExternallyManaged;
    }

    public AbstractEngineConfiguration setTransactionsExternallyManaged(boolean transactionsExternallyManaged) {
        this.transactionsExternallyManaged = transactionsExternallyManaged;
        return this;
    }

    public Map<Object, Object> getBeans() {
        return this.beans;
    }

    public AbstractEngineConfiguration setBeans(Map<Object, Object> beans) {
        this.beans = beans;
        return this;
    }

    public IdGenerator getIdGenerator() {
        return this.idGenerator;
    }

    public AbstractEngineConfiguration setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    public boolean isUsePrefixId() {
        return this.usePrefixId;
    }

    public AbstractEngineConfiguration setUsePrefixId(boolean usePrefixId) {
        this.usePrefixId = usePrefixId;
        return this;
    }

    public String getXmlEncoding() {
        return this.xmlEncoding;
    }

    public AbstractEngineConfiguration setXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
        return this;
    }

    public CommandConfig getDefaultCommandConfig() {
        return this.defaultCommandConfig;
    }

    public AbstractEngineConfiguration setDefaultCommandConfig(CommandConfig defaultCommandConfig) {
        this.defaultCommandConfig = defaultCommandConfig;
        return this;
    }

    public CommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public AbstractEngineConfiguration setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public CommandContextFactory getCommandContextFactory() {
        return this.commandContextFactory;
    }

    public AbstractEngineConfiguration setCommandContextFactory(CommandContextFactory commandContextFactory) {
        this.commandContextFactory = commandContextFactory;
        return this;
    }

    public CommandInterceptor getCommandInvoker() {
        return this.commandInvoker;
    }

    public AbstractEngineConfiguration setCommandInvoker(CommandInterceptor commandInvoker) {
        this.commandInvoker = commandInvoker;
        return this;
    }

    public AgendaOperationRunner getAgendaOperationRunner() {
        return this.agendaOperationRunner;
    }

    public AbstractEngineConfiguration setAgendaOperationRunner(AgendaOperationRunner agendaOperationRunner) {
        this.agendaOperationRunner = agendaOperationRunner;
        return this;
    }

    public List<CommandInterceptor> getCustomPreCommandInterceptors() {
        return this.customPreCommandInterceptors;
    }

    public AbstractEngineConfiguration setCustomPreCommandInterceptors(List<CommandInterceptor> customPreCommandInterceptors) {
        this.customPreCommandInterceptors = customPreCommandInterceptors;
        return this;
    }

    public List<CommandInterceptor> getCustomPostCommandInterceptors() {
        return this.customPostCommandInterceptors;
    }

    public AbstractEngineConfiguration setCustomPostCommandInterceptors(List<CommandInterceptor> customPostCommandInterceptors) {
        this.customPostCommandInterceptors = customPostCommandInterceptors;
        return this;
    }

    public List<CommandInterceptor> getCommandInterceptors() {
        return this.commandInterceptors;
    }

    public AbstractEngineConfiguration setCommandInterceptors(List<CommandInterceptor> commandInterceptors) {
        this.commandInterceptors = commandInterceptors;
        return this;
    }

    public Map<String, AbstractEngineConfiguration> getEngineConfigurations() {
        return this.engineConfigurations;
    }

    public AbstractEngineConfiguration setEngineConfigurations(Map<String, AbstractEngineConfiguration> engineConfigurations) {
        this.engineConfigurations = engineConfigurations;
        return this;
    }

    public void addEngineConfiguration(String key, String scopeType, AbstractEngineConfiguration engineConfiguration) {
        if (this.engineConfigurations == null) {
            this.engineConfigurations = new HashMap();
        }

        this.engineConfigurations.put(key, engineConfiguration);
        this.engineConfigurations.put(scopeType, engineConfiguration);
    }

    public Map<String, AbstractServiceConfiguration> getServiceConfigurations() {
        return this.serviceConfigurations;
    }

    public AbstractEngineConfiguration setServiceConfigurations(Map<String, AbstractServiceConfiguration> serviceConfigurations) {
        this.serviceConfigurations = serviceConfigurations;
        return this;
    }

    public void addServiceConfiguration(String key, AbstractServiceConfiguration serviceConfiguration) {
        if (this.serviceConfigurations == null) {
            this.serviceConfigurations = new HashMap();
        }

        this.serviceConfigurations.put(key, serviceConfiguration);
    }

    public Map<String, EventRegistryEventConsumer> getEventRegistryEventConsumers() {
        return this.eventRegistryEventConsumers;
    }

    public AbstractEngineConfiguration setEventRegistryEventConsumers(Map<String, EventRegistryEventConsumer> eventRegistryEventConsumers) {
        this.eventRegistryEventConsumers = eventRegistryEventConsumers;
        return this;
    }

    public void addEventRegistryEventConsumer(String key, EventRegistryEventConsumer eventRegistryEventConsumer) {
        if (this.eventRegistryEventConsumers == null) {
            this.eventRegistryEventConsumers = new HashMap();
        }

        this.eventRegistryEventConsumers.put(key, eventRegistryEventConsumer);
    }

    public AbstractEngineConfiguration setDefaultCommandInterceptors(Collection<? extends CommandInterceptor> defaultCommandInterceptors) {
        this.defaultCommandInterceptors = defaultCommandInterceptors;
        return this;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return this.sqlSessionFactory;
    }

    public AbstractEngineConfiguration setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        return this;
    }

    public boolean isDbHistoryUsed() {
        return this.isDbHistoryUsed;
    }

    public AbstractEngineConfiguration setDbHistoryUsed(boolean isDbHistoryUsed) {
        this.isDbHistoryUsed = isDbHistoryUsed;
        return this;
    }

    public DbSqlSessionFactory getDbSqlSessionFactory() {
        return this.dbSqlSessionFactory;
    }

    public AbstractEngineConfiguration setDbSqlSessionFactory(DbSqlSessionFactory dbSqlSessionFactory) {
        this.dbSqlSessionFactory = dbSqlSessionFactory;
        return this;
    }

    public TransactionFactory getTransactionFactory() {
        return this.transactionFactory;
    }

    public AbstractEngineConfiguration setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
        return this;
    }

    public TransactionContextFactory getTransactionContextFactory() {
        return this.transactionContextFactory;
    }

    public AbstractEngineConfiguration setTransactionContextFactory(TransactionContextFactory transactionContextFactory) {
        this.transactionContextFactory = transactionContextFactory;
        return this;
    }

    public int getMaxNrOfStatementsInBulkInsert() {
        return this.maxNrOfStatementsInBulkInsert;
    }

    public AbstractEngineConfiguration setMaxNrOfStatementsInBulkInsert(int maxNrOfStatementsInBulkInsert) {
        this.maxNrOfStatementsInBulkInsert = maxNrOfStatementsInBulkInsert;
        return this;
    }

    public boolean isBulkInsertEnabled() {
        return this.isBulkInsertEnabled;
    }

    public AbstractEngineConfiguration setBulkInsertEnabled(boolean isBulkInsertEnabled) {
        this.isBulkInsertEnabled = isBulkInsertEnabled;
        return this;
    }

    public Set<Class<?>> getCustomMybatisMappers() {
        return this.customMybatisMappers;
    }

    public AbstractEngineConfiguration setCustomMybatisMappers(Set<Class<?>> customMybatisMappers) {
        this.customMybatisMappers = customMybatisMappers;
        return this;
    }

    public Set<String> getCustomMybatisXMLMappers() {
        return this.customMybatisXMLMappers;
    }

    public AbstractEngineConfiguration setCustomMybatisXMLMappers(Set<String> customMybatisXMLMappers) {
        this.customMybatisXMLMappers = customMybatisXMLMappers;
        return this;
    }

    public Set<String> getDependentEngineMyBatisXmlMappers() {
        return this.dependentEngineMyBatisXmlMappers;
    }

    public AbstractEngineConfiguration setCustomMybatisInterceptors(List<Interceptor> customMybatisInterceptors) {
        this.customMybatisInterceptors = customMybatisInterceptors;
        return this;
    }

    public List<Interceptor> getCustomMybatisInterceptors() {
        return this.customMybatisInterceptors;
    }

    public AbstractEngineConfiguration setDependentEngineMyBatisXmlMappers(Set<String> dependentEngineMyBatisXmlMappers) {
        this.dependentEngineMyBatisXmlMappers = dependentEngineMyBatisXmlMappers;
        return this;
    }

    public List<MybatisTypeAliasConfigurator> getDependentEngineMybatisTypeAliasConfigs() {
        return this.dependentEngineMybatisTypeAliasConfigs;
    }

    public AbstractEngineConfiguration setDependentEngineMybatisTypeAliasConfigs(List<MybatisTypeAliasConfigurator> dependentEngineMybatisTypeAliasConfigs) {
        this.dependentEngineMybatisTypeAliasConfigs = dependentEngineMybatisTypeAliasConfigs;
        return this;
    }

    public List<MybatisTypeHandlerConfigurator> getDependentEngineMybatisTypeHandlerConfigs() {
        return this.dependentEngineMybatisTypeHandlerConfigs;
    }

    public AbstractEngineConfiguration setDependentEngineMybatisTypeHandlerConfigs(List<MybatisTypeHandlerConfigurator> dependentEngineMybatisTypeHandlerConfigs) {
        this.dependentEngineMybatisTypeHandlerConfigs = dependentEngineMybatisTypeHandlerConfigs;
        return this;
    }

    public List<SessionFactory> getCustomSessionFactories() {
        return this.customSessionFactories;
    }

    public AbstractEngineConfiguration addCustomSessionFactory(SessionFactory sessionFactory) {
        if (this.customSessionFactories == null) {
            this.customSessionFactories = new ArrayList();
        }

        this.customSessionFactories.add(sessionFactory);
        return this;
    }

    public AbstractEngineConfiguration setCustomSessionFactories(List<SessionFactory> customSessionFactories) {
        this.customSessionFactories = customSessionFactories;
        return this;
    }

    public boolean isUsingRelationalDatabase() {
        return this.usingRelationalDatabase;
    }

    public AbstractEngineConfiguration setUsingRelationalDatabase(boolean usingRelationalDatabase) {
        this.usingRelationalDatabase = usingRelationalDatabase;
        return this;
    }

    public boolean isUsingSchemaMgmt() {
        return this.usingSchemaMgmt;
    }

    public AbstractEngineConfiguration setUsingSchemaMgmt(boolean usingSchema) {
        this.usingSchemaMgmt = usingSchema;
        return this;
    }

    public String getDatabaseTablePrefix() {
        return this.databaseTablePrefix;
    }

    public AbstractEngineConfiguration setDatabaseTablePrefix(String databaseTablePrefix) {
        this.databaseTablePrefix = databaseTablePrefix;
        return this;
    }

    public String getDatabaseWildcardEscapeCharacter() {
        return this.databaseWildcardEscapeCharacter;
    }

    public AbstractEngineConfiguration setDatabaseWildcardEscapeCharacter(String databaseWildcardEscapeCharacter) {
        this.databaseWildcardEscapeCharacter = databaseWildcardEscapeCharacter;
        return this;
    }

    public String getDatabaseCatalog() {
        return this.databaseCatalog;
    }

    public AbstractEngineConfiguration setDatabaseCatalog(String databaseCatalog) {
        this.databaseCatalog = databaseCatalog;
        return this;
    }

    public String getDatabaseSchema() {
        return this.databaseSchema;
    }

    public AbstractEngineConfiguration setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
        return this;
    }

    public boolean isTablePrefixIsSchema() {
        return this.tablePrefixIsSchema;
    }

    public AbstractEngineConfiguration setTablePrefixIsSchema(boolean tablePrefixIsSchema) {
        this.tablePrefixIsSchema = tablePrefixIsSchema;
        return this;
    }

    public boolean isAlwaysLookupLatestDefinitionVersion() {
        return this.alwaysLookupLatestDefinitionVersion;
    }

    public AbstractEngineConfiguration setAlwaysLookupLatestDefinitionVersion(boolean alwaysLookupLatestDefinitionVersion) {
        this.alwaysLookupLatestDefinitionVersion = alwaysLookupLatestDefinitionVersion;
        return this;
    }

    public boolean isFallbackToDefaultTenant() {
        return this.fallbackToDefaultTenant;
    }

    public AbstractEngineConfiguration setFallbackToDefaultTenant(boolean fallbackToDefaultTenant) {
        this.fallbackToDefaultTenant = fallbackToDefaultTenant;
        return this;
    }

    /** @deprecated */
    @Deprecated
    public String getDefaultTenantValue() {
        return this.getDefaultTenantProvider().getDefaultTenant((String)null, (String)null, (String)null);
    }

    public AbstractEngineConfiguration setDefaultTenantValue(String defaultTenantValue) {
        this.defaultTenantProvider = (tenantId, scope, scopeKey) -> defaultTenantValue;
        return this;
    }

    public DefaultTenantProvider getDefaultTenantProvider() {
        return this.defaultTenantProvider;
    }

    public AbstractEngineConfiguration setDefaultTenantProvider(DefaultTenantProvider defaultTenantProvider) {
        this.defaultTenantProvider = defaultTenantProvider;
        return this;
    }

    public boolean isEnableLogSqlExecutionTime() {
        return this.enableLogSqlExecutionTime;
    }

    public void setEnableLogSqlExecutionTime(boolean enableLogSqlExecutionTime) {
        this.enableLogSqlExecutionTime = enableLogSqlExecutionTime;
    }

    public Map<Class<?>, SessionFactory> getSessionFactories() {
        return this.sessionFactories;
    }

    public AbstractEngineConfiguration setSessionFactories(Map<Class<?>, SessionFactory> sessionFactories) {
        this.sessionFactories = sessionFactories;
        return this;
    }

    public String getDatabaseSchemaUpdate() {
        return this.databaseSchemaUpdate;
    }

    public AbstractEngineConfiguration setDatabaseSchemaUpdate(String databaseSchemaUpdate) {
        this.databaseSchemaUpdate = databaseSchemaUpdate;
        return this;
    }

    public boolean isUseLockForDatabaseSchemaUpdate() {
        return this.useLockForDatabaseSchemaUpdate;
    }

    public AbstractEngineConfiguration setUseLockForDatabaseSchemaUpdate(boolean useLockForDatabaseSchemaUpdate) {
        this.useLockForDatabaseSchemaUpdate = useLockForDatabaseSchemaUpdate;
        return this;
    }

    public boolean isEnableEventDispatcher() {
        return this.enableEventDispatcher;
    }

    public AbstractEngineConfiguration setEnableEventDispatcher(boolean enableEventDispatcher) {
        this.enableEventDispatcher = enableEventDispatcher;
        return this;
    }

    public FlowableEventDispatcher getEventDispatcher() {
        return this.eventDispatcher;
    }

    public AbstractEngineConfiguration setEventDispatcher(FlowableEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        return this;
    }

    public List<FlowableEventListener> getEventListeners() {
        return this.eventListeners;
    }

    public AbstractEngineConfiguration setEventListeners(List<FlowableEventListener> eventListeners) {
        this.eventListeners = eventListeners;
        return this;
    }

    public Map<String, List<FlowableEventListener>> getTypedEventListeners() {
        return this.typedEventListeners;
    }

    public AbstractEngineConfiguration setTypedEventListeners(Map<String, List<FlowableEventListener>> typedEventListeners) {
        this.typedEventListeners = typedEventListeners;
        return this;
    }

    public List<EventDispatchAction> getAdditionalEventDispatchActions() {
        return this.additionalEventDispatchActions;
    }

    public AbstractEngineConfiguration setAdditionalEventDispatchActions(List<EventDispatchAction> additionalEventDispatchActions) {
        this.additionalEventDispatchActions = additionalEventDispatchActions;
        return this;
    }

    public void initEventDispatcher() {
        if (this.eventDispatcher == null) {
            this.eventDispatcher = new FlowableEventDispatcherImpl();
        }

        this.initAdditionalEventDispatchActions();
        this.eventDispatcher.setEnabled(this.enableEventDispatcher);
        this.initEventListeners();
        this.initTypedEventListeners();
    }

    protected void initEventListeners() {
        if (this.eventListeners != null) {
            for(FlowableEventListener listenerToAdd : this.eventListeners) {
                this.eventDispatcher.addEventListener(listenerToAdd);
            }
        }

    }

    protected void initAdditionalEventDispatchActions() {
        if (this.additionalEventDispatchActions == null) {
            this.additionalEventDispatchActions = new ArrayList();
        }

    }

    protected void initTypedEventListeners() {
        if (this.typedEventListeners != null) {
            for(Map.Entry<String, List<FlowableEventListener>> listenersToAdd : this.typedEventListeners.entrySet()) {
                FlowableEngineEventType[] types = FlowableEngineEventType.getTypesFromString((String)listenersToAdd.getKey());

                for(FlowableEventListener listenerToAdd : listenersToAdd.getValue()) {
                    this.eventDispatcher.addEventListener(listenerToAdd, types);
                }
            }
        }

    }

    public boolean isLoggingSessionEnabled() {
        return this.loggingListener != null;
    }

    public LoggingListener getLoggingListener() {
        return this.loggingListener;
    }

    public void setLoggingListener(LoggingListener loggingListener) {
        this.loggingListener = loggingListener;
    }

    public Clock getClock() {
        return this.clock;
    }

    public AbstractEngineConfiguration setClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public AbstractEngineConfiguration setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public int getMaxLengthString() {
        if (this.maxLengthStringVariableType == -1) {
            return "oracle".equalsIgnoreCase(this.databaseType) ? 2000 : 4000;
        } else {
            return this.maxLengthStringVariableType;
        }
    }

    public int getMaxLengthStringVariableType() {
        return this.maxLengthStringVariableType;
    }

    public AbstractEngineConfiguration setMaxLengthStringVariableType(int maxLengthStringVariableType) {
        this.maxLengthStringVariableType = maxLengthStringVariableType;
        return this;
    }

    public PropertyDataManager getPropertyDataManager() {
        return this.propertyDataManager;
    }

    public Duration getLockPollRate() {
        return this.lockPollRate;
    }

    public AbstractEngineConfiguration setLockPollRate(Duration lockPollRate) {
        this.lockPollRate = lockPollRate;
        return this;
    }

    public Duration getSchemaLockWaitTime() {
        return this.schemaLockWaitTime;
    }

    public void setSchemaLockWaitTime(Duration schemaLockWaitTime) {
        this.schemaLockWaitTime = schemaLockWaitTime;
    }

    public AbstractEngineConfiguration setPropertyDataManager(PropertyDataManager propertyDataManager) {
        this.propertyDataManager = propertyDataManager;
        return this;
    }

    public PropertyEntityManager getPropertyEntityManager() {
        return this.propertyEntityManager;
    }

    public AbstractEngineConfiguration setPropertyEntityManager(PropertyEntityManager propertyEntityManager) {
        this.propertyEntityManager = propertyEntityManager;
        return this;
    }

    public ByteArrayDataManager getByteArrayDataManager() {
        return this.byteArrayDataManager;
    }

    public AbstractEngineConfiguration setByteArrayDataManager(ByteArrayDataManager byteArrayDataManager) {
        this.byteArrayDataManager = byteArrayDataManager;
        return this;
    }

    public ByteArrayEntityManager getByteArrayEntityManager() {
        return this.byteArrayEntityManager;
    }

    public AbstractEngineConfiguration setByteArrayEntityManager(ByteArrayEntityManager byteArrayEntityManager) {
        this.byteArrayEntityManager = byteArrayEntityManager;
        return this;
    }

    public TableDataManager getTableDataManager() {
        return this.tableDataManager;
    }

    public AbstractEngineConfiguration setTableDataManager(TableDataManager tableDataManager) {
        this.tableDataManager = tableDataManager;
        return this;
    }

    public List<EngineDeployer> getDeployers() {
        return this.deployers;
    }

    public AbstractEngineConfiguration setDeployers(List<EngineDeployer> deployers) {
        this.deployers = deployers;
        return this;
    }

    public List<EngineDeployer> getCustomPreDeployers() {
        return this.customPreDeployers;
    }

    public AbstractEngineConfiguration setCustomPreDeployers(List<EngineDeployer> customPreDeployers) {
        this.customPreDeployers = customPreDeployers;
        return this;
    }

    public List<EngineDeployer> getCustomPostDeployers() {
        return this.customPostDeployers;
    }

    public AbstractEngineConfiguration setCustomPostDeployers(List<EngineDeployer> customPostDeployers) {
        this.customPostDeployers = customPostDeployers;
        return this;
    }

    public boolean isEnableConfiguratorServiceLoader() {
        return this.enableConfiguratorServiceLoader;
    }

    public AbstractEngineConfiguration setEnableConfiguratorServiceLoader(boolean enableConfiguratorServiceLoader) {
        this.enableConfiguratorServiceLoader = enableConfiguratorServiceLoader;
        return this;
    }

    public List<EngineConfigurator> getConfigurators() {
        return this.configurators;
    }

    public AbstractEngineConfiguration addConfigurator(EngineConfigurator configurator) {
        if (this.configurators == null) {
            this.configurators = new ArrayList();
        }

        this.configurators.add(configurator);
        return this;
    }

    public List<EngineConfigurator> getAllConfigurators() {
        return this.allConfigurators;
    }

    public AbstractEngineConfiguration setConfigurators(List<EngineConfigurator> configurators) {
        this.configurators = configurators;
        return this;
    }

    public EngineConfigurator getIdmEngineConfigurator() {
        return this.idmEngineConfigurator;
    }

    public AbstractEngineConfiguration setIdmEngineConfigurator(EngineConfigurator idmEngineConfigurator) {
        this.idmEngineConfigurator = idmEngineConfigurator;
        return this;
    }

    public EngineConfigurator getEventRegistryConfigurator() {
        return this.eventRegistryConfigurator;
    }

    public AbstractEngineConfiguration setEventRegistryConfigurator(EngineConfigurator eventRegistryConfigurator) {
        this.eventRegistryConfigurator = eventRegistryConfigurator;
        return this;
    }

    public AbstractEngineConfiguration setForceCloseMybatisConnectionPool(boolean forceCloseMybatisConnectionPool) {
        this.forceCloseMybatisConnectionPool = forceCloseMybatisConnectionPool;
        return this;
    }

    public boolean isForceCloseMybatisConnectionPool() {
        return this.forceCloseMybatisConnectionPool;
    }
}
