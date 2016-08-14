import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.junit.Assert;
import org.junit.Test;

public class HttpsUserContextFactoryTest {

    @Test
    public void testParsing() {
        String input = "/O=stocks/OU=User/CN=jan$1$laptop$2";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);
        Assert.assertEquals("jan", p.getUsername());
        Assert.assertEquals("laptop", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(2, p.getDid());
    }

    @Test
    public void testBasicParsing() {
        String input = "/CN=Jan$1$Handy$2";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        Assert.assertEquals("Jan", p.getUsername());
        Assert.assertEquals("Handy", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(2, p.getDid());
    }

    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        Assert.assertEquals("", p.getUsername());
        Assert.assertEquals("", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(1, p.getDid());
    }

    @Test(expected = SecurityException.class)
    public void testMalformed() {
        String input = "/CN=$1$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        Assert.assertEquals("", p.getUsername());
        Assert.assertEquals("", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(1, p.getDid());
    }
}
