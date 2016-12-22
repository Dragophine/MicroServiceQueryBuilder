package msquerybuilderbackend.entity;
	
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ListEntity {
	
	@GraphId private Long id;
	private String label;
	private String direction;
	private String relationship;

	public ListEntity(){
		
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public void setDirection(String d){
		this.direction =d;
	}
	
	public void setRelationship(String r){
		this.relationship=r;
	}
	
	public String getLabel(){
		return this.label;
	}
	
	public String getDirection(){
		if (this.direction==null) return "";
		return this.direction;
	}
	
	public String getRelationship(){
		if (this.relationship==null) return "";
		return this.relationship;
	}

}
