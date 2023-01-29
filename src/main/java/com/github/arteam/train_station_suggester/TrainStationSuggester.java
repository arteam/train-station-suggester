package com.github.arteam.train_station_suggester;

import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;

import java.io.IOException;
import java.util.List;

public class TrainStationSuggester {

    private final AnalyzingInfixSuggester suggester;

    public TrainStationSuggester(AnalyzingInfixSuggester suggester) {
        this.suggester = suggester;
    }

    public List<String> suggest(String term) {
        try {
            return suggester.lookup(term, 5, true, false).stream().map(l -> l.key.toString()).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
