package application.entity;

public class FilterAttribute {

	private String attributeName;
	private String filterType;
	private String key;
	
	
	public FilterAttribute(){
		
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getFilterType(){
		return this.filterType;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setFilterType(String ft){
		this.filterType=ft;
	}
	
	public void setKey(String k){
		this.key=k;
	}
}
