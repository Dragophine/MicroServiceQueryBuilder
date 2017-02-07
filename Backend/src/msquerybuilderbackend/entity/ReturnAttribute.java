package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * entity class of ReturnAttribute with getter and setter
 * ReturnAttribute is an entity used in the entity node in the QueryBuilder object
 * the entity is relevant for parsing the QueryBuilderObject and interpreting it
 * @author drago
 *
 */
public class ReturnAttribute {

	private String attributeName ="";
	private String aggregation="";
	private String alias="";
	
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
	
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}


	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
}
