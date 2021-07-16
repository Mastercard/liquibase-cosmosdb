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

import liquibase.ext.cosmosdb.statement.CountDocumentsInContainerStatement;

public class GetNextChangeSetSequenceValueStatement extends CountDocumentsInContainerStatement {//NOSONAR

    public static final String COMMAND_NAME = "nextChangeSetSequence";

    public GetNextChangeSetSequenceValueStatement(final String containerId) {
        super(containerId);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

}
