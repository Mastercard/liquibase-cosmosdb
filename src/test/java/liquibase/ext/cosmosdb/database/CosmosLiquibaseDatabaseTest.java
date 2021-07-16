package liquibase.ext.cosmosdb.database;

import liquibase.CatalogAndSchema;
import liquibase.Scope;
import liquibase.changelog.ChangeLogHistoryService;
import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.ext.cosmosdb.statement.DeleteAllContainersStatement;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.DATABASE_CHANGE_LOG_LOCK_TABLE_NAME;
import static liquibase.ext.cosmosdb.TestUtils.DATABASE_CHANGE_LOG_TABLE_NAME;
import static liquibase.ext.cosmosdb.TestUtils.TEST_COSMOS_DB_CONNECTION_STRING;
import static liquibase.ext.cosmosdb.TestUtils.TEST_WRONG_CONNECTION_STRING;
import static liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase.COSMOSDB_PRODUCT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CosmosLiquibaseDatabaseTest {

    @Mock
    private CatalogAndSchema catalogAndSchema;

    @Mock
    private Scope scope;

    @Mock
    private ChangeLogHistoryServiceFactory changeLogHistoryServiceFactory;

    @Mock
    private ExecutorService executorService;

    @Mock
    private ChangeLogHistoryService changeLogHistoryService;

    @Mock
    private Executor executor;

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(scope, changeLogHistoryServiceFactory, executorService, changeLogHistoryService, executor);
    }

    @SneakyThrows
    @Test
    void findCorrectDatabaseImplementation() {
        final CosmosConnection connection = new CosmosConnection();
        final CosmosLiquibaseDatabase database =
                (CosmosLiquibaseDatabase) DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
        assertThat(database).isNotNull();
        assertThat(database.getConnection()).isEqualTo(connection);
        assertThat(database.toString()).isNotNull().isNotEqualTo("NOT CONNECTED");
    }

    @SneakyThrows
    @Test
    void dropDatabaseObjectsTest() {
        MockedStatic<Scope> scopeUtilities = Mockito.mockStatic(Scope.class);
        scopeUtilities.when(Scope::getCurrentScope).thenReturn(scope);
        when(scope.getSingleton(any())).thenReturn(executorService);
        when(executorService.getExecutor(any(), any())).thenReturn(executor);

        MockedStatic<ChangeLogHistoryServiceFactory> changeLogHistoryServiceFactoryUtilities = Mockito.mockStatic(ChangeLogHistoryServiceFactory.class);
        changeLogHistoryServiceFactoryUtilities.when(ChangeLogHistoryServiceFactory::getInstance).thenReturn(changeLogHistoryServiceFactory);
        when(changeLogHistoryServiceFactory.getChangeLogService(any())).thenReturn(changeLogHistoryService);

        CosmosLiquibaseDatabase cosmosLiquibaseDatabase = new CosmosLiquibaseDatabase();
        cosmosLiquibaseDatabase.dropDatabaseObjects(catalogAndSchema);

        verify(scope).getSingleton(any());
        verify(changeLogHistoryService).destroy();
        verify(executor).execute(any(DeleteAllContainersStatement.class));
        verify(changeLogHistoryServiceFactory).getChangeLogService(any());
        scopeUtilities.close();
        changeLogHistoryServiceFactoryUtilities.close();
    }

    @Test
    void setDatabaseChangeLogTableName() {
        final CosmosLiquibaseDatabase cosmosLiquibaseDatabase = new CosmosLiquibaseDatabase();
        assertThat(cosmosLiquibaseDatabase.getDatabaseChangeLogTableName()).isEqualTo(DATABASE_CHANGE_LOG_TABLE_NAME);
    }

    @Test
    void getDatabaseChangeLogLockTableName() {
        final CosmosLiquibaseDatabase cosmosLiquibaseDatabase = new CosmosLiquibaseDatabase();
        assertThat(cosmosLiquibaseDatabase.getDatabaseChangeLogLockTableName()).isEqualTo(DATABASE_CHANGE_LOG_LOCK_TABLE_NAME);
    }

    @Test
    void getDefaultPort() {
        assertThat(new CosmosLiquibaseDatabase().getDefaultPort()).isEqualTo(8081);
    }

    @Test
    void getDefaultDriver() {
        CosmosLiquibaseDatabase cosmosLiquibaseDatabase = new CosmosLiquibaseDatabase();

        assertThat(cosmosLiquibaseDatabase.getDefaultDriver(TEST_COSMOS_DB_CONNECTION_STRING)).isEqualTo(CosmosClientDriver.class.getName());
        assertThat(cosmosLiquibaseDatabase.getDefaultDriver(TEST_WRONG_CONNECTION_STRING)).isNull();

    }

    @Test
    void getDefaultDatabaseProductName() {
        assertThat(new CosmosLiquibaseDatabase().getDefaultDatabaseProductName()).isEqualTo(COSMOSDB_PRODUCT_NAME);
    }

    @SneakyThrows
    @Test
    void isCorrectDatabaseImplementation() {
        CosmosLiquibaseDatabase database = new CosmosLiquibaseDatabase();
        assertThat(database.isCorrectDatabaseImplementation(null)).isFalse();
        assertThat(database.isCorrectDatabaseImplementation(new JdbcConnection())).isFalse();
        assertThat(database.isCorrectDatabaseImplementation(new CosmosConnection())).isTrue();
    }
}