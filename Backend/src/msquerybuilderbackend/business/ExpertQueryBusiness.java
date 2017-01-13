package msquerybuilderbackend.business;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.ExpertQueryJsonObject;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;

@Component
public class ExpertQueryBusiness {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ParameterRepository parameterRepository;
	
	public Result executeExpertQuery(ExpertQueryJsonObject expertQuery) throws Exception{
		Map<String,Object> paramsMap = new HashMap<String,Object>();
    	Result result=null;
    	if (expertQuery.getParameter() !=null)
    	{
    		for (Parameter p:expertQuery.getParameter())
    		{
    			AttributeTypes.testTypes(p); 		
	    		paramsMap.put(p.getKey(), p.getValue());
	    		System.out.println(p.getKey() + " "+p.getValue());
	    	}
    		result= neo4jOperations.query(expertQuery.getQuery(), paramsMap,true);	    		
    	}
    	else
    	{
    		result = neo4jOperations.query(expertQuery.getQuery(),new HashMap<String, String>(), true);
    	}

	return result;
	}
	
	public Long createExpertQuery(ExpertQueryJsonObject expertQueryJsonObject) throws Exception{		 
	    ExpertQuery alreadyUsedName= expertQueryRepository.findByName(expertQueryJsonObject.getName());
		if (alreadyUsedName != null){		
			return 0L;
		}else{	
	    	for (Parameter p : expertQueryJsonObject.getParameter())
	    	{
				AttributeTypes.testTypes(p);					    		
	    	}
			ExpertQuery expertQuery = new ExpertQuery();
			expertQuery.setName(expertQueryJsonObject.getName());;
			expertQuery.setDescription(expertQueryJsonObject.getDescription());
			expertQuery.setParameter(expertQueryJsonObject.getParameter());
			expertQuery.setQuery(expertQueryJsonObject.getQuery());
			Category cat= categoryRepository.findByName(expertQueryJsonObject.getCategory());
			expertQuery.setCategory(cat);
	    	expertQueryRepository.save(expertQuery);
	    	ExpertQuery newExpertQuery = expertQueryRepository.findByName(expertQueryJsonObject.getName());
	    	return newExpertQuery.getId();
		}
	}
	
	public void deleteExpertQuery(String queryId){
		ExpertQuery expertQuery=null;
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
			 expertQuery= expertQueryRepository.findOne(id);
		} else{
			 expertQuery= expertQueryRepository.findByName(queryId);
		}
    	for (Parameter p : expertQuery.getParameter())
    	{
	    	parameterRepository.delete(p.getId());
    	}
    	
    	expertQueryRepository.delete(expertQuery.getId());
	}
	
	
	public ExpertQuery updateExpertQuery(String queryId, ExpertQueryJsonObject updatedQuery) throws Exception{
		ExpertQuery expertQuery=null;
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
			 expertQuery= expertQueryRepository.findOne(Long.parseLong(queryId));
		} else{
			 expertQuery= expertQueryRepository.findByName(queryId);
		}
		
		ExpertQuery alreadyUsedName=expertQueryRepository.findByName(updatedQuery.getName());
		if ((updatedQuery.getName().equals(expertQuery.getName()))||(alreadyUsedName==null)){
		
	    	for (Parameter p : expertQuery.getParameter())
	    	{			    	
		    	parameterRepository.delete(p.getId());
	    	}
	    	
	    	for (Parameter p : updatedQuery.getParameter())
	    	{			    	
		    	AttributeTypes.testTypes(p);
	    	}
	    	
    	
    	
	    	expertQuery.setDescription(updatedQuery.getDescription());
	    	expertQuery.setName(updatedQuery.getName());
	    	expertQuery.setQuery(updatedQuery.getQuery());
	    	Category cat = categoryRepository.findByName(updatedQuery.getCategory());
	    	
	    	expertQuery.setCategory(cat);
	    	expertQuery.setParameter(updatedQuery.getParameter());
	    	
	    	expertQueryRepository.save(expertQuery);
	    	return expertQuery;
		}	else{
			return null;
		}
	}
	
	public Set<ExpertQueryJsonObject> getAllExpertQueries(){
		Iterable<ExpertQuery> expertqueries= expertQueryRepository.findAll();
		
		Set<ExpertQueryJsonObject> expertqueriesjson = new HashSet<ExpertQueryJsonObject>();
		for (ExpertQuery eq:expertqueries){
			ExpertQueryJsonObject eqjo = new ExpertQueryJsonObject();
			eqjo.setName(eq.getName());
			eqjo.setDescription(eq.getDescription());
			eqjo.setParameter(eq.getParameter());
			eqjo.setId(eq.getId());
			eqjo.setQuery(eq.getQuery());
			eqjo.setCategory(eq.getCategory().getName());
			expertqueriesjson.add(eqjo);
		}
		return expertqueriesjson;

	}
	
	public ExpertQueryJsonObject getExpertQuery(String queryId){
		ExpertQuery expertQuery=null;
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
			 expertQuery= expertQueryRepository.findOne(Long.parseLong(queryId));
		} else{
			 expertQuery= expertQueryRepository.findByName(queryId);
		}
		
		ExpertQueryJsonObject eqjo = new ExpertQueryJsonObject();
		eqjo.setName(expertQuery.getName());
		eqjo.setDescription(expertQuery.getDescription());
		eqjo.setParameter(expertQuery.getParameter());
		eqjo.setId(expertQuery.getId());
		eqjo.setQuery(expertQuery.getQuery());
		eqjo.setCategory(expertQuery.getCategory().getName());

		return eqjo;
	}
}
