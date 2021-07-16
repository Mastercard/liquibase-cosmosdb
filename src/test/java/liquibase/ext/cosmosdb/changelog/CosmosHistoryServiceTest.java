package liquibase.ext.cosmosdb.changelog;

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

import liquibase.database.core.H2Database;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase.COSMOSDB_PRODUCT_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CosmosHistoryServiceTest {

    @Mock
    private CosmosLiquibaseDatabase database;

    private CosmosHistoryService cosmosHistoryService;

    @BeforeAll
    void setUp() {
        cosmosHistoryService = new CosmosHistoryService();
    }

    @Test
    void getPriorityTest() {
        assertThat(cosmosHistoryService.getPriority()).isEqualTo(PRIORITY_SPECIALIZED);
    }

    @Test
    void supportsTest() {
        when(database.getDatabaseProductName()).thenReturn(COSMOSDB_PRODUCT_NAME);

        assertAll(
            () ->  assertThat(cosmosHistoryService.supports(database)).isTrue(),
            () ->  assertThat(cosmosHistoryService.supports(new H2Database())).isFalse()
        );

    }

    @Test
    void canCreateChangeLogTableTest() {
        assertThat(cosmosHistoryService.canCreateChangeLogTable()).isTrue();

    }
}
