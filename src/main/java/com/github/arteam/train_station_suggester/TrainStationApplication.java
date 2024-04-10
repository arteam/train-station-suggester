package com.github.arteam.train_station_suggester;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.AutoCloseableManager;
import io.dropwizard.setup.Environment;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.analyzing.BlendedInfixSuggester;
import org.apache.lucene.store.MMapDirectory;

import java.nio.file.Files;
import java.nio.file.Path;

public class TrainStationApplication extends Application<TrainStationConfiguration> {


    @Override
    public void run(TrainStationConfiguration trainStationConfiguration, Environment environment) throws Exception {
        Path path = Files.createTempDirectory("ts-index");

        var fsDirectory = MMapDirectory.open(path);
        var analyzer = new StandardAnalyzer();
        var suggester = new BlendedInfixSuggester(fsDirectory, analyzer);
        var trainStationIndexBuilder = new TrainStationIndexBuilder(path, suggester);
        environment.jersey().register(new TrainStationResource(new TrainStationSuggester(suggester)));
        environment.healthChecks().register("train-station", new TrainStationHealthCheck(path));
        environment.lifecycle().manage(trainStationIndexBuilder);
        environment.lifecycle().manage(new AutoCloseableManager(fsDirectory));
        environment.lifecycle().manage(new AutoCloseableManager(analyzer));
        environment.lifecycle().manage(new AutoCloseableManager(suggester));
    }

    public static void main(String[] args) throws Exception {
        new TrainStationApplication().run(args);
    }
}
