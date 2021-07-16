package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.util.CosmosPagedIterable;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.statement.DeleteAllContainersStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteAllContainersStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @SneakyThrows
    @Test
    void deleteAllContainersStatementTest(){
        CosmosPagedIterable cosmosPagedIterable = Mockito.mock(CosmosPagedIterable.class);
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.readAllContainers()).thenReturn(cosmosPagedIterable);

        DeleteAllContainersStatement deleteAllContainersStatement = new DeleteAllContainersStatement();
        deleteAllContainersStatement.execute(database);

        assertThat(deleteAllContainersStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(deleteAllContainersStatement.toString()).isNotNull();

        //skipContainer
        deleteAllContainersStatement = new DeleteAllContainersStatement(Collections.singletonList(TEST_CONTAINER_ID));
        deleteAllContainersStatement.execute(database);

        verify(database, times(2)).getCosmosDatabase();
        verify(cosmosDatabase, times(2)).readAllContainers();

    }
}
