package msquerybuilderbackend.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * entity class for ExpertQuery objects with getter and setter
 * these objects are also saved in the neo4j database (instead of ExpertQueryJsonObject)
 * An ExpertQuery consists of the querystring and the parameters for the queryString and is created in the expert mode of the application
 * @author drago
 *
 */
@NodeEntity
public class ExpertQuery{

	@GraphId private Long id;

	private String name;
private String query;
@Relationship(type = "HAS_PARAMETER", direction = Relationship.OUTGOING)
private Set<Parameter> parameter = new HashSet<Parameter>(0);



private String description;

@Relationship(type = "HAS_CATEGORY", direction = Relationship.OUTGOING)
private Category category;


public ExpertQuery(){
	
}

public Long getId(){
	return this.id;
}

public String getQuery(){
	return this.query;
}

public Set<Parameter> getParameter(){
	if(this.parameter==null) this.parameter= new HashSet<Parameter>();
	return this.parameter;
}

public String getName(){
	return this.name;
}

public String getDescription(){
	return this.description;
}

public Category getCategory(){
	return this.category;
}

public void setQuery(String q){
	this.query=q;
}

public void setParameter(Set<Parameter> p){
	this.parameter=p;
}

public void setName(String n){
	this.name=n;
}

public void setDescription(String d){
	this.description=d;
}

public void setCategory(Category c){
	this.category=c;
}

public void addParameter(Parameter p){
	if (parameter==null)
		parameter = new HashSet<Parameter>(0);
	parameter.add(p);
}


}
