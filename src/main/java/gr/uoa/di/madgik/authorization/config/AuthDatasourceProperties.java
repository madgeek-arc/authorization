/**
 * Copyright 2021-2025 OpenAIRE AMKE
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


import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "authorization.datasource")
public class AuthDatasourceProperties extends DataSourceProperties {

    /**
     * Hibernate specific properties.
     */
    private HibernateProperties hibernate;


    public HibernateProperties getHibernate() {
        return hibernate;
    }

    public void setHibernate(HibernateProperties hibernate) {
        this.hibernate = hibernate;
    }

    public static class HibernateProperties {

        /**
         * Database Dialect. In most cases, Hibernate will be able to determine the proper Dialect
         * to use by asking some questions of the JDBC Connection during bootstrap.
         */
        String dialect;

        /**
         * Automatic schema generation.
         * Available values: (none | create-only | drop | create | create-drop | validate | update)
         */
        String hbm2ddl;

        public String getDialect() {
            return dialect;
        }

        public void setDialect(String dialect) {
            this.dialect = dialect;
        }

        public String getHbm2ddl() {
            return hbm2ddl;
        }

        public void setHbm2ddl(String hbm2ddl) {
            this.hbm2ddl = hbm2ddl;
        }
    }
}
