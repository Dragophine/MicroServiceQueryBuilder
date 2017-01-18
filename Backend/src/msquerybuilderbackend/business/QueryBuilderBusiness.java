package msquerybuilderbackend.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.FilterAttribute;
import msquerybuilderbackend.entity.Filters;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.OrderByAttribute;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.entity.Relationship;
import msquerybuilderbackend.entity.ReturnAttribute;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;
import msquerybuilderbackend.repository.QueryBuilderJsonStringRepository;

@Component
public class QueryBuilderBusiness {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	@Autowired
	ParameterRepository parameterRepository;
	@Autowired
	QueryBuilderJsonStringRepository queryBuilderJsonStringObjectRepository;
	 @Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	//required global variables
	Map<String,String> synonyms = new HashMap<String,String>();
//	Map<String,Object> paramsMap = new HashMap<String,Object>();
	char synonym='a';
	char end='z';
//	Set<Parameter> parameter = new HashSet<Parameter>(0);
	String query = "";
	boolean distinct = false;
	LinkedList<String> filterStatements = new LinkedList<String>();
	LinkedList<String> actualFilterStatements = new LinkedList<String>();
	LinkedList<String> orderStatements = new LinkedList<String>();
	LinkedList<String> returnStatements = new LinkedList<String>();
	LinkedList<String> cypherquery = new LinkedList<String>();

	public Result executeQueryBuilderQuery(QueryBuilder queryBuilder) throws Exception{
	//	Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Result result = null;
		ExpertQuery expertQuery = buildQueryString(queryBuilder);
		for (Parameter p:expertQuery.getParameter()){
			paramsMap.put(p.getKey(), p.getValue());
		}
		result = neo4jOperations.query(expertQuery.getQuery(), paramsMap, true);	
		return result;
	}
	
	public Long createQueryBuilder(QueryBuilder queryBuilder) throws JsonProcessingException, Exception{

		QueryBuilderJsonStringObject alreadyUsedName= queryBuilderJsonStringObjectRepository.findByName(queryBuilder.getName());
		if (alreadyUsedName != null){
			
			return 0L;	
		}else{
			Category category = categoryRepository.findByName(queryBuilder.getCategory());
			
/**
* Interpretation des Querybuilders wie bei execute ausständig
*/			
			//Hoffe ich hab das so richtig verstanden...
			//ExpertQuery erstellen, QueryString und Parameter übergeben,
			//an QueryBuilderJsonStringObject anhängen und das ganze speichern
			//???
			
			ExpertQuery expertQuery = buildQueryString(queryBuilder);
			

			expertQuery.setName(queryBuilder.getName());
			expertQuery.setDescription(queryBuilder.getDescription());
			expertQuery.setCategory(category);
//			expertQuery.setQuery(s);
//			for (Parameter p: parameter){
//				expertQuery.addParameter(p);
//			}
			
			/**
			 * ExpertQuery auch den Namen und Beschreibung geben
			 */
			QueryBuilderJsonStringObject qbjso = new QueryBuilderJsonStringObject();
			qbjso.setName(queryBuilder.getName());
			qbjso.setDescription(queryBuilder.getDescription());
			qbjso.setCategory(category);
			ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String queryBuilderJsonString = mapper.writeValueAsString(queryBuilder);
			System.out.println(queryBuilderJsonString);
			qbjso.setQueryBuilderJson(queryBuilderJsonString);
			
			qbjso.setExpertQuery(expertQuery);
	    	queryBuilderJsonStringObjectRepository.save(qbjso);
	    	QueryBuilderJsonStringObject returnNew=queryBuilderJsonStringObjectRepository.findByName(queryBuilder.getName());
	
	    	return returnNew.getId();
		}
	}

	
	public ExpertQuery generateQueryBuilderQueryString(QueryBuilder queryBuilder) throws Exception{
		return buildQueryString(queryBuilder);
	}
	
	
	public void deleteQueryBuilder(String queryId){
		QueryBuilderJsonStringObject qbjso=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(queryId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 qbjso= queryBuilderJsonStringObjectRepository.findOne(id);
		} else{
			 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
		}
//		
//		
//		Set<ExpertQuery> expertqueries = qbjso.getExpertQuery();
//		Iterator iter = expertqueries.iterator();
//
//		while (iter.hasNext()){
//			ExpertQuery eq= (ExpertQuery) iter.next();
		if (qbjso.getExpertQuery()!=null){
			for (Parameter p : qbjso.getExpertQuery().getParameter())
	    	{
		    	parameterRepository.delete(p.getId());
	    	}
			expertQueryRepository.delete(qbjso.getExpertQuery().getId());
		}
		

    	
    	
    	queryBuilderJsonStringObjectRepository.delete(qbjso.getId());

	}
	
