package msquerybuilderbackend.entity;

public class Filters implements Comparable<Filters>{
	private long id;
	
	private String filterType="";
//	private String key;
	private String type="";
	private String value="";
	private Boolean changeable;
	private Boolean isBracketOpen;
	private Boolean isBracketClosed;
	private String logic="";
	
	public Filters(){
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFilterType() {
		return filterType;
	}
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getChangeable() {
		return changeable;
	}
	public void setChangeable(Boolean changeable) {
		this.changeable = changeable;
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

	@Override
	public int compareTo(Filters f1) {
		// TODO Auto-generated method stub
		return Long.compare(this.id, f1.id);
	}
}
