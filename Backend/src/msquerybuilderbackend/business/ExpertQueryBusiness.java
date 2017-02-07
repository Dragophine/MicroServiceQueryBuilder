package msquerybuilderbackend.business;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.ExpertQueryJsonObject;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.User;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;
import msquerybuilderbackend.repository.UserRepository;

/**
 * Class for all activities with neo4j database regarding the entity ExpertQueryJsonObject in the application (ExpertQuery in the neo4j database)
 * @author drago
 *
 */
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
	@Autowired
	UserRepository userRepository;
	
	/**
	 * method which executes the querystring in the neo4j database of a given expertQuery(jsonobject) 
	 * @param expertQuery is the object which contains the querystring and the parameter
	 * @return the result of the executed query from the neo4j database
	 * @throws Exception if a value and its given type of the expertQuery object do not match
	 */
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
	
	/**
	 * method which checks the name of the given object if it already exists and returns 0L if true,
	 * otherwise it converts the given ExpertQueryJsonObject to an ExpertQuery object and creates it in the neo4j database as an ExpertQuery object
	 * @param expertQueryJsonObject is the expertQuery object to create
	 * @return the neo4j ID of the new expertQuery object
	 * @throws Exception if a value and its given type of the expertQuery object do not match
	 */
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
			User author= userRepository.findByEmail(expertQueryJsonObject.getAuthor());
			Category cat= categoryRepository.findByName(expertQueryJsonObject.getCategory().trim());
			expertQuery.setCategory(cat);
			expertQuery.setAuthor(author);
	    	expertQueryRepository.save(expertQuery);
	    	ExpertQuery newExpertQuery = expertQueryRepository.findByName(expertQueryJsonObject.getName());
	    	return newExpertQuery.getId();
		}
	}
	
	/**
	 * method which deletes a certain ExpertQuery object from the neo4j database
	 * @param queryId is the ID of the object to delete; can be the neo4j ID or the unique name
	 */
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
			 * If the given value can not be converted to Long, the inherent value is 
			 * obviously not a number, so it must be the unique name.
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
	
	
	/**
	 * method which updates a certain expertQuery object in the neo4j database with given content
	 * @param queryId is the ID of the expertQuery object to be updated; can be the neo4j ID or the unique name
	 * @param updatedQuery is an ExpertQuryJsonObject with the new content
	 * @return the updated expertQuery object
	 * @throws Exception  if a value and its given type of the expertQuery object do not match
	 */
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
			 * If the given value can not be converted to Long, the inherent value is 
			 * obviously not a number, so it must be the unique name.
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
	    	User author= userRepository.findByEmail(updatedQuery.getAuthor());
	    	expertQuery.setCategory(cat);
	    	expertQuery.setAuthor(author);
	    	expertQuery.setParameter(updatedQuery.getParameter());
	    	
	    	expertQueryRepository.save(expertQuery);
	    	return expertQuery;
		}	else{
			return null;
		}
	}
	
	/**
	 * method which queries all expertQuery objects from the neo4j database and converts them to ExpertQueryJsonObjects for the frontend
	 * @return a set of ExpertQueryJsonObjects
	 */
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
			if(eq.getCategory() != null)
			{
				eqjo.setCategory(eq.getCategory().getName());	
			}
			if(eq.getAuthor()!=null){
				eqjo.setAuthor(eq.getAuthor().getEmail());
			}
			expertqueriesjson.add(eqjo);
		}
		return expertqueriesjson;

	}
	
	/**
	 * method which queries a certain ExpertQuery Object from the neo4j database and converts it to a ExpertQueryJsonObject for the frontend
	 * @param queryId is the ID of the object to find; can be the neo4j ID or the unique name
	 * @return the ExpertQueryJsonObject found as ExpertQuery object in the neo4j database
	 */
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
			 * If the given value can not be converted to Long, the inherent value is 
			 * obviously not a number, so it must be the unique name.
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
		if(expertQuery.getCategory() != null)
		{
			eqjo.setCategory(expertQuery.getCategory().getName());	
		}
		if(expertQuery.getAuthor()!=null){
			eqjo.setAuthor(expertQuery.getAuthor().getEmail());
		}

		return eqjo;
	}
	
	
	/**
	 * method which queries ExpertQuery objects in the neo4j database by given parameters and converts them to ExpertQueryJsonObject for the frontend
	 * @param category is the specific category in which the ExpertQuery objects have to be
	 * @param name is the specific name or pattern which the ExpertQuery objects have to be named or contain in the name
	 * @param description is a specific pattern which the ExpertQuery objects have to contain in the description
	 * @return a set of ExpertQueryJsonObjects which have been found with the given parameters
	 */
	public Set<ExpertQueryJsonObject> getExpertQueryBySearch(String category, String name, String description){
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
				
		Set<ExpertQuery> expertqueries= expertQueryRepository.searchByParameter(description, name, category );
		Set<ExpertQueryJsonObject> expertqueriesjson = new HashSet<ExpertQueryJsonObject>();
		for (ExpertQuery eq:expertqueries){
			ExpertQueryJsonObject eqjo = new ExpertQueryJsonObject();
			eqjo.setName(eq.getName());
			eqjo.setDescription(eq.getDescription());
			eqjo.setParameter(eq.getParameter());
			eqjo.setId(eq.getId());
			eqjo.setQuery(eq.getQuery());
			if(eq.getCategory() != null)
			{
				eqjo.setCategory(eq.getCategory().getName());	
			}
			if(eq.getAuthor()!=null){
				eqjo.setAuthor(eq.getAuthor().getEmail());
			}
			expertqueriesjson.add(eqjo);
		}
		return expertqueriesjson;
		
	}
}
