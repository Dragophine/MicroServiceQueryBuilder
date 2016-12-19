package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Alert {

	
	@GraphId private Long id;
	
	private String name;
	private String nodeName;
	private String attributeName;
	private String type;
	private String filterType;
	private Object value;
	private String email;
	

public void setName(String n){
	this.name=n;
}

public void setNodeName(String n){
	this.nodeName=n;
}

public void setAttributeName(String a){
	this.attributeName=a;
}

public void setType(String t){
	this.type=t;
}

public void setFilterType(String f){
	this.filterType=f;
}

public void setValue(Object v){
	this.value=v;
}

public void setEmail(String e){
	this.email=e;
}

public String getName(){
	return this.name;
}

public String getNodeName(){
	return this.nodeName;
}

public String getAttributeName(){
	return this.attributeName;
}

public String getType(){
	return this.type;
}

public String getFilterType(){
	return this.filterType;
}

public Object getValue(){
	return this.value;
}

public String getEmail(){
	return this.email;
}

public Long getId(){
	return this.id;
}
}
