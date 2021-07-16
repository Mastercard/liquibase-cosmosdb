package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosScripts;
import com.azure.cosmos.CosmosStoredProcedure;
import com.azure.cosmos.util.CosmosPagedIterable;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.TestUtils.TEST_STORE_PROC_PROPERTIES;
import static liquibase.ext.cosmosdb.statement.DeleteStoredProcedureStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteStoredProcedureStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosScripts cosmosScripts;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @Mock
    CosmosStoredProcedure cosmosStoredProcedure;

    @SneakyThrows
    @Test
    void deleteStoredProcedureStatementTest() {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);
        when(cosmosContainer.getScripts()).thenReturn(cosmosScripts);
        when(cosmosScripts.getStoredProcedure(any())).thenReturn(cosmosStoredProcedure);

        final DeleteStoredProcedureStatement deleteStoredProcedureStatement = new DeleteStoredProcedureStatement(TEST_CONTAINER_ID,TEST_STORE_PROC_PROPERTIES, false);
        deleteStoredProcedureStatement.execute(database);

        assertThat(deleteStoredProcedureStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(deleteStoredProcedureStatement.toString()).isNotNull();

        verify(database).getCosmosDatabase();
        verify(cosmosDatabase).getContainer(any());
        verify(cosmosContainer).getScripts();
        verify(cosmosScripts).getStoredProcedure(any());
        verify(cosmosStoredProcedure).delete();

    }

    @SneakyThrows
    @Test
    void skipMissingDeleteStoredProcedureTest() {
        CosmosPagedIterable cosmosPagedIterable = Mockito.mock(CosmosPagedIterable.class);
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);
        when(cosmosContainer.getScripts()).thenReturn(cosmosScripts);
        when(cosmosScripts.readAllStoredProcedures()).thenReturn(cosmosPagedIterable);

        final DeleteStoredProcedureStatement deleteStoredProcedureStatement = new DeleteStoredProcedureStatement(TEST_CONTAINER_ID,TEST_STORE_PROC_PROPERTIES, true);
        deleteStoredProcedureStatement.execute(database);

        verify(database).getCosmosDatabase();
        verify(cosmosDatabase).getContainer(any());
        verify(cosmosContainer).getScripts();
        verify(cosmosScripts, never()).getStoredProcedure(any());
        verify(cosmosStoredProcedure, never()).delete();

    }
}
