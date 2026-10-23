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

import gr.uoa.di.madgik.authorization.domain.Permission;
import gr.uoa.di.madgik.authorization.repository.PermissionRepository;
import gr.uoa.di.madgik.authorization.service.Authorization;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.dao.DataIntegrityViolationException;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DataSourceAutoConfiguration.class,
                    HibernateJpaAutoConfiguration.class,
                    TransactionAutoConfiguration.class,
                    AuthorizationAutoConfiguration.class))
            .withPropertyValues(
                    "authorization.datasource.url=jdbc:h2:mem:authTest;DATABASE_TO_UPPER=false",
                    "authorization.datasource.driverClassName=org.h2.Driver",
                    "authorization.jpa.properties.hibernate.hbm2ddl.auto=create-drop");

    @Test
    void keepsHostDataSourceAndJpaBeans() {
        runner.withPropertyValues("spring.datasource.url=jdbc:h2:mem:hostDb")
                .run(context -> {
                    assertThat(context).hasNotFailed()
                            .hasBean("dataSource")
                            .hasBean("entityManagerFactory")
                            .hasBean("transactionManager")
                            .hasBean("authDataSource");
                    assertThat(context.getBean(DataSource.class)).isSameAs(context.getBean("dataSource"));
                });
    }

    @Test
    void storesAndQueriesPermissions() {
        runner.run(context -> {
            context.getBean(PermissionRepository.class).save(new Permission("user", "read", "resource"));

            Authorization authorization = context.getBean(Authorization.class);
            assertThat(authorization.canDo("user", "read", "resource")).isTrue();
            assertThat(authorization.canDo("user", "write", "resource")).isFalse();
        });
    }

    @Test
    void rejectsDuplicatePermission() {
        runner.run(context -> {
            PermissionRepository repository = context.getBean(PermissionRepository.class);
            repository.save(new Permission("user", "read", "resource", "group"));

            assertThatThrownBy(() -> repository.save(new Permission("user", "read", "resource", "group")))
                    .isInstanceOf(DataIntegrityViolationException.class);
            assertThat(repository.count()).isEqualTo(1);
        });
    }
}
