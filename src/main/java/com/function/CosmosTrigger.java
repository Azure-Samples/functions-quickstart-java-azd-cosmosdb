package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Cosmos DB Trigger function that responds to changes in a Cosmos DB container.
 * This function automatically executes when documents are added, updated, or deleted
 * in the configured container.
 *
 * <p>Configuration is loaded from environment variables:
 * <ul>
 *   <li>COSMOS_CONNECTION__accountEndpoint: The Cosmos DB account endpoint</li>
 *   <li>COSMOS_DATABASE_NAME: The name of the database to monitor</li>
 *   <li>COSMOS_CONTAINER_NAME: The name of the container to monitor</li>
 * </ul>
 *
 * <p>The function uses a lease container to track processed changes and support
 * multiple instances.
 *
 * <p>Example document that would trigger this function when added/modified in Cosmos DB:
 * <pre>{@code
 * {
 *   "id": "doc-001",
 *   "Text": "This is a sample document",
 *   "Number": 42,
 *   "Boolean": true
 * }
 * }</pre>
 */
public class CosmosTrigger {

    /**
     * Processes changes to documents in the Cosmos DB container.
     *
     * @param items   Array of JSON strings representing the modified documents.
     * @param context The execution context for logging and function metadata.
     */
    @FunctionName("cosmos_trigger")
    public void run(
        @CosmosDBTrigger(
            name = "input",
            databaseName = "%COSMOS_DATABASE_NAME%",
            containerName = "%COSMOS_CONTAINER_NAME%",
            connection = "COSMOS_CONNECTION",
            leaseContainerName = "leases",
            createLeaseContainerIfNotExists = true
        ) String[] items,
        final ExecutionContext context
    ) {
        if (items != null && items.length > 0) {
            context.getLogger().info("Documents modified: " + items.length);
            JsonObject firstDoc = JsonParser.parseString(items[0]).getAsJsonObject();
            context.getLogger().info("First document Id: " + firstDoc.get("id").getAsString());
        }
    }
}
