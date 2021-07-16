package liquibase.ext.cosmosdb.parser.json;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.ext.cosmosdb.change.AbstractCosmosChangeTest;
import liquibase.nosql.parser.json.JsonNoSqlChangeLogParser;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonNoSqlChangeLogParserTest extends AbstractCosmosChangeTest {

    @Mock
    private ResourceAccessor resourceAccessor;

    @Test
    void getPriorityTest() {
        assertThat(new JsonNoSqlChangeLogParser().getPriority()).isEqualTo(PRIORITY_SPECIALIZED);
    }

    @Test
    void supportsFileExtensionTest() {

        String changeLogXmlFile = "liquibase/ext/changelog.test.xml";
        String changeLogJsonFile = "liquibase/ext/changelog.test.json";

        JsonNoSqlChangeLogParser jsonNoSqlChangeLogParser = new JsonNoSqlChangeLogParser();

        assertThat(jsonNoSqlChangeLogParser.supports(changeLogXmlFile, resourceAccessor)).isFalse();
        assertThat(jsonNoSqlChangeLogParser.supports(changeLogJsonFile, resourceAccessor)).isTrue();
    }

    @SneakyThrows
    @Test
    void parseTest(){
        final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        String changeSetPath = "liquibase/ext/changelog.generic.test.json";
        String emptyChangeSetPath = "liquibase/ext/changelog.empty.test.json";
        String invalidChangeSetPath = "liquibase/test/changelog.empty.test.json";

        JsonNoSqlChangeLogParser jsonNoSqlChangeLogParser = new JsonNoSqlChangeLogParser();

        final DatabaseChangeLog changeSets =
            jsonNoSqlChangeLogParser.parse(changeSetPath, new ChangeLogParameters(database), resourceAccessor);
        assertThat(changeSets.getChangeSets()).isNotNull().hasSize(1);

        Throwable exception = assertThrows(ChangeLogParseException.class, () -> jsonNoSqlChangeLogParser.parse(emptyChangeSetPath, new ChangeLogParameters(database), resourceAccessor));
        assertThat(exception.getMessage()).isEqualTo("Empty file " + emptyChangeSetPath);

        exception = assertThrows(ChangeLogParseException.class, () -> jsonNoSqlChangeLogParser.parse(invalidChangeSetPath, new ChangeLogParameters(database), resourceAccessor));
        assertThat(exception.getMessage()).isEqualTo(invalidChangeSetPath + " does not exist");
    }
}
