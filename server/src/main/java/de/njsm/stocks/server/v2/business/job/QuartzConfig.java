/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.business.job;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;

@Configuration
class QuartzConfig {

    @Bean
    JobDetail caConsistencyJob() {
        return JobBuilder.newJob()
                .ofType(CaConsistencyCheckJob.class)
                .withIdentity("CA Consistency Check")
                .storeDurably()
                .build();
    }

    @Bean
    Trigger caConsistencyTrigger(@Qualifier("caConsistencyJob") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 30 * * * ? *"))
                .build();
    }

    @Bean
    JobDetail historyCleanJob() {
        return JobBuilder.newJob()
                .ofType(HistoryCleanerJob.class)
                .withIdentity("History Cleaner")
                .storeDurably()
                .build();
    }

    @Bean
    Trigger historyCleanerTrigger(@Qualifier("historyCleanJob") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ? *"))
                .build();
    }

    @Bean
    SchedulerFactoryBean scheduler(
            AutoWiringSpringBeanJobFactory jobFactory,
            Map<String, JobDetail> jobMap,
            Map<String, Trigger> triggers
    ) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        JobDetail[] jobs = jobMap.values().toArray(JobDetail[]::new);
        Trigger[] tr = triggers.values().toArray(Trigger[]::new);
        schedulerFactory.setTriggers(tr);
        schedulerFactory.setJobDetails(jobs);

        schedulerFactory.setJobFactory(jobFactory);
        return schedulerFactory;
    }

    @Bean
    AutoWiringSpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
}
