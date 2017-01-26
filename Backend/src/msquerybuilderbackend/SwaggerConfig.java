package msquerybuilderbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author drago
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	/**
	 * creates Docket for swagger documentation
	 * @return
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("msquerybuilderbackend.rest"))
				//.paths(PathSelectors.ant("/api/*"))
				.build()
				.apiInfo(apiInfo());
	}

	/**
	 * creates ApiInfo
	 * @return the ApiInfo
	 */
	private ApiInfo apiInfo() {
		ApiInfo defaultinfo = ApiInfo.DEFAULT;
		ApiInfo apiInfo = new ApiInfo("MicroServiceQueryBuilderBackend", "Service for Querying Neo4j", defaultinfo.getVersion(), defaultinfo.getTermsOfServiceUrl(), new Contact("", "", ""), defaultinfo.getLicense(), defaultinfo.getLicenseUrl());
		return apiInfo;
	}

}
