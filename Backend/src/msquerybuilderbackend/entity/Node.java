package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;

import ch.qos.logback.core.filter.Filter;



public class Node {
	
	private String type;
	
	private Set<ReturnAttribute> returnAttributes  = new HashSet<ReturnAttribute>(0);
	

	private Set<FilterAttribute> filterAttributes  = new HashSet<FilterAttribute>(0);
	
	
	private Set<OrderByAttribute> orderByAttributes  = new HashSet<OrderByAttribute>(0);
	
	
	

	private Set<Relationship> relationship= new HashSet<Relationship>();
	
	
	public Node(){
		
	}

	
	public String getType(){
		if (this.type==null) return "";
		return this.type;
	}
	
	public Set<ReturnAttribute> getReturnAttributes(){
		
		return this.returnAttributes;
	}
	public Set<FilterAttribute> getFilterAttributes(){
		return this.filterAttributes;
	}
	public Set<OrderByAttribute> getOrderByAttributes(){
		return this.orderByAttributes;
	}
	
	public Set<Relationship> getRelationship(){
		return this.relationship;
	}
	
	
	
	public void setReturnAttributes(Set<ReturnAttribute> r){
		this.returnAttributes=r;
	}
	
	public void setFilterAttributes(Set<FilterAttribute> f){
		this.filterAttributes=f;
	}
	
	public void setOrderByAttributes(Set<OrderByAttribute> o){
		this.orderByAttributes=o;
	}
	
	public void setRelationship(Set<Relationship> r){
		this.relationship=r;
	}
	
	public void addReturnAttribute(ReturnAttribute r){
		if (returnAttributes==null)
			returnAttributes = new HashSet<ReturnAttribute>(0);
		returnAttributes.add(r);
	}
	
	public void addFilterAttribute(FilterAttribute f){
		if (filterAttributes==null)
			filterAttributes = new HashSet<FilterAttribute>(0);
		filterAttributes.add(f);
	}
	
	public void addOrderByAttribute(OrderByAttribute o){
		if (orderByAttributes==null)
			orderByAttributes = new HashSet<OrderByAttribute>(0);
		orderByAttributes.add(o);
	}
	
	public void addRelationship(Relationship r){
		if (relationship==null)
			relationship = new HashSet<Relationship>(0);
		relationship.add(r);
	}
	
	
}
