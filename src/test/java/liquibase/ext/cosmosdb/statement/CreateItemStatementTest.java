package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.TestUtils.TEST_JSON_DOCUMENT;
import static liquibase.ext.cosmosdb.statement.CreateItemStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateItemStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @SneakyThrows
    @Test
    void createItemStatementTest() {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);

        CreateItemStatement createItemStatement = new CreateItemStatement(TEST_CONTAINER_ID, TEST_JSON_DOCUMENT);
        createItemStatement.execute(database);

        assertThat(createItemStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(createItemStatement.toString()).isNotNull();

        verify(database).getCosmosDatabase();
        verify(cosmosDatabase).getContainer(any());

    }
}
