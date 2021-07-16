package liquibase.ext.cosmosdb.database;

import com.azure.cosmos.CosmosDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static com.azure.cosmos.implementation.apachecommons.lang.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CosmosConnectionTest {

    private static final String TEST_COSMOS_JSON_CONNECTION_STRING_1 = "cosmosdb://{\"accountEndpoint\":\"https://ech-0a9d975b:8080\",\"accountKey\":\"C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==\",\"databaseName\":\"testdb1\"}";

    private static final String DATABASE_PRODUCT_VERSION = "3.12.7";
    private static final int DATABASE_MAJOR_VERSION = 3;
    private static final int DATABASE_MINOR_VERSION = 12;
    private static final String TEST_DATABASE_NAME_COSMOS = "testdb1";
    private static final String COSMOSDB_PRODUCT_NAME = "Cosmos DB";

    private static final String TEST_COSMOS_JSON_CONNECTION_STRING_MISSING_ACCOUNT_ENDPOINT = "cosmosdb://{\"accountKey\":\"C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==\",\"databaseName\":\"testdb1\"}";
    private static final String TEST_COSMOS_JSON_CONNECTION_STRING_MISSING_DATABASE = "cosmosdb://{\"accountEndpoint\":\"https://ech-0a9d975b:8080\",\"accountKey\":\"C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==\"}";
    private static final String TEST_COSMOS_JSON_CONNECTION_STRING_MISSING_ACCOUNT_KEY = "cosmosdb://{\"accountEndpoint\":\"https://ech-0a9d975b:8080\",\"databaseName\":\"testdb1\"}";

    private static final String TEST_COSMOS_URL_CONNECTION_STRING_1 = "cosmosdb://ech-0a9d975b:C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==:8080/testdb1?ssl=false";

    @Mock
    private CosmosClientDriver driverMock;

    @Mock
    private CosmosClientProxy clientMock;

    @Mock
    private CosmosDatabase databaseMock;

    @Mock
    private Properties propertiesMock;

    private CosmosConnection cosmosConnection;

    @BeforeAll
    void setUp() {
        cosmosConnection = new CosmosConnection();
    }

    @SneakyThrows
    @Test
    void getURLTest() {
        assertThat(cosmosConnection.getURL()).isEqualTo(EMPTY);

        when(driverMock.connect(any())).thenReturn(clientMock);
        cosmosConnection.open(TEST_COSMOS_JSON_CONNECTION_STRING_1, driverMock, propertiesMock);
        assertThat(cosmosConnection.getURL()).isEqualTo(TEST_COSMOS_JSON_CONNECTION_STRING_1);
    }

    @SneakyThrows
    @Test
    void openCloseTest() {
        assertThat(cosmosConnection.getCosmosDatabase()).isNull();
        assertThat(cosmosConnection.getCosmosClient()).isNull();
        assertThat(cosmosConnection.isClosed()).isTrue();

        when(driverMock.connect(any())).thenReturn(clientMock);
        when(clientMock.getDatabase(any())).thenReturn(databaseMock);
        cosmosConnection.open(TEST_COSMOS_JSON_CONNECTION_STRING_1, driverMock, propertiesMock);
        assertThat(cosmosConnection.getCosmosDatabase()).isNotNull();
        assertThat(cosmosConnection.getCosmosClient()).isNotNull();
        assertThat(cosmosConnection.isClosed()).isFalse();
        assertThat(cosmosConnection.getCatalog()).isEqualTo(TEST_DATABASE_NAME_COSMOS);

        cosmosConnection.close();
        assertThat(cosmosConnection.getCosmosDatabase()).isNull();
        assertThat(cosmosConnection.getCosmosClient()).isNull();
        assertThat(cosmosConnection.isClosed()).isTrue();
    }

    @Test
    @SneakyThrows
    void shouldNotCloseResetIfAlreadyClosed() {

        when(driverMock.connect(any())).thenReturn(clientMock);
        when(clientMock.getDatabase(any())).thenReturn(databaseMock);
        cosmosConnection.open(TEST_COSMOS_JSON_CONNECTION_STRING_1, driverMock, propertiesMock);

        cosmosConnection.close();

        assertThatCode(cosmosConnection::close).doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    void cosmosConnectionPropertiesTest(){

        assertThat(cosmosConnection.getDatabaseProductVersion()).isEqualTo(DATABASE_PRODUCT_VERSION);
        assertThat(cosmosConnection.getDatabaseMajorVersion()).isEqualTo(DATABASE_MAJOR_VERSION);
        assertThat(cosmosConnection.getDatabaseMinorVersion()).isEqualTo(DATABASE_MINOR_VERSION);
        assertThat(cosmosConnection.getDatabaseProductName()).isEqualTo(COSMOSDB_PRODUCT_NAME);

    }

    @Test
    @SneakyThrows
    void shouldNotOpenConnectionIfMissingProperties(){

        assertThatIllegalArgumentException().isThrownBy(() -> cosmosConnection.open(TEST_COSMOS_JSON_CONNECTION_STRING_MISSING_ACCOUNT_ENDPOINT, driverMock, propertiesMock));
        assertThatIllegalArgumentException().isThrownBy(() -> cosmosConnection.open(TEST_COSMOS_JSON_CONNECTION_STRING_MISSING_DATABASE, driverMock, propertiesMock));
        assertThatIllegalArgumentException().isThrownBy(() -> cosmosConnection.open(TEST_COSMOS_JSON_CONNECTION_STRING_MISSING_ACCOUNT_KEY, driverMock, propertiesMock));
        
    }

}