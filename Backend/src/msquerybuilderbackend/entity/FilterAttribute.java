package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class FilterAttribute {
	@GraphId private Long id;
	private String attributeName="";
	private String filterType="";
//	private String key;
	private String type="";
	private String value="";
	private Boolean changeable;
	
	public FilterAttribute(){
		
	}
	public Long getId(){
		return this.id;
	}
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getFilterType(){
		return this.filterType;
	}
	
//	public String getKey(){
//		return this.key;
//	}
	
	public String getType(){
		return this.type;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public Boolean getChangeable(){
		return this.changeable;
	}
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setFilterType(String ft){
		this.filterType=ft;
	}
	
//	public void setKey(String k){
//		this.key=k;
//	}
	public void setType(String t){
		this.type=t;
	}
	
	public void setValue(String val){
		this.value=val;
	}
	
	public void setChangeable(Boolean c){
		this.changeable=c;
	}
}
