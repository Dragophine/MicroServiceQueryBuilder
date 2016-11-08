package application.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Parameter {
	
@GraphId private Long id;
private String key;
private Object value;
private String type;

public Parameter(){

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

public void setKey(String k){
	this.key=k;
}

public void setValue(Object v){
	this.value=v;
}

public void setType(String t){
	this.type=t;
}


}