	public QueryBuilderJsonStringObject updateQueryBuilder(String queryId, QueryBuilder updatedQuery) throws JsonProcessingException, Exception{
		QueryBuilderJsonStringObject qbjso=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(queryId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 qbjso= queryBuilderJsonStringObjectRepository.findOne(Long.parseLong(queryId));
		} else{
			 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
		}
		

		QueryBuilderJsonStringObject alreadyUsedName=queryBuilderJsonStringObjectRepository.findByName(updatedQuery.getName());
		if ((updatedQuery.getName().equals(qbjso.getName()))||(alreadyUsedName==null)){
		
			
			 
			if (qbjso.getExpertQuery()!=null){
				expertQueryRepository.delete(qbjso.getExpertQuery());
			}
			/**
			 * Interpretation der Query in ExpertQuery ausständig wie bei execute
			 */
			
	    	
	    	/**
	    	 * eventuell nicht notwendig, falls es durch die Beschreibung des RElationships in der Entity funktioniert
	    	 */
	//    	for (ExpertQuery q : updatedQuery.getExpertQuery())
	//    	{			    	
	//	    	expertQueryRepository.save(q);
	//    	}
	    	
	//    	Set<ExpertQuery> updatedQuerySet = new HashSet<ExpertQuery>();
	//    	updatedQuerySet.add(expertQuery);
			
			Category category = categoryRepository.findByName(updatedQuery.getCategory());
	    	qbjso.setDescription(updatedQuery.getDescription());
	    	qbjso.setName(updatedQuery.getName());
	    	qbjso.setCategory(category);
	    	ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String queryBuilderJsonString = mapper.writeValueAsString(updatedQuery);
			qbjso.setQueryBuilderJson(queryBuilderJsonString);
	    	
	    	/**
	    	 * zusammengebaute ExpertQuery
	    	 */
	//    	newExpertQuery.setName(updatedQuery.getName());
	//    	newExpertQuery.setDescription(updatedQuery.getDescription());
	//    	newExpertQuery.setCategory(category);
	//    	qbjso.setExpertQuery(newExpertQuery);
	
			ExpertQuery expertQuery = buildQueryString(updatedQuery);
			
			
			expertQuery.setName(updatedQuery.getName());
			expertQuery.setDescription(updatedQuery.getDescription());
			expertQuery.setCategory(category);
//			expertQuery.setQuery(s);
//			for (Parameter p: parameter){
//				expertQuery.addParameter(p);
//			}
			qbjso.setExpertQuery(expertQuery);
	    	
	    	queryBuilderJsonStringObjectRepository.save(qbjso);
	    	return qbjso;
		}else{
			return null;
		}

	}
	
