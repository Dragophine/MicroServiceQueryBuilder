package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;


public class ReturnAttribute {

	private String attributeName ="";
	private String returnType ="";
	private String aggregation="";
	
	
	public ReturnAttribute(){
		
	}
	
	
	public String getAggregation() {
		return aggregation;
	}


	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
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
