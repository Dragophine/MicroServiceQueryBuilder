package msquerybuilderbackend.business;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;

import msquerybuilderbackend.entity.User;


public interface UserBusiness extends UserDetailsManager {
	User getUserByEmail(String email);

	Iterable<User> getAllUser();

	User getUserByid(Long userid);

}
