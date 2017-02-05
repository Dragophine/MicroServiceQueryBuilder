package msquerybuilderbackend.entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;


/**
 * entity class for Alerts with getter and setter
 * Alerts can be created to check specific values in the neo4j database and to alert the user when reaching the defined limits
 * @author drago
 *
 */
@NodeEntity
public class Alert {

	
	@GraphId private Long id;
	
	private String name;
	private String type;
	private String filterType;
	private Object value;
	private String email;
	private String query;
	private List<Date> dates = new ArrayList<Date>(100);
	

	public void setName(String n){
		this.name=n;
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
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String q) {
		query = q;
	}
	
	public List<Date> getDates() {
		return dates;
	}
	
	public void setDates(List<Date> dates) {
		this.dates = dates;
	}
	
	public void addDate(Date date)
	{
		this.dates.add(date);
	}
}
