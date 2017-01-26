package msquerybuilderbackend;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author drago
 * 
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "repository")
// @EnableScheduling
// @EnableAutoConfiguration
@EnableTransactionManagement
// @ComponentScan("at.jku.se.dedoc")
public class Neo4JConfig extends Neo4jConfiguration {

	/**
	 * Neo4JConfiguration via Spring which connects to the Neo4J database with the given logindata
	 * @return the configuration
	 */
	@Bean
	public org.neo4j.ogm.config.Configuration getConfiguration() {
		org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
		config.driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
				.setURI("http://neo4j:admin@localhost:7474");
		return config;
	}

/**
 * method to map the entities
 * @return the SessionFactory with the configuration for neo4j and the location of the entities
 */
	@Override
	@Bean
	public SessionFactory getSessionFactory() {
		// TODO Auto-generated method stu
		return new SessionFactory(getConfiguration(), "msquerybuilderbackend.entity");
	}
}
