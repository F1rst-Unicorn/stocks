package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.EanNumber;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EanNumberHandlerTest extends DbTestCase {

    private EanNumberHandler uut;

    @Before
    public void setup() {
        uut = new EanNumberHandler(getConnection(),
                getNewResourceIdentifier(),
                new InsertVisitor<>());
    }

    @Test
    public void addAEanNumber() {
        EanNumber data = new EanNumber(1, 1, "Code", 1);

        Validation<StatusCode, Integer> code = uut.add(data);

        assertTrue(code.isSuccess());

        Validation<StatusCode, List<EanNumber>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream().map(f -> f.eanCode).anyMatch(name -> name.equals(data.eanCode)));
    }

    @Test
    public void deleteAEanNumber() {
        EanNumber data = new EanNumber(1, 0, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<EanNumber>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(0, dbData.success().size());
    }

    @Test
    public void invalidDataVersionIsRejected() {
        EanNumber data = new EanNumber(1, 1, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, List<EanNumber>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(1, dbData.success().size());
    }

    @Test
    public void unknownDeletionsAreReported() {
        EanNumber data = new EanNumber(100, 1, "EAN BEER", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }
}