package msquerybuilderbackend.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterAttribute {
	
	private String attributeName="";

	private Set<Filters> filters = new HashSet<Filters>(0);

	
	public FilterAttribute(){
		
	}

	public String getAttributeName(){
		return this.attributeName;
	}
	
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}


	public Set<Filters> getFilters() {
		if (filters==null) return new HashSet<Filters>();
		return filters;
	}

	public void setFilters(Set<Filters> filter) {
		this.filters = filter;
	}

	public void addFilters(Filters filter){
		if (filter==null) this.filters=new HashSet<Filters>();
		this.filters.add(filter);
	}

}
