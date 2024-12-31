package gr.athenarc.authorization.config;

import com.zaxxer.hikari.HikariDataSource;
import gr.athenarc.authorization.repository.PermissionRepository;
import gr.athenarc.authorization.service.Authorization;
import gr.athenarc.authorization.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
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

    @ConditionalOnMissingBean(name = "authDataSource")
    @Bean(name = "authDataSource")
    public DataSource authDataSource() {
        return authDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "authEntityManagerFactory")
    @ConditionalOnMissingBean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(@Qualifier("authDataSource") DataSource authDataSource) {

        Map<String, String> authJpaProperties = new HashMap<>();
        authJpaProperties.put("hibernate.dialect", authDataSourceProperties.getHibernate().getDialect());
        authJpaProperties.put("hibernate.hbm2ddl.auto", authDataSourceProperties.getHibernate().getHbm2ddl());

        LocalContainerEntityManagerFactoryBean authEntityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        authEntityManagerFactory.setDataSource(authDataSource);
        authEntityManagerFactory.setPersistenceUnitName("authPersistentUnit");
        authEntityManagerFactory.setPackagesToScan("gr.athenarc.authorization.domain");
        authEntityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        authEntityManagerFactory.setJpaPropertyMap(authJpaProperties);
        return authEntityManagerFactory;
    }

    @Bean(name = "authTransactionManager")
    @ConditionalOnMissingBean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory authEntityManagerFactory) {
        return new JpaTransactionManager(authEntityManagerFactory);
    }

    @ConditionalOnClass(value = AuthorizationService.class)
    @Bean
    public Authorization authorization(PermissionRepository repository) {
        return new AuthorizationService(repository);
    }

}

