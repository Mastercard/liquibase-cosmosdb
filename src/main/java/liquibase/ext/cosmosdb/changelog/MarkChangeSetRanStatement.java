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

import liquibase.change.Change;
import liquibase.change.core.TagDatabaseChange;
import liquibase.changelog.ChangeSet;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.ext.cosmosdb.database.CosmosLiquibaseDatabase;
import liquibase.ext.cosmosdb.statement.AbstractCosmosContainerStatement;
import liquibase.nosql.statement.NoSqlExecuteStatement;
import liquibase.util.LiquibaseUtil;
import liquibase.util.StringUtil;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

public class MarkChangeSetRanStatement extends AbstractCosmosContainerStatement
        implements NoSqlExecuteStatement<CosmosLiquibaseDatabase> {

    public static final String COMMAND_NAME = "markChangeSet";

    @Getter
    private final ChangeSet changeSet;

    @Getter
    private final ChangeSet.ExecType execType;

    @Getter
    private final Integer orderExecuted;

    @Getter
    private final String deploymentId;

    public MarkChangeSetRanStatement(final String containerId,
                                     final ChangeSet changeSet,
                                     final ChangeSet.ExecType execType,
                                     final Integer orderExecuted,
                                     final String deploymentId) {
        super(containerId);
        this.changeSet = changeSet;
        this.execType = execType;
        this.orderExecuted = orderExecuted;
        this.deploymentId = deploymentId;
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String toJs() {
        return
                "db."
                        + getCommandName()
                        + "("
                        + containerId
                        + ", "
                        + changeSet
                        + ", "
                        + execType
                        + ", "
                        + orderExecuted
                        + ", "
                        + deploymentId
                        + ");";
    }

    @Override
    public void execute(final CosmosLiquibaseDatabase database) {
        try {
            final ChangeSetRepository repository = new ChangeSetRepository(database.getCosmosDatabase(), containerId);

            if (execType.equals(ChangeSet.ExecType.FAILED) || execType.equals(ChangeSet.ExecType.SKIPPED)) {
                return; //don't mark
            }


            if (execType.ranBefore) {
                //TODO: update
            } else {

                final CosmosRanChangeSet insertRanChangeSet = new CosmosRanChangeSet(
                        UUID.randomUUID().toString()
                        , changeSet.getFilePath()
                        , changeSet.getId()
                        , changeSet.getAuthor()
                        , changeSet.generateCheckSum()
                        , new Date()
                        , extractTag(changeSet)
                        , execType
                        , changeSet.getDescription()
                        , changeSet.getComments()
                        , changeSet.getContexts()
                        , changeSet.getInheritableContexts()
                        , changeSet.getLabels()
                        , deploymentId
                        , orderExecuted
                        , LiquibaseUtil.getBuildVersion()
                );

                repository.create(insertRanChangeSet);
            }

        } catch (final Exception e) {
            throw new UnexpectedLiquibaseException(e);
        }

    }

    public String extractTag(final ChangeSet changeSet) {
        String tag = null;
        for (Change change : changeSet.getChanges()) {
            if (change instanceof TagDatabaseChange) {
                TagDatabaseChange tagChange = (TagDatabaseChange) change;
                tag = StringUtil.trimToNull(tagChange.getTag());
            }
        }
        return tag;
    }
}


