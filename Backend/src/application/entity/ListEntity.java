package application.entity;
	
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ListEntity {
	
	@GraphId private Long id;
	private String label;

	public ListEntity(){
		
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return this.label;
	}

}
