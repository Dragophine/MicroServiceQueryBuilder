package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;



public class ExpertQueryJsonObject {
	private Long id;
	private String name;
	private String query;
	private Set<Parameter> parameter = new HashSet<Parameter>(0);
	private String description;
	private String category;


public ExpertQueryJsonObject(){
	
}

public Long getId(){
	return this.id;
}

public void setId(Long id) {
	this.id = id;
}

public String getQuery(){
	return this.query;
}

public Set<Parameter> getParameter(){
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

public void setParameter(Set<Parameter> p){
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

public void addParameter(Parameter p){
	if (parameter==null)
		parameter = new HashSet<Parameter>(0);
	parameter.add(p);
}


}
