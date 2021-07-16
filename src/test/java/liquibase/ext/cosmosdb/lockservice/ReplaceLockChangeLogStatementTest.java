package liquibase.ext.cosmosdb.lockservice;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReplaceLockChangeLogStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @SneakyThrows
    @Test
    void replaceLockChangeLogStatementTest(){
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);

        ReplaceLockChangeLogStatement replaceLockChangeLogStatement = new ReplaceLockChangeLogStatement(TEST_CONTAINER_ID, false);

        assertThat(replaceLockChangeLogStatement.replace(database)).isEqualTo(1);
        assertThat(replaceLockChangeLogStatement.update(database)).isEqualTo(1);
        assertThat(replaceLockChangeLogStatement.toJs()).isNotNull();

        verify(database, times(2)).getCosmosDatabase();
        verify(cosmosDatabase, times(2)).getContainer(any());

    }
}
