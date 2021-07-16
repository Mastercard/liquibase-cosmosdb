package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosDatabase;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_PROPERTIES;
import static liquibase.ext.cosmosdb.statement.CreateContainerStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateContainerStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @SneakyThrows
    @Test
    void createContainerStatementTest() {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);

        CreateContainerStatement createContainerStatement = new CreateContainerStatement(TEST_CONTAINER_ID, TEST_CONTAINER_PROPERTIES);
        createContainerStatement.execute(database);

        assertThat(createContainerStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(createContainerStatement.toString()).isNotNull();

        createContainerStatement = new CreateContainerStatement(TEST_CONTAINER_ID);
        createContainerStatement.execute(database);

        //containerId: null
        createContainerStatement = new CreateContainerStatement();
        createContainerStatement.execute(database);

        verify(database, times(3)).getCosmosDatabase();

    }
}
