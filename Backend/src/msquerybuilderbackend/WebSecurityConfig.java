package msquerybuilderbackend;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import msquerybuilderbackend.business.UserBusiness;

@Configuration
@EnableWebSecurity


public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
	@Autowired
	private UserBusiness userBusiness;
	
	/**
	 * method which defines where the users can be found
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userBusiness).passwordEncoder(encoder());

	}
	
	/**
	 * method to get a PasswordEncoder
	 * @return a new PasswordEncoder
	 */
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * method to configure the security configuration
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
    	  	
		http.cors().and()
		.authorizeRequests()
		
		.regexMatchers("^/api.*", "/user", "/authentications/user").fullyAuthenticated();
	
		http.httpBasic();
		http.csrf().disable();
	}
    

    /**
     * method to create a WebMvcConfigurer
     * @return a WebMvcConfigurer
     */
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
}