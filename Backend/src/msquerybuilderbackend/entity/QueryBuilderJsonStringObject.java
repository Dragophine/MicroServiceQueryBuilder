package msquerybuilderbackend.entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


/**
 * entity class of QueryBuilderJsonStringObject (for the backend and neo4j database) with getter and setter
 * QueryBuilderJsonStringObject is an entity used only in the backend and neo4j database 
 * the entity is relevant for saving the QueryBuilder object as JSONString to keep all details and hierarchies
 * because saving it as a QueryBuilder object in the neo4j database would mean that not all dependencies would be saved 
 * when there are many levels in the hierarchy
 * @author drago
 *
 */
@NodeEntity
public class QueryBuilderJsonStringObject {
	
	@GraphId private Long id;
	private String name="";
	private String description="";
	
	private String queryBuilderJson="";
	@Relationship(type = "HAS_CATEGORY", direction = Relationship.OUTGOING)
	private Category category;
	@Relationship(type = "HAS_EXPERTQUERY", direction = Relationship.OUTGOING)
	private ExpertQuery expertQuery;
//	private Set<ExpertQuery> expertQuery = new HashSet<ExpertQuery>(0);
	
//	public Set<ExpertQuery> getExpertQuery() {
//		if (this.expertQuery==null){
//			return new HashSet<ExpertQuery>(0);
//		}
//		return this.expertQuery;
//	}
	
	
	public String getQueryBuilderJson() {
		return queryBuilderJson;
	}

	public ExpertQuery getExpertQuery() {
		return expertQuery;
	}

	public void setExpertQuery(ExpertQuery expertQuery) {
		this.expertQuery = expertQuery;
	}

	public void setQueryBuilderJson(String queryBuilderJson) {
		this.queryBuilderJson = queryBuilderJson;
	}

//	public void addExpertQuery(ExpertQuery e){
//		if (this.expertQuery==null){
//			this.expertQuery= new HashSet<ExpertQuery>(0);
//		}
//		this.expertQuery.add(e);
//	}

//	public void setExpertQuery(Set<ExpertQuery> expertquery) {
//		this.expertQuery = expertquery;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	
}
