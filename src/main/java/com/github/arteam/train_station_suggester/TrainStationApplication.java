package com.github.arteam.train_station_suggester;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.AutoCloseableManager;
import io.dropwizard.setup.Environment;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.analyzing.BlendedInfixSuggester;
import org.apache.lucene.store.MMapDirectory;

import java.nio.file.Path;

public class TrainStationApplication extends Application<TrainStationConfiguration> {

    public static final Path PATH = Path.of("/home/artem/Downloads/ts-index");

    @Override
    public void run(TrainStationConfiguration trainStationConfiguration, Environment environment) throws Exception {
        var fsDirectory = MMapDirectory.open(PATH);
        var analyzer = new StandardAnalyzer();
        var suggester = new BlendedInfixSuggester(fsDirectory, analyzer);
        var trainStationIndexBuilder = new TrainStationIndexBuilder(PATH, suggester);
        environment.jersey().register(new TrainStationResource(new TrainStationSuggester(suggester)));
        environment.healthChecks().register("train-station", new TrainStationHealthCheck());
        environment.lifecycle().manage(trainStationIndexBuilder);
        environment.lifecycle().manage(new AutoCloseableManager(fsDirectory));
        environment.lifecycle().manage(new AutoCloseableManager(analyzer));
        environment.lifecycle().manage(new AutoCloseableManager(suggester));
    }

    public static void main(String[] args) throws Exception {
        new TrainStationApplication().run(args);
    }
}
