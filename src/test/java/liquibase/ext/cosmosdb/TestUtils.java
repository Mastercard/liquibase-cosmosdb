package liquibase.ext.cosmosdb;

/*-
 * #%L
 * Liquibase CosmosDB Extension
 * %%
 * Copyright (C) 2020 Mastercard
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.change.CheckSum;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.util.file.FilenameUtils;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class TestUtils {
    public static final String APPLICATION_TEST_PROPERTIES_FILE_NAME = "application-test.properties";
    public static final String DB_CONNECTION_URI_PROPERTY = "db.connection.uri";
    public static final String DATABASE_CHANGE_LOG_TABLE_NAME = "DATABASECHANGELOG";
    public static final String DATABASE_CHANGE_LOG_LOCK_TABLE_NAME = "DATABASECHANGELOGLOCK";

    public static final String TEST_UUID = "uuid2";
    public static final Date TEST_DATE = new Date();
    public static final CheckSum TEST_CHECKSUM = CheckSum.compute("CheckSumString");
    public static final Labels TEST_LABELS = new Labels("Label1", "Label2");
    public static final ContextExpression TEST_CONTEXT_EXPRESSION = new ContextExpression("context1", "context2");
    public static final Collection<ContextExpression> TEST_INHERITABLE_EXPRESSION =
        Collections.singletonList(new ContextExpression("inheritableContext1", "inheritableContext2"));
    public static final String TEST_CONTAINER_ID = "test";
    public static final String TEST_CONTAINER_PROPERTIES = "{ \"partitionKey\": {\"paths\": [\"/partitionField1\"], \"kind\": \"Hash\" } }";
    public static final String TEST_STORE_PROC_PROPERTIES = "{\"body\": \"function () {Sample body}\", \"id\": \"sproc_1\"}";
    public static final String TEST_THROUGHPUT_PROPERTIES = "{\"maxThroughput\": 800}";
    public static final String TEST_UPSERT_ITEM_STATEMENT = "{\"id\" : \"1\", \"lastName\" : \"LastName1\", \"firstName\" : \"FirstName1\", \"age\" : \"99\"}";
    public static final String TEST_COSMOS_DB_CONNECTION_STRING = "cosmosdb://ech-0a9d9346:C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==@ech-0a9d9346:8080/testdb1";
    public static final String TEST_WRONG_CONNECTION_STRING = "mongodb://wrong driver name";
    public static final String TEST_JSON_DOCUMENT = "{\"id\" : \"1\", \"lastName\" : \"LastNameRemained1\", \"firstName\" : \"FirstNameShouldBeChanged1\"}";
    public static final String TEST_JSON_QUERY= "{\"query\": \"SELECT * FROM person f WHERE f.id = @id\", \"parameters\": [ {\"name\": \"@id\", \"value\": \"1\" }] }";


    @SneakyThrows
    public static Properties loadProperties() {
        return loadProperties(APPLICATION_TEST_PROPERTIES_FILE_NAME);
    }

    @SneakyThrows
    public static Properties loadProperties(final String propertyFile) {
        final Properties properties = new Properties();
        properties.load(TestUtils.class.getClassLoader().getResourceAsStream(propertyFile));
        return properties;
    }

    public static List<ChangeSet> getChangeSets(final String changeSetPath, final CosmosLiquibaseDatabase database) throws LiquibaseException {
        final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        final ChangeLogParser parser =
            ChangeLogParserFactory.getInstance().getParser(FilenameUtils.getExtension(changeSetPath), resourceAccessor);

        final DatabaseChangeLog changeLog =
            parser.parse(changeSetPath, new ChangeLogParameters(database), resourceAccessor);
        return changeLog.getChangeSets();
    }
}
