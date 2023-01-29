package com.github.arteam.train_station_suggester;

import com.codahale.metrics.health.HealthCheck;

public class TrainStationHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
