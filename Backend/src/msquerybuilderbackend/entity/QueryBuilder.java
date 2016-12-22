package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;



public class QueryBuilder {



private String name;
private String description;

//later to change to type Category
private String category;
private String limitCount;
//private Set<Parameter> parameter = new HashSet<Parameter>(0);
private Node node;


public QueryBuilder(){
	
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

//public void addParameter(Parameter p){
//	if (parameter==null)
//		parameter = new HashSet<Parameter>(0);
//	parameter.add(p);
//}

}
