package com.github.arteam.train_station_suggester;

import com.codahale.metrics.health.HealthCheck;

import java.nio.file.Files;
import java.nio.file.Path;

public class TrainStationHealthCheck extends HealthCheck {

    private final Path indexPath;

    public TrainStationHealthCheck(Path path) {
        this.indexPath = path;
    }

    @Override
    protected Result check() throws Exception {
        return Files.exists(indexPath) ? Result.healthy() : Result.unhealthy("Index is not built");
    }
}
