package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.TestUtils.TEST_JSON_DOCUMENT;
import static liquibase.ext.cosmosdb.TestUtils.TEST_JSON_QUERY;
import static liquibase.ext.cosmosdb.statement.UpdateEachItemStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateEachItemStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @Mock
    private CosmosContainerResponse cosmosContainerResponse;

    @SneakyThrows
    @Test
    void updateEachItemStatementTest() {
        CosmosPagedIterable cosmosPagedIterable = Mockito.mock(CosmosPagedIterable.class);

        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);
        when(cosmosContainer.queryItems((SqlQuerySpec) any(), any(), any())).thenReturn(cosmosPagedIterable);
        when(cosmosContainer.read()).thenReturn(cosmosContainerResponse);
        when(cosmosContainerResponse.getProperties()).thenReturn(new CosmosContainerProperties("test",""));

        UpdateEachItemStatement updateEachItemStatement = new UpdateEachItemStatement(TEST_CONTAINER_ID, TEST_JSON_QUERY, TEST_JSON_DOCUMENT);
        updateEachItemStatement.execute(database);

        assertThat(updateEachItemStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(updateEachItemStatement.toString()).isNotNull();

        verify(database).getCosmosDatabase();
        verify(cosmosDatabase).getContainer(any());
        verify(cosmosContainer).read();
        verify(cosmosContainer).queryItems((SqlQuerySpec) any(), any(), any());
        verify(cosmosContainerResponse).getProperties();

    }
}
