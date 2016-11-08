package application.entity;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ExpertQuery {

	@GraphId private Long id;

	private String name;
private String query;
private List<Parameter> parameter;



private String description;

//later to change to type Category
private String category;


public ExpertQuery(){
	
}

public String getQuery(){
	return this.query;
}

public List<Parameter> getParameter(){
	return this.parameter;
}

public String getName(){
	return this.name;
}

public String getDescription(){
	return this.description;
}

public String getCategory(){
	return this.category;
}

public void setQuery(String q){
	this.query=q;
}

public void setParameter(List<Parameter> p){
	this.parameter=p;
}

public void setName(String n){
	this.name=n;
}

public void setDescription(String d){
	this.description=d;
}

public void setCategory(String c){
	this.category=c;
}

public void hasParameter(Parameter p){
	if (parameter==null)
		parameter = new ArrayList<Parameter>();
	parameter.add(p);
}


}
