package de.njsm.stocks.client;


import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.PrintableException;
import de.njsm.stocks.client.frontend.UIFactory;
import de.njsm.stocks.client.init.InitManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    public static void main (String[] args) {
        int exitCode = 0;
        LOG.info("Starting up");

        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
            UIFactory f = context.getBean(UIFactory.class);
            InitManager im = context.getBean(InitManager.class);
            Configuration c = context.getBean(Configuration.class);

            im.initialise();
            c.initialise();

            f.getMainHandler(c).run(args);
        } catch (PrintableException e) {
            exitCode = handleError(e);
        } finally {
            LOG.info("Shutting down");
            threadPool.shutdown();
        }
        System.exit(exitCode);
    }

    private static int handleError(PrintableException e) {
        LOG.error("", e);
        System.err.println(e.getMessage());
        System.err.println("For details consider the log file at " +
                "~/.stocks/stocks.log");
        return 1;
    }
}
