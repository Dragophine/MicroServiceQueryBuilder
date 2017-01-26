package msquerybuilderbackend.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
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
	

	public Result executeQueryBuilderQuery(QueryBuilder queryBuilder) throws Exception{
	//	Map<String,Object> paramsMap = new HashMap<String,Object>();
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		Result result = null;
		
		QueryBuilderWriterBusiness queryBuilderWriterBusiness= new QueryBuilderWriterBusiness();
		ExpertQuery expertQuery = queryBuilderWriterBusiness.writeQueryBuilderString(queryBuilder);
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
			Category category = categoryRepository.findByName(queryBuilder.getCategory().trim());
			
/**
* Interpretation des Querybuilders wie bei execute ausständig
*/			
			//Hoffe ich hab das so richtig verstanden...
			//ExpertQuery erstellen, QueryString und Parameter übergeben,
			//an QueryBuilderJsonStringObject anhängen und das ganze speichern
			//???
			QueryBuilderWriterBusiness queryBuilderWriterBusiness= new QueryBuilderWriterBusiness();
			ExpertQuery expertQuery = queryBuilderWriterBusiness.writeQueryBuilderString(queryBuilder);
			

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
		QueryBuilderWriterBusiness queryBuilderWriterBusiness = new QueryBuilderWriterBusiness();
		return queryBuilderWriterBusiness.writeQueryBuilderString(queryBuilder);
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
			QueryBuilderWriterBusiness queryBuilderWriterBusiness = new QueryBuilderWriterBusiness();
			ExpertQuery expertQuery = queryBuilderWriterBusiness.writeQueryBuilderString(updatedQuery);
			
			
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

	public Set<QueryBuilder> getQueryBuilderBySearch(String category, String name, String description){
//		String categoryParam="";
//		if(category!=null) {
//			categoryParam="c.name='"+category+"'";
//			if ((name!=null)|| (description!=null)) categoryParam="and "+categoryParam;
//		}
//		String nameParam="";
//		if(name!=null){
//			nameParam="n.name='.*"+name+".*";
//			if ((description!=null)) nameParam="and "+nameParam;
//		}
//		 
//		String descParam="";
//		if(description!=null) descParam="n.description=~ '.*"+description+".*'";;
//		
//		
//		System.out.println(categoryParam);
//		System.out.println(nameParam);
//		System.out.println(descParam);
		if (category==null){
			category=".*.*";
		}else{
			category=".*"+category.trim()+".*";
		}
		if (name==null){
			name=".*.*";
		}else{
			name=".*"+name+".*";
		}
		if (description==null){
			description=".*.*";
			
		}else{
			description=".*"+description+".*";
		}
//		List<QueryBuilderJsonStringObject> categoryList=queryBuilderJsonStringObjectRepository.searchByCategory(category.trim());
//		List<QueryBuilderJsonStringObject> nameList=queryBuilderJsonStringObjectRepository.searchByName(name);
//		List<QueryBuilderJsonStringObject> descList=queryBuilderJsonStringObjectRepository.searchByDescription(description);
//		
//		Collection<QueryBuilderJsonStringObject> commonList = CollectionUtils.retainAll(categoryList, nameList);
//		Collection<QueryBuilderJsonStringObject> commonList2= CollectionUtils.retainAll(commonList, descList);
		
		List<QueryBuilderJsonStringObject> qbjso= queryBuilderJsonStringObjectRepository.searchByParameter(description, name, category);
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




	

	

}