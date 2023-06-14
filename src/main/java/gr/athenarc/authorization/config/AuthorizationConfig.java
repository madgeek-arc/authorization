package gr.athenarc.authorization.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "gr.athenarc.authorization")
@EntityScan(basePackages = "gr.athenarc.authorization.domain")
public class AuthorizationConfig {


}
