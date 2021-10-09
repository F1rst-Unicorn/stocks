package de.njsm.stocks.server.v2.db;

public enum TechnicalUseCase {

    JOB_RUNNER("job-runner");

    private final String dbIdentifier;

    TechnicalUseCase(String dbIdentifier) {
        this.dbIdentifier = dbIdentifier;
    }

    public String getDbIdentifier() {
        return dbIdentifier;
    }
}
