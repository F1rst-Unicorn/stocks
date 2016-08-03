import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.junit.Test;

public class HttpsUserContextFactoryTest {

    @Test
    public void testParsing() {
        String input = "/O=stocks/OU=User/CN=jan$1$laptop$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);
        p.getDid();
    }

    @Test
    public void testBasicParsing() {
        String input = "/CN=Jan$1$Handy$13";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);
        p.getDid();
    }
}
