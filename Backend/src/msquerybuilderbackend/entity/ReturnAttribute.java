package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ReturnAttribute {
	@GraphId private Long id;
	private String attributeName ="";
	private String returnType ="";
	
	
	public ReturnAttribute(){
		
	}
	
	public Long getId(){
		return this.id;
	}
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getReturnType(){
		return this.returnType;
	}
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setReturnType(String ret){
		this.returnType=ret;
	}
}
