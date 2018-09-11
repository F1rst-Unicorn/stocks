package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class PrincipalFilterParameterizedTest {

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

        Principals p = PrincipalFilter.parseSubjectName(subject);

        Assert.assertEquals("Jack", p.getUsername());
        Assert.assertEquals("Laptop", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(2, p.getDid());
    }

}
