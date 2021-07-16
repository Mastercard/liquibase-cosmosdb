package liquibase.ext.cosmosdb.statement;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosScripts;
import com.azure.cosmos.CosmosStoredProcedure;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.TestUtils.TEST_STORE_PROC_PROPERTIES;
import static liquibase.ext.cosmosdb.statement.CreateStoredProcedureStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateStoredProcedureStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosScripts cosmosScripts;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @Mock
    private CosmosStoredProcedure cosmosStoredProcedure;

    @SneakyThrows
    @Test
    void createStoredProcedureStatementTest() {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);
        when(cosmosContainer.getScripts()).thenReturn(cosmosScripts);
        when(cosmosScripts.getStoredProcedure(any())).thenReturn(cosmosStoredProcedure);

        CreateStoredProcedureStatement createStoredProcedureStatement = new CreateStoredProcedureStatement(TEST_CONTAINER_ID, TEST_STORE_PROC_PROPERTIES, true);
        createStoredProcedureStatement.execute(database);

        assertThat(createStoredProcedureStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(createStoredProcedureStatement.toString()).isNotNull();

        //replaceExisting:false
        createStoredProcedureStatement = new CreateStoredProcedureStatement(TEST_CONTAINER_ID, TEST_STORE_PROC_PROPERTIES, false);
        createStoredProcedureStatement.execute(database);

        verify(database, times(2)).getCosmosDatabase();
        verify(cosmosDatabase, times(2)).getContainer(any());
        verify(cosmosContainer, times(2)).getScripts();
        verify(cosmosScripts).getStoredProcedure(any());
        verify(cosmosScripts).createStoredProcedure(any());

    }
}
