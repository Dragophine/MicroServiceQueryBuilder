package msquerybuilderbackend.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * entity class of FilterAttribute with getter and setter
 * FilterAttribute is an entity used in the entity node in the QueryBuilder object
 * the entity is relevant for parsing the QueryBuilderObject and interpreting it
 * @author drago
 *
 */
public class FilterAttribute implements Comparable<FilterAttribute>{
	
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

	@Override
	public int compareTo(FilterAttribute f1) {
		// TODO Auto-generated method stub
		return this.getAttributeName().compareTo(f1.getAttributeName());
	}

}
