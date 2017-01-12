package msquerybuilderbackend.entity;




public class FilterAttribute {
	
	private String attributeName="";
	private String filterType="";
//	private String key;
	private String type="";
	private String value="";
	private Boolean changeable;
	private Boolean isBracketOpen;
	private Boolean isBracketClosed;
	private String logic="";
	
	public FilterAttribute(){
		
	}

	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getFilterType(){
		return this.filterType;
	}
	
//	public String getKey(){
//		return this.key;
//	}
	
	public String getType(){
		return this.type;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public Boolean getChangeable(){
		return this.changeable;
	}
	
	
	public Boolean getIsBracketOpen() {
		return isBracketOpen;
	}

	public void setIsBracketOpen(Boolean isBracketOpen) {
		this.isBracketOpen = isBracketOpen;
	}

	public Boolean getIsBracketClosed() {
		return isBracketClosed;
	}

	public void setIsBracketClosed(Boolean isBracketClosed) {
		this.isBracketClosed = isBracketClosed;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public void setAttributeName(String at){
		this.attributeName=at;
	}
	
	public void setFilterType(String ft){
		this.filterType=ft;
	}
	
//	public void setKey(String k){
//		this.key=k;
//	}
	public void setType(String t){
		this.type=t;
	}
	
	public void setValue(String val){
		this.value=val;
	}
	
	public void setChangeable(Boolean c){
		this.changeable=c;
	}
}
