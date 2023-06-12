package gr.athenarc.authorization.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(AuthorizationConfig.class)
public class AuthorizationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationConfig sampleService(AuthDatasourceProperties properties) {
        return new AuthorizationConfig(properties);
    }

}
