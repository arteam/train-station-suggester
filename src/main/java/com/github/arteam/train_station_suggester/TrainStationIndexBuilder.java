package com.github.arteam.train_station_suggester;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class TrainStationIndexBuilder {

    private final Path path;
    private final AnalyzingInfixSuggester suggester;

    public TrainStationIndexBuilder(Path path, AnalyzingInfixSuggester suggester) {
        this.path = path;
        this.suggester = suggester;
    }

    private void build() throws IOException {
        if (Files.exists(path)) {
            return;
        }
        try (var is = getClass().getResourceAsStream("/stations.csv");
             var reader = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
             var csvParser = new CSVParser(reader, CSVFormat.Builder.create(CSVFormat.DEFAULT)
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build());
        ) {
            for (CSVRecord csvRecord : csvParser) {
                suggester.add(new BytesRef(csvRecord.get(1)), Set.of(), 0L, null);
            }
            suggester.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
