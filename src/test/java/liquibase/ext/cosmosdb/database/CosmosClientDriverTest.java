package liquibase.ext.cosmosdb.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.DriverPropertyInfo;
import java.util.Properties;

import static liquibase.ext.cosmosdb.TestUtils.TEST_COSMOS_DB_CONNECTION_STRING;
import static liquibase.ext.cosmosdb.TestUtils.TEST_WRONG_CONNECTION_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CosmosClientDriverTest {

    private CosmosClientDriver cosmosClientDriver;

    @BeforeAll
    void setUp() {
        cosmosClientDriver = new CosmosClientDriver();
    }

    @Test
    void unsupportedConnectionTest(){
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
            () -> cosmosClientDriver.connect(TEST_WRONG_CONNECTION_STRING, new Properties())
        );
    }

    @Test
    void cosmosClientDriverPropertiesTest(){
        assertAll(
            () ->  assertThat(cosmosClientDriver.getMajorVersion()).isEqualTo(0),
            () ->  assertThat(cosmosClientDriver.getMinorVersion()).isEqualTo(0),
            () ->  assertThat(cosmosClientDriver.jdbcCompliant()).isFalse(),
            () ->  assertThat(cosmosClientDriver.getPropertyInfo(TEST_COSMOS_DB_CONNECTION_STRING, new Properties())).isInstanceOf(DriverPropertyInfo[].class)
        );

    }

    @Test
    void acceptsURLTest(){
        assertAll(
            () ->  assertThat(cosmosClientDriver.acceptsURL(TEST_WRONG_CONNECTION_STRING)).isFalse(),
            () ->  assertThat(cosmosClientDriver.acceptsURL(TEST_COSMOS_DB_CONNECTION_STRING)).isTrue()
        );

    }
}
