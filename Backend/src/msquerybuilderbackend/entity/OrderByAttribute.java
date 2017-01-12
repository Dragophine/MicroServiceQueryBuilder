package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class OrderByAttribute {
	@GraphId private Long id;
	private String attributeName="";
	private String direction="";
	
	
	public OrderByAttribute(){
		
	}
	public Long getId(){
		return this.id;
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getDirection(){
		return this.direction;
	}
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setDirection(String d){
		this.direction=d;
	}
}
