package liquibase.ext.cosmosdb.lockservice;

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

import liquibase.nosql.changelog.AbstractNoSqlItemToDocumentConverter;

import java.util.HashMap;
import java.util.Map;

import static com.azure.cosmos.implementation.Constants.Properties.ID;
import static com.azure.cosmos.implementation.apachecommons.lang.StringUtils.EMPTY;
import static java.util.Optional.ofNullable;

public class ChangeLogLockToDocumentConverter extends AbstractNoSqlItemToDocumentConverter<CosmosChangeLogLock, Map<String, Object>> {
    @Override
    public Map<String, Object> toDocument(final CosmosChangeLogLock item) {
        //TODO: Find a solution to use Document from azure instead of Map
        // Changed from Document as it fails when doing replace, upsert with :
        // Cosmos DB Error: PartitionKey extracted from document doesn't match the one specified in the header

        final Map<String, Object> document = new HashMap<>();
        document.put(ID, Integer.toString(item.getId()));
        document.put(CosmosChangeLogLock.Fields.lockGranted, fromDate(item.getLockGranted()));
        document.put(CosmosChangeLogLock.Fields.lockedBy, item.getLockedBy());
        document.put(CosmosChangeLogLock.Fields.locked, item.getLocked());

        return document;
    }

    @Override
    public CosmosChangeLogLock fromDocument(final Map<String, Object> document) {
        return CosmosChangeLogLock.builder()
                .id(ofNullable(document.get(ID)).map(s -> Integer.parseInt((String) s)).orElse(-1))
                .lockGranted(ofNullable(document.get(CosmosChangeLogLock.Fields.lockGranted)).map(s -> toDate((String) s)).orElse(null))
                .lockedBy(ofNullable((String) document.get(CosmosChangeLogLock.Fields.lockedBy)).orElse(EMPTY))
                .locked((Boolean) ofNullable(document.get(CosmosChangeLogLock.Fields.locked)).orElse(null))
                .build();
    }
}
