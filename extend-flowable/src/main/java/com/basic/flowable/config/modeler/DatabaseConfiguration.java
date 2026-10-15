package com.basic.flowable.config.modeler;

import org.flowable.ui.modeler.repository.ModelRepositoryImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


/**
 * @author yanbin
 * @description TODO (/)
 * @date 2023/10/19
 */
@Configuration
@ComponentScan(value = {
        "org.flowable.ui.modeler.repository",
        "org.flowable.ui.modeler.service",
        "org.flowable.ui.common.tenant",
        "org.flowable.ui.common.repository"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ModelRepositoryImpl.class})
})
public class DatabaseConfiguration {

//    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);
//
//    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";
//
//    @Bean
//    public Liquibase liquibase(DataSource dataSource) {
//        LOGGER.info("Configuring Liquibase");
//
//        Liquibase liquibase = null;
//        try {
//            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
//            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
//            database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
//            database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());
//
//            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
//            liquibase.update("flowable");
//            return liquibase;
//        } catch (Exception e) {
//            throw new InternalServerErrorException("Error creating liquibase database", e);
//        } finally {
//            closeDatabase(liquibase);
//        }
//    }
//
//    private void closeDatabase(Liquibase liquibase) {
//        if (liquibase != null) {
//            Database database = liquibase.getDatabase();
//            if (database != null) {
//                try {
//                    database.close();
//                } catch (DatabaseException e) {
//                    LOGGER.warn("Error closing database", e);
//                }
//            }
//        }
//    }

}
