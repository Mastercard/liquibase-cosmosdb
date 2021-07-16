package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.util.CosmosPagedIterable;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.statement.DeleteContainerStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteContainerStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @Mock
    private CosmosContainer cosmosContainer;

    @SneakyThrows
    @Test
    void deleteContainerStatementTest(){
        CosmosPagedIterable cosmosPagedIterable = Mockito.mock(CosmosPagedIterable.class);
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.readAllContainers()).thenReturn(cosmosPagedIterable);
        when(cosmosDatabase.getContainer("test")).thenReturn(cosmosContainer);

        DeleteContainerStatement deleteContainerStatement = new DeleteContainerStatement("test");
        deleteContainerStatement.execute(database);

        assertThat(deleteContainerStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(deleteContainerStatement.toString()).isNotNull();

        //skipContainer
        deleteContainerStatement = new DeleteContainerStatement("test", true);
        deleteContainerStatement.execute(database);

        verify(database, times(2)).getCosmosDatabase();
        verify(cosmosDatabase).readAllContainers();
        verify(cosmosContainer).delete();
    }
}
