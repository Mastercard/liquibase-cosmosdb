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

import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.change.CheckSum;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(callSuper = true)
public class CosmosRanChangeSet extends RanChangeSet {

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String FILE_NAME = "fileName";
        public static final String CHANGE_SET_ID = "changeSetId";
        public static final String AUTHOR = "author";
        public static final String LAST_CHECK_SUM = "md5sum";
        public static final String DATE_EXECUTED = "dateExecuted";
        public static final String TAG = "tag";
        public static final String EXEC_TYPE = "execType";
        public static final String DESCRIPTION = "description";
        public static final String COMMENTS = "comments";
        public static final String CONTEXT_EXPRESSION = "contexts";
        public static final String LABELS = "labels";
        public static final String DEPLOYMENT_ID = "deploymentId";
        public static final String ORDER_EXECUTED = "orderExecuted";
        public static final String LIQUIBASE = "liquibase";
    }

    @Getter
    @Setter
    private String uuid;

    @Getter
    @Setter
    private Collection<ContextExpression> inheritableContexts;

    @Getter
    @Setter
    private String liquibase;

    public CosmosRanChangeSet(final String uuid, final String changeLog, final String id, final String author, final CheckSum lastCheckSum, final Date dateExecuted //NOSONAR
            , final String tag, final ChangeSet.ExecType execType, final String description, final String comments, final ContextExpression contextExpression, final Collection<ContextExpression> inheritableContexts
            , final Labels labels, final String deploymentId, final Integer orderExecuted, final String liquibase) {
        super(changeLog, id, author, lastCheckSum, dateExecuted, tag, execType, description, comments, contextExpression, labels, deploymentId);
        super.setOrderExecuted(orderExecuted);
        this.uuid = uuid;
        this.inheritableContexts = inheritableContexts;
        this.liquibase = liquibase;
    }

    public CosmosRanChangeSet(final ChangeSet changeSet, final ChangeSet.ExecType execType, final ContextExpression contextExpression, final Labels labels) {
        super(changeSet, execType, contextExpression, labels);
    }
}
