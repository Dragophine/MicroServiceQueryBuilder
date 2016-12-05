package application.entity;

public class ReturnAttribute {

	private String attributeName ="";
	private String returnType ="";
	
	public ReturnAttribute(){
		
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getReturnType(){
		return this.returnType;
	}
	
	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setReturnType(String ret){
		this.returnType=ret;
	}
}
