package com.therapy.nest.shared.config;

import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "graphql.spqr")
@ConditionalOnProperty(value = "graphql.spqr.gui.enabled", havingValue = "false")
public class GraphQLConfig {

    @Value("${graphql.query.max-depth}")
    private int graphqlQueryMaxDepth;

    @Bean
    public GraphQLSchema graphQLSchema(GraphQLSchemaGenerator schemaGenerator) {
        schemaGenerator.withSchemaProcessors((schemaBuilder, buildContext) -> {
            schemaBuilder.codeRegistry(
                    buildContext
                            .codeRegistry
                            .fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY)
                            .build());
            return schemaBuilder;
        });
        return schemaGenerator.generate();
    }

    @Bean
    public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(graphqlQueryMaxDepth);
    }
}