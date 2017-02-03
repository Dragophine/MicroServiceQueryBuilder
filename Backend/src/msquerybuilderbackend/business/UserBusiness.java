package msquerybuilderbackend.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import msquerybuilderbackend.entity.User;
import msquerybuilderbackend.repository.UserRepository;
import msquerybuilderbackend.repository.UserAuthorityRepository;

/**
 * Class for all activities with neo4j database regarding the entity User and its authorities
 * @author 
 *
 */
@Component
@Service
@Transactional
public class UserBusiness implements UserDetailsManager {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserAuthorityRepository userAuthorityRepository;


	public void changePassword(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * method which creates a new user in the neo4j database and adds the authorities as relationships
	 * 
	 * @param user is the new user to create
	 */
	public void createUser(UserDetails user) {
		Iterable<Long> existingUsers = userRepository.checkForExistingUser(user.getUsername());
		Assert.isTrue(!existingUsers.iterator().hasNext());
		User user1 = (User) user;
	
		User newUser = new User();
		newUser.setEmail(user.getUsername());
		newUser.setPassword(user.getPassword());
		System.out.println(user1.getFirstName());
		newUser.setFirstName(user1.getFirstName());
		newUser.setLastName(user1.getLastName());
		
		// hash password
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));
		
		user.getAuthorities().forEach(authority -> {
			newUser.addAuthority(userAuthorityRepository.mergeAuthority(authority.getAuthority()));
		});
		
		userRepository.save(newUser);
	}
	

	
	public void deleteUser(String arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void updateUser(UserDetails arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * method which returns whether a user with given username exists or not
	 * @param username is the username to look for
	 * @return true/false if the user exists or not
	 */
	@Transactional(readOnly = true)
	public boolean userExists(String username) {
		return getUserByEmail(username) != null;
	}

	/**
	 * method which returns UserDetails of a certain user specified by username
	 * @param username is the specific name of the user to return
	 * @return UserDetails of this specific user
	 */
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		User user = getUserByEmail(username);

		if(user == null) {
			throw new UsernameNotFoundException("No user with username '" + username + "' exists.");
		}
		
		return user;
	}

	/**
	 * method which returns the user by given email
	 * @param email is the specific email of the certain user
	 * @return the User object
	 */
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * method which returns all users from the neo4j database
	 * @return all found users
	 */
	public Iterable<User> getAllUser() {
		return userRepository.findAll();
	}

	/**
	 * method which returns a certain user with specific neo4j Id
	 * @param userid is the neo4j ID of the user to look for
	 * @return the User object
	 */
	public User getUserByid(Long userid) {
		return userRepository.findOne(userid);
	}


}
