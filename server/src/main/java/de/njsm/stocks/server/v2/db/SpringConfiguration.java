package de.njsm.stocks.server.v2.db;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration(proxyBeanMethods = false)
public class SpringConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties("de.njsm.stocks.server.db")
	public Properties dataSourceProperties() {
		return new Properties();
	}

    @Bean
    public DataSource postgresql(@Qualifier("dataSourceProperties") Properties properties) throws SQLException {
        var postgresql = new PGSimpleDataSource();
        postgresql.setURL(properties.getProperty("url"));
        for (Object key : properties.keySet()) {
            if (key.equals("url"))
                continue;
            postgresql.setProperty((String) key, properties.getProperty((String) key));
        }
        return postgresql;
    }

	@Bean
	@ConfigurationProperties("de.njsm.stocks.server.hikari")
	public DataSource hikari(@Qualifier("postgresql") DataSource postgresql) {
        var ds = new HikariDataSource();
        ds.setDataSource(postgresql);
        return ds;
	}

	@Bean
	@ConfigurationProperties("spring.liquibase")
	public SpringLiquibase liquibase(@Qualifier("hikari") DataSource dataSource) {
		SpringLiquibase result = new SpringLiquibase();
		result.setDataSource(dataSource);
		return result;
	}
}
