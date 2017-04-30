package de.njsm.stocks.client;


import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.PrintableException;
import de.njsm.stocks.client.frontend.MainHandler;
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
            InitManager im = context.getBean(InitManager.class);
            Configuration c = context.getBean(Configuration.class);

            im.initialise();
            c.initialise();

            MainHandler runner = context.getBean(MainHandler.class);
            runner.run(args);
        } catch (PrintableException e) {
            exitCode = handleError(e);
        } catch (Exception e) {
            exitCode = handleUnexpectedError(e);
        } finally {
            LOG.info("Shutting down");
            threadPool.shutdown();
        }
        System.exit(exitCode);
    }

    private static int handleUnexpectedError(Exception e) {
        System.out.println("An unexpected error has occured!");
        System.out.println("Please consider sending the log file at ");
        System.out.println("~/.stocks/stocks.log to the developers");
        return 2;
    }

    private static int handleError(PrintableException e) {
        LOG.error("", e);
        System.out.println(e.getMessage());
        System.out.println("For details consider the log file at " +
                "~/.stocks/stocks.log");
        return 1;
    }
}