	public Set<QueryBuilder> getAllQueryBuilder(){
		Iterable<QueryBuilderJsonStringObject> qbjso= queryBuilderJsonStringObjectRepository.findAll();
		Set<QueryBuilder> querybuilders = new HashSet<QueryBuilder>();
		ObjectMapper mapper = new ObjectMapper();
		for ( QueryBuilderJsonStringObject qb: qbjso ){
			try {


				// Convert JSON string to Object
				String jsonInString = qb.getQueryBuilderJson();
				QueryBuilder queryBuilder = mapper.readValue(jsonInString, QueryBuilder.class);
				queryBuilder.setId(qb.getId());
				querybuilders.add(queryBuilder);

			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		
		return querybuilders;
	}
	
	public QueryBuilder getQueryBuilder(String queryId){
		QueryBuilderJsonStringObject qbjso=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(queryId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 qbjso= queryBuilderJsonStringObjectRepository.findOne(Long.parseLong(queryId));
		} else{
			 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
		}
			
		
		ObjectMapper mapper = new ObjectMapper();
		QueryBuilder queryBuilder=null;
			try {
				// Convert JSON string to Object
				String jsonInString = qbjso.getQueryBuilderJson();
				 queryBuilder = mapper.readValue(jsonInString, QueryBuilder.class);
				 queryBuilder.setId(qbjso.getId());

			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		
		
		
			return queryBuilder;
	}
	
	
	public Result getQueryBuilderExecute(String queryId){
		QueryBuilderJsonStringObject qbjso=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(queryId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 qbjso= queryBuilderJsonStringObjectRepository.findOne(Long.parseLong(queryId));
		} else{
			 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
		}
		
		
			
		
		Result result=null;
		Set<Parameter>parameter=qbjso.getExpertQuery().getParameter();
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		if (parameter!=null){
			for (Parameter p:parameter){
				paramsMap.put(p.getKey(), p.getValue());
			}
		}

		result = neo4jOperations.query(qbjso.getExpertQuery().getQuery(),paramsMap, true);
		return result;
	}
	
	public ExpertQuery getQueryBuilderQueryString(String queryId){
		QueryBuilderJsonStringObject qbjso=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(queryId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 qbjso= queryBuilderJsonStringObjectRepository.findOne(Long.parseLong(queryId));
		} else{
			 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
		}
		
		return qbjso.getExpertQuery();
	}




//######################### logic for executing query ##################

	private ExpertQuery buildQueryString(QueryBuilder queryBuilder) throws Exception{
		ExpertQuery expertQuery = new ExpertQuery();
		//initialise maps
//		paramsMap.clear();
//		parameter.clear();
		filterStatements.clear();
		actualFilterStatements.clear();
		orderStatements.clear();
		returnStatements.clear();
		cypherquery.clear();
		synonyms.clear();
		synonym = 'a';
		String query = "";
		distinct = queryBuilder.getDistinct();
		
		Node node = queryBuilder.getNode();
		
		//TODO erste relation auf optional prüfen!!
		//build the query
		buildNode(node, "", expertQuery);
		
		Iterator<String> it = cypherquery.iterator();
		while (it.hasNext()){
			query += "MATCH " + it.next();
			if (it.hasNext()) query += " ";
		}
		
		//Return-Clause muss vorhanden sein!!
		query += " RETURN";
		if (distinct) query += " DISTINCT ";
		
		it = returnStatements.iterator();
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += ",";
		}
		
		if (!orderStatements.isEmpty()) query += " ORDER BY ";
		
		it = orderStatements.iterator();
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += ", ";
		}
		
		if (queryBuilder.getLimitCount() != "") {
			query += " LIMIT " + queryBuilder.getLimitCount();
		}
		
		//erstellen des results
//		Result result = null;
//		result = neo4jOperations.query(query, paramsMap, true);
		expertQuery.setQuery(query);
		return expertQuery;
	}
	
	private void buildNode(Node node, String s, ExpertQuery expertQuery) throws Exception{
		
		String doublePoint = "";
		if (node.getType() != "") doublePoint = ":";
		
		String nodeString = s + "(" + synonym + doublePoint + node.getType() + ")";
		
		synonyms.put(node.getType(),String.valueOf(synonym));		
		synonym++;
		
		//filter zusammenstellen
		if (!node.getFilterAttributes().isEmpty()){
			solveFilter(node.getFilterAttributes(), node.getType(), expertQuery);
		}
		
		//order zusammenstellen
		if (!node.getOrderByAttributes().isEmpty()){
			solveOrder(node.getOrderByAttributes(), node.getType());
		}
		
		//return zusammenstellen
		if (!node.getReturnAttributes().isEmpty()){
			solveReturn(node.getReturnAttributes(), node.getType());
		}
		
		if (node.getRelationship().isEmpty()){
			
			if (!filterStatements.isEmpty()) nodeString += " WHERE ";
			Iterator<String> it = filterStatements.iterator();
			while (it.hasNext()){
				nodeString += it.next();
			}
			cypherquery.add(nodeString);
			filterStatements.clear();
			filterStatements.addAll(actualFilterStatements);
			actualFilterStatements.clear();
			return;
		} else {
			Iterator<Relationship> it = node.getRelationship().iterator();
			while (it.hasNext()){
				
				actualFilterStatements.addAll(filterStatements);
				
				buildRelation(it.next(), nodeString, expertQuery);
			}
			return;
		}	
	}
	
	
	private void buildRelation(Relationship relation, String s, ExpertQuery expertQuery) throws Exception{
		String relationship = "";
		
		if (relation.getDirection().equalsIgnoreCase("outgoing")){
			relationship = "-[" + synonym + ":" + relation.getRelationshipType() + "]->";
		} else {
			relationship = "<-[" + synonym + ":" + relation.getRelationshipType()+ "]-";
		}
		synonyms.put(relation.getRelationshipType(),String.valueOf(synonym));		
		synonym++;
		
		//filter zusammenstellen
		if (!relation.getFilterAttributes().isEmpty()){
			solveFilter(relation.getFilterAttributes(), relation.getRelationshipType(), expertQuery);
		}
		
		//order zusammenstellen
		if (!relation.getOrderByAttributes().isEmpty()){
			solveOrder(relation.getOrderByAttributes(), relation.getRelationshipType());
		}
		
		//return zusammenstellen
		if (!relation.getReturnAttributes().isEmpty()){
			solveReturn(relation.getReturnAttributes(), relation.getRelationshipType());
		}
		
		if (relation.getNode() == null){
			cypherquery.add(s + relationship);
			//dieser Zweig wird nie betreten
			return;
		} else {
			
			buildNode(relation.getNode(), s + relationship, expertQuery);
			return;
		}	
	}
	
	private void solveFilter(Set<FilterAttribute> filterSet, String type, ExpertQuery expertQuery) throws Exception{
		int i = 1;
		
		for (FilterAttribute f : filterSet){
			
			Set<Filters> set = f.getFilters();
			List<Filters> list = new ArrayList<Filters>(set);
			Collections.sort(list);
			
			for (Filters fil: list){	
			
			//hier könnte es eventuell zu einem Problem mit der Parameterbezeichnung kommen!!
			String paramName = type + f.getAttributeName() + i;
			i++;
			
			Parameter newParam = new Parameter();
			newParam.setChangeable(fil.getChangeable());
			newParam.setKey(paramName);
			newParam.setValue(fil.getValue());
			newParam.setType(fil.getType());
//			parameter.add(newParam);
			expertQuery.addParameter(newParam);
			AttributeTypes.testTypes(newParam);
//			paramsMap.put(paramName, newParam.getValue());
			
			String statement = "";
			if (fil.getIsBracketOpen()) statement += "(";
			statement += (synonyms.get(type) + "." + f.getAttributeName() + fil.getFilterType() + "{" + paramName + "}");
			if (fil.getIsBracketClosed()) statement += ")";
			if (!fil.getLogic().isEmpty()) statement += " " + fil.getLogic() + " ";
			filterStatements.add(statement);	
			}	
		}
	}
	
	private void solveOrder(Set<OrderByAttribute> obSet, String type){
		for (OrderByAttribute o : obSet){
			String dir = "";
			if (o.getDirection() != ""){
				dir = " " + o.getDirection();
			}
			//TODO evtl noch eine AGGREGATION bei orderby einfügen!!
			orderStatements.add(synonyms.get(type) + "." + o.getAttributeName() + dir);
		}
	}
	
	private void solveReturn (Set<ReturnAttribute> retSet, String type){
		int i = 1;
		//TODO Liste filtern ReturnAttribute
		for (ReturnAttribute r : retSet){
			
			String returnStatement = " ";
//			if (distinct) returnStatement += ("DISTINCT ");
			if (!r.getAggregation().isEmpty()){
				if (r.getAggregation().equalsIgnoreCase("none") == false){
					returnStatement += (r.getAggregation() + "(");
				}
			}
			returnStatement += (synonyms.get(type) + "." + r.getAttributeName());
			if (!r.getAggregation().isEmpty()){
				if (r.getAggregation().equalsIgnoreCase("none") == false){
					returnStatement += ")";
				}
			}
			//TODO eventuell eigenes Attribut für die ALIAS
			
			returnStatement += (" AS " + type + r.getAttributeName() + i);
			i++;
			returnStatements.add(returnStatement);
		}
	}
}