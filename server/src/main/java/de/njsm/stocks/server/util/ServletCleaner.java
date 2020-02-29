/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.netflix.hystrix.Hystrix;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import rx.schedulers.Schedulers;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class ServletCleaner implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {}

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        closeConnectionPool(sce);
        closeHystrix(sce);
        closeQuartzThreadPool(sce);
        closePgDriver(sce);
    }

    private void closePgDriver(ServletContextEvent sce) {
        Enumeration<Driver> e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(e.nextElement());
            } catch (SQLException ex) {
                sce.getServletContext().log("Failed to unload JDBC driver", ex);
            }
        }
    }

    private void closeConnectionPool(ServletContextEvent sce) {
        ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        if (springContext != null) {
            ComboPooledDataSource ds = springContext.getBean("datasource", ComboPooledDataSource.class);
            ds.close();
        }
    }

    private void closeQuartzThreadPool(ServletContextEvent sce) {
        ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        if (springContext != null) {
            SchedulerFactoryBean ds = springContext.getBean("scheduler", SchedulerFactoryBean.class);
            try {
                ds.getScheduler().shutdown(true);
            } catch (SchedulerException e) {
                sce.getServletContext().log("Quartz shutdown failed", e);
            }
        }
    }

    // https://github.com/Netflix/Hystrix/issues/816
    // https://stackoverflow.com/questions/37009425/fixing-the-web-application-root-created-a-threadlocal-with-key-of-type-com-n
    private void closeHystrix(ServletContextEvent sce) {
        Hystrix.reset(30, TimeUnit.SECONDS);

        try {
            Field currentCommandField = ReflectionUtils.findField(Hystrix.class, "currentCommand");
            ReflectionUtils.makeAccessible(currentCommandField);

            @SuppressWarnings("rawtypes")
            ThreadLocal currentCommand = (ThreadLocal) currentCommandField.get(null);
            currentCommand.remove();
        } catch(Exception e) {
            sce.getServletContext().log("Failed to clean hystrix thread-locals", e);
        }
        Schedulers.shutdown();
    }
}
