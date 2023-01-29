package com.github.arteam.train_station_suggester;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/suggest")
@Produces(MediaType.APPLICATION_JSON)
public class TrainStationResource {

    private final TrainStationSuggester suggester;

    public TrainStationResource(TrainStationSuggester suggester) {
        this.suggester = suggester;
    }

    @GET
    public List<String> suggest(@QueryParam("term") String term) {
        return suggester.suggest(term);
    }
}
