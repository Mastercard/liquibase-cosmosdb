package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.TestUtils.TEST_UPSERT_ITEM_STATEMENT;
import static liquibase.ext.cosmosdb.statement.UpsertItemStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UpsertItemStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @Mock
    CosmosContainerResponse cosmosContainerResponse;

    @SneakyThrows
    @Test
    void upsertItemStatementTest() {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);
        when(cosmosContainer.read()).thenReturn(cosmosContainerResponse);
        when(cosmosContainerResponse.getProperties()).thenReturn(new CosmosContainerProperties("test",""));

        final UpsertItemStatement upsertItemStatement = new UpsertItemStatement(TEST_CONTAINER_ID, TEST_UPSERT_ITEM_STATEMENT);
        upsertItemStatement.execute(database);

        assertThat(upsertItemStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(upsertItemStatement.toString()).isNotNull();

        verify(database).getCosmosDatabase();
        verify(cosmosDatabase).getContainer(any());
        verify(cosmosContainer).read();
        verify(cosmosContainerResponse).getProperties();

    }
}
