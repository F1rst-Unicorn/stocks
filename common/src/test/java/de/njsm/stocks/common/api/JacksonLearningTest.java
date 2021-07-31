package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JacksonLearningTest {

    @Test
    public void getterWithoutPrefixWorks() throws JsonProcessingException {
        String output = new ObjectMapper().writeValueAsString(new Data());

        assertEquals("{\"data\":1109}", output);
    }

    private static final class Data {
        @JsonGetter
        public int data() {
            return 1109;
        }
    }
}
