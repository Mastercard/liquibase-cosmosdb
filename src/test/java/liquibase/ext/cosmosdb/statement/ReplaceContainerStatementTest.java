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
import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_PROPERTIES;
import static liquibase.ext.cosmosdb.TestUtils.TEST_THROUGHPUT_PROPERTIES;
import static liquibase.ext.cosmosdb.statement.ReplaceContainerStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReplaceContainerStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @SneakyThrows
    @Test
    void replaceContainerStatementTest() {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);

        final ReplaceContainerStatement replaceContainerStatement = new ReplaceContainerStatement(TEST_CONTAINER_ID, TEST_CONTAINER_PROPERTIES, TEST_THROUGHPUT_PROPERTIES);
        assertThat(replaceContainerStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        replaceContainerStatement.execute(database);
        assertThat(replaceContainerStatement.toString()).isNotNull();

        verify(database, times(2)).getCosmosDatabase();
        verify(cosmosDatabase, times(2)).getContainer(any());

    }

    @SneakyThrows
    @Test
    void nullReplaceContainerStatementTest() {
        final ReplaceContainerStatement replaceContainerStatement = new ReplaceContainerStatement(null, null, null);
        replaceContainerStatement.execute(database);
        verify(database, never()).getCosmosDatabase();
        verify(cosmosDatabase, never()).getContainer(any());

    }
}
