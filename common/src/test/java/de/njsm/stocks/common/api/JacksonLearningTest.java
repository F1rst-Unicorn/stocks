package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class JacksonLearningTest {

    @Test
    public void getterWithoutPrefixWorks() throws JsonProcessingException {
        String output = new ObjectMapper().writeValueAsString(new Data());

        assertEquals("{\"data\":1109}", output);
    }

    @Test
    public void canDeserialiseListResponse() throws JsonProcessingException {
        ListResponse<Integer> output = new ObjectMapper().readValue("{\"status\":0,\"data\":[]}", ListResponse.class);

        assertEquals(StatusCode.SUCCESS, output.getStatus());
        assertEquals(Collections.emptyList(), output.getData());
    }

    private static final class Data {
        @JsonGetter
        public int data() {
            return 1109;
        }
    }
}
