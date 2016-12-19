package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Category {
@GraphId private Long id;
private String name;
private String description;


public Category(){
	
}

public void setName(String name){
	this.name=name;
}

public void setDescription(String d){
	this.description=d;
}

public String getName(){
	return this.name;
}

public String getDescription(){
	if (this.description==null) return "";
	return this.description;
}
}
