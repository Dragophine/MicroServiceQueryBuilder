package msquerybuilderbackend.entity;


public class OrderByAttribute {

	private String attributeName="";
	private String direction="";
	private int id;
	



	public OrderByAttribute(){
		
	}

	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getDirection(){
		return this.direction;
	}
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setDirection(String d){
		this.direction=d;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
