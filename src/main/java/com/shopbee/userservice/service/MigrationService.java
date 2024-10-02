package com.shopbee.userservice.service;

import io.quarkus.liquibase.LiquibaseFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSetStatus;
import liquibase.exception.LiquibaseException;

import java.util.List;

@ApplicationScoped
public class MigrationService {

    @Inject
    LiquibaseFactory liquibaseFactory;

    public void checkMigration() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            List<ChangeSetStatus> status = liquibase.getChangeSetStatuses(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
