package liquibase.ext.cosmosdb.changelog;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTAINER_ID;
import static liquibase.ext.cosmosdb.changelog.MarkChangeSetRanStatement.COMMAND_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarkChangeSetRanStatementTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    @Mock
    private CosmosDatabase cosmosDatabase;

    @Mock
    private ChangeSet changeSet;

    @Mock
    private CosmosContainer cosmosContainer;

    @Mock
    private Change change;

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(change);
    }

    @SneakyThrows
    @ParameterizedTest
    @EnumSource(value = ChangeSet.ExecType.class, names = {"EXECUTED", "SKIPPED", "FAILED", "RERAN"})
    void markChangeSetRanStatementTest(ChangeSet.ExecType execType) {
        when(database.getCosmosDatabase()).thenReturn(cosmosDatabase);
        when(cosmosDatabase.getContainer(any())).thenReturn(cosmosContainer);

        final MarkChangeSetRanStatement markChangeSetRanStatement =
            new MarkChangeSetRanStatement(TEST_CONTAINER_ID, changeSet, execType, 1, TEST_CONTAINER_ID);
        markChangeSetRanStatement.execute(database);

        assertThat(markChangeSetRanStatement.getCommandName()).isEqualTo(COMMAND_NAME);
        assertThat(markChangeSetRanStatement.toString()).isNotNull();

        verify(database).getCosmosDatabase();

    }

    @Test
    public void extractTagTest(){
        when(changeSet.getChanges()).thenReturn(Collections.singletonList(change));

        final MarkChangeSetRanStatement markChangeSetRanStatement =
            new MarkChangeSetRanStatement(TEST_CONTAINER_ID, changeSet, ChangeSet.ExecType.SKIPPED, 1, TEST_CONTAINER_ID);

        assertThat(markChangeSetRanStatement.extractTag(changeSet)).isNull();

    }

    @Test
    public void unexpectedLiquibaseExceptionTest(){
        final MarkChangeSetRanStatement markChangeSetRanStatement =
            new MarkChangeSetRanStatement(TEST_CONTAINER_ID, null, ChangeSet.ExecType.SKIPPED, 1, TEST_CONTAINER_ID);

        assertThrows(UnexpectedLiquibaseException.class, () -> { markChangeSetRanStatement.execute(database); });

    }
}
