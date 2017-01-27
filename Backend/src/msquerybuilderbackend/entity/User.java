package msquerybuilderbackend.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;


/**
 * entity class of User with getter and setter
 * User is an entity with userspecific data saved in the neo4j database
 * the entity is relevant for user and authority management
 *
 */
@NodeEntity
public class User implements UserDetails {
	
	@GraphId
	private Long id;
	
	private static final long serialVersionUID = -8976733628047697060L;
	
	private String email;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private String firstName;
	private String lastName;

	
	@JsonIgnore
	@Relationship(type = "HAS_AUTHORITY", direction = Relationship.OUTGOING)
	private Set<UserAuthority> authorities = new HashSet<>(0);

	public User() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getUsername() {
		return email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public void addAuthority(UserAuthority userrole) {
		Assert.notNull(userrole);
		authorities.add(userrole);
	}
	
	public void removeAuthority(UserAuthority authority) {
		Assert.notNull(authority);
		authorities.remove(authority);
	}
	
	public void setAuthorities(Set<UserAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	

	
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@JsonIgnore
	@Override
	public boolean isEnabled() {
		//return dateActivated>0;
		return true;
	}


}
