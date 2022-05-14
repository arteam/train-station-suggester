package com.github.arteam.train_station_suggester;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.BlendedInfixSuggester;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class Main {

    public static final Path PATH = Path.of("/home/artem/Downloads/ts-index");

    public static void main(String[] args) throws Exception {
        build();
        suggest();
    }

    private static void suggest() throws IOException {
        try (var directory = MMapDirectory.open(PATH);
             var analyzer = new StandardAnalyzer();
             BlendedInfixSuggester suggester = new BlendedInfixSuggester(directory, analyzer);) {
            suggest(suggester, "ber");
            suggest(suggester, "berl");
            suggest(suggester, "berli");
            suggest(suggester, "berlin");
            suggest(suggester, "berlin h");
            suggest(suggester, "berlin hb");
            suggest(suggester, "berlin hbf");
        }
    }

    private static void suggest(BlendedInfixSuggester suggester, String term) throws IOException {
        for (Lookup.LookupResult lookupResult : suggester.lookup(term, 5, true, true)) {
            System.out.println(lookupResult.key);
        }
        System.out.println("=============");
    }

    private static void build() throws IOException {
        if (Files.exists(PATH)) {
            return;
        }
        try (var is = Main.class.getResourceAsStream("/stations.csv");
             var reader = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
             var csvParser = new CSVParser(reader, CSVFormat.Builder.create(CSVFormat.DEFAULT)
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build());
             var directory = MMapDirectory.open(PATH);
             var analyzer = new StandardAnalyzer();
             var suggester = new BlendedInfixSuggester(directory, analyzer)
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
