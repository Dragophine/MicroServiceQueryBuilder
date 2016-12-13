package application.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilderStringObject {
	private String queryString;
	private Map<String,Object> paramsMap= new HashMap<String,Object>();
	private char synonym;
	private Map<String,String> synonyms= new HashMap<String,String>();
	
	
	public QueryBuilderStringObject(){
		
	}
	
	public String getQueryString(){
		return this.queryString;
	}
	
	public Map<String,Object> getParamsMap(){
		return this.paramsMap;
	}
	public char getAlphabet(){
		return this.synonym;
	}
	
	public Map<String,String> getSynonyms(){
		return this.synonyms;
	}
	
	public void setQueryString(String q){
		this.queryString=q;
	}
	
	public void setParamsMap(HashMap<String,Object> p){
		this.paramsMap=p;
	}
	
	public void addParamsMap(String s, Object o){
		this.paramsMap.put(s, o);
	}
	
	public void setAlphabet(char a){
		this.synonym=a;
	}
	
	public void addSynonyms(String s, String o){
		this.synonyms.put(s, o);
	}
	
	public void addSynonyms(HashMap<String,String> m){
		this.synonyms=m;
	}
}
