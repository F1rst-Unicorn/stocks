package de.njsm.stocks.common.api.serialisers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.njsm.stocks.common.api.RecipeForGetting;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class DurationDeserialiserTest {
    @Test
    public void deserialisingWorks() throws JsonProcessingException {
        String input = "{\"id\":1,\"version\":0,\"name\":\"name\"," +
                "\"duration\":\"PT2H\",\"instructions\":\"\"}";

        RecipeForGetting output = new ObjectMapper().readValue(input, RecipeForGetting.class);

        assertEquals(Duration.ZERO.plusHours(2), output.duration());
    }
}
