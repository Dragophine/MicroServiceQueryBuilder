package msquerybuilderbackend.business;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
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
	public Result executeQueryBuilderQuery(QueryBuilder queryBuilder){
		Map<String,Object> paramsMap = new HashMap<String,Object>();
    	Result result=null;
    	
//    		result = neo4jOperations.query(queryBuilder.getQuery(),new HashMap<String, String>(), true);
    	

	return result;
	}
	
	public Long createQueryBuilder(QueryBuilder queryBuilder) throws JsonProcessingException{

		QueryBuilderJsonStringObject alreadyUsedName= queryBuilderJsonStringObjectRepository.findByName(queryBuilder.getName());
		if (alreadyUsedName != null){
			
			return 0L;	
		}else{
			Category category = categoryRepository.findByName(queryBuilder.getCategory());
			
/**
* Interpretation des Querybuilders wie bei execute ausständig
*/				
//			expertQuery.setName(queryBuilder.getName());
//			expertQuery.setDescription(queryBuilder.getDescription());
//			expertQuery.setCategory(category);
	//		queryBuilderJsonStringObject.addExpertQuery(expertquery);
			
			/**
			 * ExpertQuery auch den Namen und Beschreibung geben
			 */
			QueryBuilderJsonStringObject qbjso = new QueryBuilderJsonStringObject();
			qbjso.setName(queryBuilder.getName());
			qbjso.setDescription(queryBuilder.getDescription());
			qbjso.setCategory(category);
			ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String queryBuilderJsonString = mapper.writeValueAsString(queryBuilder);
			qbjso.setQueryBuilderJson(queryBuilderJsonString);
			
			qbjso.setExpertQuery(null);
	    	queryBuilderJsonStringObjectRepository.save(qbjso);
	    	QueryBuilderJsonStringObject returnNew=queryBuilderJsonStringObjectRepository.findByName(queryBuilder.getName());
	
	    	return returnNew.getId();
		}
	}
}
