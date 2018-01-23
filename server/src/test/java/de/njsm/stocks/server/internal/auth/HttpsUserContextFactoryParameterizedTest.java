package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class HttpsUserContextFactoryParameterizedTest {

    @Parameterized.Parameter
    public String subject;

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> getTables() {
        return Arrays.asList(
                new Object[][] {
                        {"/CN=Jack$1$Laptop$2/O=stocks/OU=User/O=stocks"},
                        {"/O=stocks/OU=User/CN=Jack$1$Laptop$2"},
                        {"/CN=Jack$1$Laptop$2"},
                        {"CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks"},
                        {"O=stocks,OU=User,CN=Jack$1$Laptop$2"},
                        {"CN=Jack$1$Laptop$2"},
                        {"  CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks     "},
                        {"  O=stocks,   OU=User,CN=Jack$1$Laptop$2  "},
                        {"  CN=Jack$1$Laptop$2"},
                        {"  CN=Jack$1$Laptop$2,O=stocks,OU=User,O=stocks    "},
                        {"  O=stocks,  OU=User,CN=Jack$1$Laptop$2   "},
                        {"CN=Jack$1$Laptop$2"},
                });
    }

    @Test
    public void testParsing() {

        Principals p = HttpsUserContextFactory.parseSubjectName(subject);

        Assert.assertEquals("Jack", p.getUsername());
        Assert.assertEquals("Laptop", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(2, p.getDid());
    }

}
