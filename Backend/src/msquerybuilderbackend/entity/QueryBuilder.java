package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;
 

/**
 * entity class of QueryBuilder (for the frontend) with getter and setter
 * QueryBuilder is an entity used only in the frontend and consists of metadata (id, name, description..) and a Node
 * the entity is relevant for parsing the QueryBuilder object and interpreting it 
 * @author drago
 *
 */
public class QueryBuilder {


private Long id;
private String name;
private String description;

//later to change to type Category
private String category;
private String limitCount;
private String skip;
private boolean distinct;
private String author;


//private Set<Parameter> parameter = new HashSet<Parameter>(0);
private Node node;
 
 

public QueryBuilder(){
	
}

public void setId(Long id){
	this.id=id;
}

public Long getId(){
	return this.id;
}
public Node getNode(){
	return this.node;
}


public String getLimitCount(){
	return this.limitCount;
}

//public Set<Parameter> getParameter(){
//	return this.parameter;
//}

public String getName(){
	return this.name;
}



public String getDescription(){
	return this.description;
}

public String getCategory(){
	return this.category;
}

public void setNode(Node n){
	this.node=n;
}

public void setLimitcount(String l){
	this.limitCount=l;
}

//public void setParameter(Set<Parameter> p){
//	this.parameter=p;
//}

public void setName(String n){
	this.name=n;
}

public void setDescription(String d){
	this.description=d;
}

public void setCategory(String c){
	this.category=c;
}

public boolean getDistinct() {
	return distinct;
}

public void setDistinct(boolean distinct) {
	this.distinct = distinct;
}

public String getSkip() {
	return skip;
}

public void setSkip(String skip) {
	this.skip = skip;
}

public String getAuthor() {
	return author;
}

public void setAuthor(String author) {
	this.author = author;
}

public void setLimitCount(String limitCount) {
	this.limitCount = limitCount;
}

//public void addParameter(Parameter p){
//	if (parameter==null)
//		parameter = new HashSet<Parameter>(0);
//	parameter.add(p);
//}

}
