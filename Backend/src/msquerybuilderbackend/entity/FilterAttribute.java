package msquerybuilderbackend.entity;

import java.util.ArrayList;
import java.util.List;

public class FilterAttribute {
	
	private String attributeName="";
	private List<Filter> filters;
	
	public FilterAttribute(){
		
	}

	public String getAttributeName(){
		return this.attributeName;
	}
	
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}

	public List<Filter> getFilter() {
		if (filters==null) return new ArrayList<Filter>();
		return filters;
	}

	public void setFilter(List<Filter> filter) {
		this.filters = filter;
	}
	
	public void addFilter(Filter filter){
		if (filter==null) this.filters=new ArrayList<Filter>();
		this.filters.add(filter);
	}
	
	
}
