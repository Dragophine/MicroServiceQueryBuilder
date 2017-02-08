package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.security.core.GrantedAuthority;


/**
 * entity class of UserAuthority with getter and setter
 * UserAuthority is an entity with userspecific data saved in the neo4j database with an relationship to the nodetype User
 * the entity is relevant for authority management
 * @author Roman
 *
 */
@NodeEntity
public class UserAuthority extends Node implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	@GraphId
	private Long id;
	
	String authority;

	public UserAuthority(String authority) {
		super();
		this.authority=authority;
	}

	public UserAuthority() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserAuthority that = (UserAuthority) o;

		return authority != null ? authority.equals(that.authority) : that.authority == null;

	}

	@Override
	public int hashCode() {
		return authority != null ? authority.hashCode() : 0;
	}
}
