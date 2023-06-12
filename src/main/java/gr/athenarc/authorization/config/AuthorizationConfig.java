package gr.athenarc.authorization.config;

import com.zaxxer.hikari.HikariDataSource;
import gr.athenarc.authorization.AuthorizationApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {
        "gr.athenarc.authorization",
},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AuthorizationApplication.class)
        })
@EntityScan(basePackages = "gr.athenarc.authorization.domain")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager",
        basePackages = {"gr.athenarc.authorization.repository"})
public class AuthorizationConfig {

    private final AuthDatasourceProperties authDataSourceProperties;

    public AuthorizationConfig(AuthDatasourceProperties authDataSourceProperties) {
        this.authDataSourceProperties = authDataSourceProperties;
    }

    @Bean(name = "authDataSource")
    public DataSource authDataSource() {
        return authDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder authEntityManagerFactoryBuilder, @Qualifier("authDataSource") DataSource authDataSource) {

        Map<String, String> authJpaProperties = new HashMap<>();
        authJpaProperties.put("hibernate.dialect", authDataSourceProperties.getHibernate().getDialect());
        authJpaProperties.put("hibernate.hbm2ddl.auto", authDataSourceProperties.getHibernate().getHbm2ddl());

        return authEntityManagerFactoryBuilder
                .dataSource(authDataSource)
                .packages("gr.athenarc.authorization.domain")
                .persistenceUnit("authDataSource")
                .properties(authJpaProperties)
                .build();
    }

    @Bean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory authEntityManagerFactory) {
        return new JpaTransactionManager(authEntityManagerFactory);
    }
}
