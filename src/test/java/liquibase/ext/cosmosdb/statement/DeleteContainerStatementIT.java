package liquibase.ext.cosmosdb.statement;

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

import com.azure.cosmos.CosmosException;
import liquibase.ext.cosmosdb.AbstractCosmosWithConnectionIntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.*;

class DeleteContainerStatementIT extends AbstractCosmosWithConnectionIntegrationTest {

    private static final String CONTAINER_NAME_1 = "containerName1";
    private static final String CONTAINER_NAME_2 = "containerName2";

    @SneakyThrows
    @Test
    void testExecute() {

        final CreateContainerStatement createContainerStatement1 = new CreateContainerStatement(CONTAINER_NAME_1);

        final CreateContainerStatement createContainerStatement2 = new CreateContainerStatement(CONTAINER_NAME_2);

        createContainerStatement1.execute(database);
        createContainerStatement2.execute(database);

        final Long countAfterCreated = cosmosDatabase.readAllContainers().stream().count();
        assertThat(countAfterCreated).isEqualTo(2L);

        DeleteContainerStatement deleteContainerStatement
                = new DeleteContainerStatement(CONTAINER_NAME_1);

        deleteContainerStatement.execute(database);

        final Long countAfterDeleted = cosmosDatabase.readAllContainers().stream().count();
        assertThat(countAfterDeleted).isEqualTo(1L);

        // Skip Missing tests
        assertThatCode(() -> new DeleteContainerStatement(CONTAINER_NAME_1 + "notExisting", TRUE).execute(database))
                .doesNotThrowAnyException();
        assertThat(cosmosDatabase.readAllContainers().stream().count()).isEqualTo(1L);

        // Skip missing FALSE should fail
        assertThatExceptionOfType(CosmosException.class)
                .isThrownBy(() -> new DeleteContainerStatement(CONTAINER_NAME_1 + "notExisting", FALSE).execute(database))
                .withMessageContaining("Resource Not Found.");

        assertThat(cosmosDatabase.readAllContainers().stream().count()).isEqualTo(1L);

        // Skip missing NULL should fail
        assertThatExceptionOfType(CosmosException.class)
                .isThrownBy(() -> new DeleteContainerStatement(CONTAINER_NAME_1 + "notExisting", null).execute(database))
                .withMessageContaining("Resource Not Found.");

        assertThat(cosmosDatabase.readAllContainers().stream().count()).isEqualTo(1L);
    }
}
