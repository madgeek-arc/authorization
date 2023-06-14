package gr.athenarc.authorization.config;


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
