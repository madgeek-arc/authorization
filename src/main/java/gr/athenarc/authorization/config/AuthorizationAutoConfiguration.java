package gr.athenarc.authorization.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@Import(AuthorizationConfig.class)
@EnableConfigurationProperties(AuthDatasourceProperties.class)
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager",
        basePackages = {"gr.athenarc.authorization.repository"})
public class AuthorizationAutoConfiguration {

    private final AuthDatasourceProperties authDataSourceProperties;

    public AuthorizationAutoConfiguration(AuthDatasourceProperties authDataSourceProperties) {
        this.authDataSourceProperties = authDataSourceProperties;
    }

    @Bean(name = "authDataSource")
    @ConditionalOnMissingBean
    public DataSource authDataSource() {
        return authDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "authEntityManagerFactory")
    @ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory authEntityManagerFactory) {
        return new JpaTransactionManager(authEntityManagerFactory);
    }

}
