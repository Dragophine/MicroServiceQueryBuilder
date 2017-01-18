package msquerybuilderbackend.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QueryBuilderStringObject {
	private String queryString;
	private Map<String,Object> paramsMap= new HashMap<String,Object>();
	private char synonym='a';
	private char end='z';
	private Map<String,String> synonyms= new HashMap<String,String>();
	
	private String query = "";
	private LinkedList<String> filterStatements = new LinkedList<String>();
	private LinkedList<String> actualFilterStatements = new LinkedList<String>();
	private LinkedList<String> orderStatements = new LinkedList<String>();
	private LinkedList<String> returnStatements = new LinkedList<String>();
	private LinkedList<String> cypherquery = new LinkedList<String>();
	
	public QueryBuilderStringObject(){
		
	}
	
	public void addFilterStatement(String s){
		if (this.filterStatements==null) new LinkedList<String>();
		this.filterStatements.add(s);
	}
	
	public void addActualFilterStatement(String s){
		if (this.actualFilterStatements==null) new LinkedList<String>();
		this.actualFilterStatements.add(s);
	}
	
	public void addOrderStatement(String s){
		if (this.orderStatements==null) new LinkedList<String>();
		this.orderStatements.add(s);
	}
	
	public void addReturnStatement(String s){
		if (this.returnStatements==null) new LinkedList<String>();
		this.returnStatements.add(s);
	}
	
	public void addCypherQuery(String s){
		if (this.cypherquery==null) new LinkedList<String>();
		this.cypherquery.add(s);
	}
	
	
	public char getSynonym() {
		return synonym;
	}



	public void setSynonym(char synonym) {
		this.synonym = synonym;
	}



	public char getEnd() {
		return end;
	}



	public void setEnd(char end) {
		this.end = end;
	}



	public String getQuery() {
		return query;
	}



	public void setQuery(String query) {
		this.query = query;
	}



	public LinkedList<String> getFilterStatements() {
		return filterStatements;
	}



	public void setFilterStatements(LinkedList<String> filterStatements) {
		this.filterStatements = filterStatements;
	}



	public LinkedList<String> getActualFilterStatements() {
		return actualFilterStatements;
	}



	public void setActualFilterStatements(LinkedList<String> actualFilterStatements) {
		this.actualFilterStatements = actualFilterStatements;
	}



	public LinkedList<String> getOrderStatements() {
		return orderStatements;
	}



	public void setOrderStatements(LinkedList<String> orderStatements) {
		this.orderStatements = orderStatements;
	}



	public LinkedList<String> getReturnStatements() {
		return returnStatements;
	}



	public void setReturnStatements(LinkedList<String> returnStatements) {
		this.returnStatements = returnStatements;
	}



	public LinkedList<String> getCypherquery() {
		return cypherquery;
	}



	public void setCypherquery(LinkedList<String> cypherquery) {
		this.cypherquery = cypherquery;
	}



	public void setParamsMap(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}



	public void setSynonyms(Map<String, String> synonyms) {
		this.synonyms = synonyms;
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
	
	
}
