package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Relationship {
	@GraphId private Long id;
	private String relationshipType="";
	private String direction ="";
	private String optional="";
	private Set<ReturnAttribute> returnAttributes = new HashSet<ReturnAttribute>(0);
	private Set<FilterAttribute> filterAttributes = new HashSet<FilterAttribute>(0);
	private Set<OrderByAttribute> orderByAttributes = new HashSet<OrderByAttribute>(0);
	private Node node;
	
	public String getRelationshipType(){

		return this.relationshipType;
	}
	
	public Long getId(){
		return this.id;
	}
	
	public String getDirection(){
		return this.direction;
	}
	
	public String getOptional(){
		return this.optional;
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
	
	public Node getNode(){
		return this.node;
	}
	
	public void setRelationshipType(String r){
		this.relationshipType=r;
	}
	
	public void setDirection(String d){
		this.direction=d;
	}
	
	public void setOptional(String o){
		this.optional=o;
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
	
	public void setNode(Node n){
		this.node=n;
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
	

	
}
