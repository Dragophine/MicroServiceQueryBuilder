package msquerybuilderbackend.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import msquerybuilderbackend.entity.User;
import msquerybuilderbackend.repository.UserRepository;
import msquerybuilderbackend.repository.UserAuthorityRepository;

@Service
@Transactional
public class UserBusinessImpl implements UserBusiness {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserAuthorityRepository userAuthorityRepository;

	@Override
	public void changePassword(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
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
	

	@Override
	public void deleteUser(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUser(UserDetails arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional(readOnly = true)
	public boolean userExists(String username) {
		return getUserByEmail(username) != null;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		User user = getUserByEmail(username);

		if(user == null) {
			throw new UsernameNotFoundException("No user with username '" + username + "' exists.");
		}
		
		return user;
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Iterable<User> getAllUser() {
		return userRepository.findAll();
	}

	@Override
	public User getUserByid(Long userid) {
		return userRepository.findOne(userid);
	}


}
