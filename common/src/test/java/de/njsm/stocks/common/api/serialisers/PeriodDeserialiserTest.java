package de.njsm.stocks.common.api.serialisers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.njsm.stocks.common.api.FoodForGetting;
import org.junit.Test;

import java.time.Period;

import static org.junit.Assert.assertEquals;

public class PeriodDeserialiserTest {

    @Test
    public void deserialisingWorks() throws JsonProcessingException {
        String input = "{\"id\":1,\"version\":0,\"name\":\"name\",\"toBuy\":false," +
                "\"expirationOffset\":2,\"location\":1,\"description\":\"\",\"storeUnit\":1}";

        FoodForGetting output = new ObjectMapper().readValue(input, FoodForGetting.class);

        assertEquals(Period.ZERO.plusDays(2), output.expirationOffset());
    }
}
