package msquerybuilderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
@ComponentScan
@EnableNeo4jRepositories
public class MicroServiceQueryBuilderBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceQueryBuilderBackendApplication.class, args);
    }
}