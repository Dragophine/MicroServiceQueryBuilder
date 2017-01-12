package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;
 


public class QueryBuilder {



@GraphId private Long id;
private String name;
private String description;

//later to change to type Category
private String category;
private String limitCount;
//private Set<Parameter> parameter = new HashSet<Parameter>(0);
private Node node;
@Relationship(type = "HAS_EXPERTQUERY", direction = Relationship.OUTGOING)
private Set<ExpertQuery> expertquery = new HashSet<ExpertQuery>(0);
 

public QueryBuilder(){
	
}

public Node getNode(){
	return this.node;
}

public Long getId(){
	return this.id;
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

public Set<ExpertQuery>getExpertQuery(){
	return this.expertquery;
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
public void setExpertQuery(Set<ExpertQuery> e){
	this.expertquery=e;
}

//public void addParameter(Parameter p){
//	if (parameter==null)
//		parameter = new HashSet<Parameter>(0);
//	parameter.add(p);
//}
public void addExpertQuery(ExpertQuery e){
	if (expertquery==null)
		expertquery = new HashSet<ExpertQuery>(0);
	expertquery.add(e);
}
}
