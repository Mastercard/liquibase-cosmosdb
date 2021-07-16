package liquibase.ext.cosmosdb.changelog;

import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.changelog.ChangeSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

import static liquibase.ext.cosmosdb.TestUtils.TEST_CHECKSUM;
import static liquibase.ext.cosmosdb.TestUtils.TEST_CONTEXT_EXPRESSION;
import static liquibase.ext.cosmosdb.TestUtils.TEST_DATE;
import static liquibase.ext.cosmosdb.TestUtils.TEST_INHERITABLE_EXPRESSION;
import static liquibase.ext.cosmosdb.TestUtils.TEST_LABELS;
import static liquibase.ext.cosmosdb.TestUtils.TEST_UUID;
import static liquibase.nosql.changelog.AbstractNoSqlItemToDocumentConverter.ISO_8601_UTC_DATETIME_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChangeSetToDocumentConverterTest {

    private ChangeSetToDocumentConverter changeSetToDocumentConverter;

    private String actualDate;

    @BeforeAll
    void setUp() {
        changeSetToDocumentConverter = new ChangeSetToDocumentConverter();
        actualDate = new SimpleDateFormat(ISO_8601_UTC_DATETIME_FORMAT).format(TEST_DATE);
    }

    @Test
    void toDateTest() {
        assertAll(
            () ->  assertThat(changeSetToDocumentConverter.toDate(actualDate)).isEqualTo(TEST_DATE),
            () ->  assertThat(changeSetToDocumentConverter.toDate(null)).isNull(),
            () ->  assertThatIllegalArgumentException().isThrownBy(() -> changeSetToDocumentConverter.toDate(TEST_DATE.toString()))
        );

    }

    @Test
    void fromDateTest() {
        assertAll(
            () ->  assertThat(changeSetToDocumentConverter.fromDate(TEST_DATE)).isEqualTo(actualDate),
            () ->  assertThat(changeSetToDocumentConverter.fromDate(null)).isNull()
        );

    }

    @Test
    void buildLabelsTest() {
        assertAll(
            () ->  assertThat(changeSetToDocumentConverter.buildLabels(new Labels("Label1", "Label2"))).isEqualTo(TEST_LABELS.toString()),
            () ->  assertThat(changeSetToDocumentConverter.buildLabels(null)).isNull(),
            () ->  assertThat(changeSetToDocumentConverter.buildLabels(new Labels())).isNull()
        );

    }

    @Test
    void buildFullContextTest() {
        assertAll(
            () ->  assertThat(changeSetToDocumentConverter.buildFullContext(TEST_CONTEXT_EXPRESSION, TEST_INHERITABLE_EXPRESSION)).isNotNull(),
            () ->  assertThat(changeSetToDocumentConverter.buildFullContext(null,null)).isNull(),
            () ->  assertThat(changeSetToDocumentConverter.buildFullContext(new ContextExpression(),
                Collections.singletonList(new ContextExpression("inheritableContext1", "inheritableContext2")))).isNull()
        );

    }

    @Test
    void testToFromDocument(){
        final CosmosRanChangeSet cosmosRanChangeSetActual = new CosmosRanChangeSet(
            TEST_UUID,
            "FilePath",
            "ChangeSetId",
            "Author",
            TEST_CHECKSUM,
            TEST_DATE,
            "Tag",
            ChangeSet.ExecType.EXECUTED,
            "Description",
            "Comments",
            TEST_CONTEXT_EXPRESSION,
            TEST_INHERITABLE_EXPRESSION,
            TEST_LABELS,
            "DeploymentId",
            1,
            "BuildVersion"
        );

        final Map<String, Object> document = changeSetToDocumentConverter.toDocument(cosmosRanChangeSetActual);
        assertThat(document.size()).isEqualTo(15);

        final CosmosRanChangeSet cosmosRanChangeSetConverted = changeSetToDocumentConverter.fromDocument(document);

        assertAll(
            () ->  assertThat(cosmosRanChangeSetConverted).isNotNull(),
            () ->  assertThat(cosmosRanChangeSetConverted.getUuid()).isEqualTo(TEST_UUID),
            () ->  assertThat(cosmosRanChangeSetConverted.getChangeLog()).isEqualTo("FilePath"),
            () ->  assertThat(cosmosRanChangeSetConverted.getId()).isEqualTo("ChangeSetId"),
            () ->  assertThat(cosmosRanChangeSetConverted.getAuthor()).isEqualTo("Author"),
            () ->  assertThat(cosmosRanChangeSetConverted.getLastCheckSum()).isEqualTo(TEST_CHECKSUM),
            () ->  assertThat(cosmosRanChangeSetConverted.getDateExecuted()).isEqualTo(TEST_DATE),
            () ->  assertThat(cosmosRanChangeSetConverted.getTag()).isEqualTo("Tag"),
            () ->  assertThat(cosmosRanChangeSetConverted.getExecType()).isEqualTo(ChangeSet.ExecType.EXECUTED),
            () ->  assertThat(cosmosRanChangeSetConverted.getDescription()).isEqualTo("Description"),
            () ->  assertThat(cosmosRanChangeSetConverted.getComments()).isEqualTo("Comments"),
            () ->  assertThat(cosmosRanChangeSetConverted.getContextExpression().getContexts()).hasSize(3),
            () ->  assertThat(cosmosRanChangeSetConverted.getLabels().getLabels()).containsAll(TEST_LABELS.getLabels()),
            () ->  assertThat(cosmosRanChangeSetConverted.getDeploymentId()).isEqualTo("DeploymentId"),
            () ->  assertThat(cosmosRanChangeSetConverted.getOrderExecuted()).isEqualTo(1),
            () ->  assertThat(cosmosRanChangeSetConverted.getLiquibase()).isEqualTo("BuildVersion")
        );

    }
}
