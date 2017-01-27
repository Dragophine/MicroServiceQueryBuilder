package msquerybuilderbackend.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;


/**
 * entity class of Parameter with getter and setter
 * Parameter is an entity used in the entity ExpertQuery (also saved in neo4j database) and the QueryBuilder object
 * the entity is relevant for saving the parameters to a queryString in the neo4j database 
 * but also to parse the QueryBuilder object and to interpret it 
 * 
 * @author drago
 *
 */
@NodeEntity
public class Parameter {
	
@GraphId private Long id;
private String key;
private Object value;
private String type;
private boolean changeable;

public Parameter(){

}

public boolean getChangeable(){
	return this.changeable;
}

public String getKey(){
	return this.key;
}

public Object getValue(){
	return this.value;
}

public String getType(){
	return this.type;
}

public Long getId(){
	return this.id;
}

public void setKey(String k){
	this.key=k;
}

public void setValue(Object v){
	this.value=v;
}

public void setType(String t){
	this.type=t;
}

public void setChangeable(boolean c){
	this.changeable=c;
}




}
