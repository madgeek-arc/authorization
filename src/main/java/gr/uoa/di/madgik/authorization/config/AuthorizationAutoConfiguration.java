/*
 * Copyright 2021-2026 OpenAIRE AMKE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.uoa.di.madgik.authorization.config;

import com.zaxxer.hikari.HikariDataSource;
import gr.uoa.di.madgik.authorization.repository.PermissionRepository;
import gr.uoa.di.madgik.authorization.service.Authorization;
import gr.uoa.di.madgik.authorization.service.AuthorizationService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Registers the permission store on its own data source, entity manager factory and transaction manager.
 * These beans are not default candidates, so they don't satisfy by-type injection or
 * {@code @ConditionalOnMissingBean} checks and leave the host's own data source and JPA setup in place.
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableJpaRepositories(
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager",
        basePackages = {"gr.uoa.di.madgik.authorization.repository"})
public class AuthorizationAutoConfiguration {

    @Bean(name = "authJpaProperties", defaultCandidate = false)
    @ConfigurationProperties("authorization.jpa")
    JpaProperties authJpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "authDataSourceProperties", defaultCandidate = false)
    @ConfigurationProperties("authorization.datasource")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "authDataSource", defaultCandidate = false)
    @ConditionalOnMissingBean(name = "authDataSource")
    @ConfigurationProperties("authorization.datasource.configuration")
    public HikariDataSource authDataSource(
            @Qualifier("authDataSourceProperties") DataSourceProperties authDataSourceProperties) {
        return authDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "authEntityManagerFactory", defaultCandidate = false)
    @ConditionalOnMissingBean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(@Qualifier("authDataSource")
                                                                           HikariDataSource authDataSource,
                                                                           @Qualifier("authJpaProperties")
                                                                           JpaProperties authJpaProperties) {
        LocalContainerEntityManagerFactoryBean authEntityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        authEntityManagerFactory.setDataSource(authDataSource);
        authEntityManagerFactory.setPersistenceUnitName("authPersistentUnit");
        authEntityManagerFactory.setPackagesToScan("gr.uoa.di.madgik.authorization.domain");
        authEntityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        authEntityManagerFactory.setJpaPropertyMap(authJpaProperties.getProperties());
        return authEntityManagerFactory;
    }

    @Bean(name = "authTransactionManager", defaultCandidate = false)
    @ConditionalOnMissingBean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory authEntityManagerFactory) {
        return new JpaTransactionManager(authEntityManagerFactory);
    }

    @ConditionalOnMissingBean(Authorization.class)
    @Bean
    public AuthorizationService authorization(PermissionRepository repository) {
        return new AuthorizationService(repository);
    }

}

