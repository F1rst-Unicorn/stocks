import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.junit.Test;

public class HttpsUserContextFactoryTest {

    @Test
    public void testParsing() {
        String input = "/O=stocks/OU=User/CN=jan$1$laptop$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        uut.parseSubjectName(input);
    }
}
