package msquerybuilderbackend.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.FilterAttribute;
import msquerybuilderbackend.entity.Filters;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.OrderByAttribute;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.Relationship;
import msquerybuilderbackend.entity.ReturnAttribute;

/**
 * Class for building the neo4j queryString of the QueryBuilder Object
 * @author Christian
 */

public class QueryBuilderWriterBusiness {
	private QueryBuilder queryBuilder;
	private char synonym;
	private Map<String,String> synonyms;
	private boolean distinct = false;
	private String query;
	private LinkedList<String> filterStatements;
	private LinkedList<String> actualFilterStatements;
	private HashMap<Integer, String> orderStatements;
	private LinkedList<String> returnStatements;
	private LinkedList<String> cypherquery;
	private Node node;
	private ExpertQuery expertQuery;

	public QueryBuilderWriterBusiness(){}
	
	/**
	 * Method which builds the queryString of the QueryBuilder-Object
	 * Furthermore the queryString with the belonging Parameters are saved into a ExpertQuery-Object
	 * @param qb the given QueryBuilder-Object the String is built of
	 * @return Returns a ExpertQuery-Object which holds the QueryString and belonging Parameters
	 * @throws Exception because used Methods throw Exception
	 */
	public ExpertQuery writeQueryBuilderString(QueryBuilder qb) throws Exception{
		queryBuilder = qb;
		expertQuery = new ExpertQuery();
		synonyms= new HashMap<String,String>();
		filterStatements = new LinkedList<String>();
		actualFilterStatements = new LinkedList<String>();
		orderStatements = new HashMap<Integer, String>();
		returnStatements = new LinkedList<String>();
		cypherquery = new LinkedList<String>();
		synonyms= new HashMap<String,String>();
		synonym = 'a';
		query = "";
		distinct = queryBuilder.getDistinct();
		node = queryBuilder.getNode();
		
		/**
		 * This method starts the recursive Building of the QueryString
		 */
		buildNode(node, "", expertQuery);
		
		/**
		 * After recursion the collected parts of the QueryString are connected
		 * First every MATCH-clause with the WHERE-Clauses 
		 * Then the RETURN and if available the DISTINCT-Clause
		 * Last ORDER BY, SKIP and LIMIT
		 */
		Iterator<String> it = cypherquery.iterator();
		while (it.hasNext()){
			query += "MATCH " + it.next();
			if (it.hasNext()) query += " ";
		}
		
		query += " RETURN";
		if (distinct) query += " DISTINCT ";
		
		it = returnStatements.iterator();
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += ",";
		}
		
		if (!orderStatements.isEmpty()) query += " ORDER BY ";
		
		/**
		 * Sort the ORDER BY-Clauses by the Index given by the User
		 */
		Map<Integer, String> treeMap = new TreeMap<Integer, String>(orderStatements);
		Collection<String> s = treeMap.values();
		it = s.iterator();
		
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += ", ";
		}
		
		if (queryBuilder.getSkip() != "") {
			query += " SKIP " + queryBuilder.getSkip();
		}
		
		if (queryBuilder.getLimitCount() != "") {
			query += " LIMIT " + queryBuilder.getLimitCount();
		}
		
		expertQuery.setQuery(query);
		return expertQuery;
	
	}

	/**
	 * Method which builds the Part for the Nodes in Cypher-Syntax
	 * @param node The Node-Object given by the QueryBuilder-Object
	 * @param s A String which is added to the cypherQuery-List when a Leaf-Node is hit, this string gets passed till this happens
	 * @param expertQuery the fitting expertQuery, gets passed to add belonging parameter
	 * @throws Exception because used Methods throw exceptions
	 */
	private void buildNode(Node node, String s, ExpertQuery expertQuery) throws Exception{
		
		String doublePoint = "";
		if (node.getType() != "") doublePoint = ":";
		
		String nodeString = s + "(" + synonym + doublePoint + node.getType() + ")";
		
		synonyms.put(node.getType(),String.valueOf(synonym));		
		synonym++;
		
		/**
		 * create FilterStatements for actual Node
		 */
		if (!node.getFilterAttributes().isEmpty()){
			solveFilter(node.getFilterAttributes(), node.getType(), expertQuery);
		}
		
		/**
		 * create OrderByStatements for actual Node
		 */
		if (!node.getOrderByAttributes().isEmpty()){
			solveOrder(node.getOrderByAttributes(), node.getType(), node.getReturnAttributes());
		}
		
		/**
		 * create ReturnStatements for actual Node
		 */
		if (!node.getReturnAttributes().isEmpty()){
			solveReturn(node.getReturnAttributes(), node.getType());
		}
		
		/**
		 * if there is no further Relation...
		 */
		if (node.getRelationship().isEmpty()){
			
			if (!filterStatements.isEmpty()) nodeString += " WHERE ";
			Iterator<String> it = filterStatements.iterator();
			int i = 1;
			String actualStatement = "";
			
			/**
			 * ...add actual String (Path till leave-Node and belonging WHERE-Clauses) to the cypherQuery-List...
			 */
			while (it.hasNext()){
				actualStatement = it.next();
				if (actualStatement.contains(" AND ") || actualStatement.contains(" OR ")){
					nodeString += actualStatement;
					i++;
				} else if (i < filterStatements.size()){
					nodeString += actualStatement + " AND ";
					i++;
				} else {
					nodeString += actualStatement;
				}
			}
			
			cypherquery.add(nodeString);
			filterStatements.clear();
			filterStatements.addAll(actualFilterStatements);
			actualFilterStatements.clear();
			return;
		} else {
			/**
			 * ...else go on with recursion (next relation)
			 */
			Iterator<Relationship> it = node.getRelationship().iterator();
			while (it.hasNext()){
				
				actualFilterStatements.addAll(filterStatements);
				
				buildRelation(it.next(), nodeString, expertQuery);
			}
			return;
		}	
	}
	
	/**
	 * Method which builds the path of the Relations in Cypher-Syntax
	 * @param relation the given Relation by the QueryBuilder-Object
	 * @param s A String which is added to the cypherQuery-List when a Leaf-Node is hit, this string gets passed till this happens
	 * @param expertQuery the belonging ExpertQuery gets passed to add Parameter
	 * @throws Exception because used Methods throw Exceptions
	 */
	private void buildRelation(Relationship relation, String s, ExpertQuery expertQuery) throws Exception{
		String relationship = "";
		
		/**
		 * check direction
		 */
		if (relation.getDirection().equalsIgnoreCase("outgoing")){
			relationship = "-[" + synonym + ":" + relation.getRelationshipType() + "]->";
		} else {
			relationship = "<-[" + synonym + ":" + relation.getRelationshipType()+ "]-";
		}
		synonyms.put(relation.getRelationshipType(),String.valueOf(synonym));		
		synonym++;
		

		/**
		 * create filterStatements for actual Relation
		 */
		if (!relation.getFilterAttributes().isEmpty()){
			solveFilter(relation.getFilterAttributes(), relation.getRelationshipType(), expertQuery);
		}
		
		/**
		 * create OrderByStatements for actual Relation
		 */
		if (!relation.getOrderByAttributes().isEmpty()){
			solveOrder(relation.getOrderByAttributes(), relation.getRelationshipType(), relation.getReturnAttributes());
		}
		
		/**
		 * create returnStatements for actual Relation
		 */
		if (!relation.getReturnAttributes().isEmpty()){
			solveReturn(relation.getReturnAttributes(), relation.getRelationshipType());
		}
		
		/**
		 * if there is no further node...
		 */
		if (relation.getNode() == null){
			/**
			 * ...add the actual String to the cypherQuery-List... 
			 */
			cypherquery.add(s + relationship);
			return;
		} else {
			/**
			 * ... else go on with recursion (next node)
			 */
			buildNode(relation.getNode(), s + relationship, expertQuery);
			return;
		}	
	}
	
	/**
	 * Method to get the Filter-Statements out of the given Node or Relation
	 * @param filterSet the given FilterAttributes by the QueryBuilder-Object
	 * @param type the type of the method-calling Object (Node or Relation)
	 * @param expertQuery in this Method the expertQuery receives its parameter
	 * @throws Exception because used method testTypes throws Exception
	 */
	private void solveFilter(Set<FilterAttribute> filterSet, String type, ExpertQuery expertQuery) throws Exception{
		int i = 1;
		/**
		 * sort the list of the FilterAttributes in alphabetical order
		 */
		List<FilterAttribute> aList = new ArrayList<FilterAttribute>(filterSet);
		Collections.sort(aList);
		
		for (FilterAttribute f : aList){
			String logic = " " + f.getLogic() + " ";
			
			/**
			 * Sort the list of the Filters by id
			 */
			Set<Filters> fSet = f.getFilters();
			List<Filters> list = new ArrayList<Filters>(fSet);
			Collections.sort(list);
			int j = 1;
			
			for (Filters fil: list){	
				String paramName = type + f.getAttributeName() + i;
				i++;
				/**
				 * create Parameter for FilterStatement
				 */
				Parameter newParam = new Parameter();
				newParam.setChangeable(fil.getChangeable());
				newParam.setKey(paramName);
				newParam.setValue(fil.getValue());
				newParam.setType(fil.getType());
				expertQuery.addParameter(newParam);
				AttributeTypes.testTypes(newParam);
				
				/**
				 * check about Brackets and Logic
				 */
				String statement = "";
				if (fil.getIsBracketOpen()) statement += "(";
				statement += (synonyms.get(type) + "." + f.getAttributeName() + fil.getFilterType() + "{" + paramName + "}");
				if (fil.getIsBracketClosed()) statement += ")";
				if (!fil.getLogic().isEmpty()) statement += " " + fil.getLogic() + " ";
				if (j >= list.size()) statement += logic;
				j++;
				filterStatements.add(statement);	
			}	
		}
	}

	/**
	 * Method to get OrderBy-Statements out of Node or Relation
	 * @param obSet the given Set of OrderBy-Attributes
	 * @param type the given Type of the calling Object (Node or Relation)
	 * @param rSet needed for Aggregation
	 */
	private void solveOrder(Set<OrderByAttribute> obSet, String type, Set<ReturnAttribute> rSet){
		String agg = "";
		for (OrderByAttribute o : obSet){
			String dir = "";
			if (o.getDirection() != ""){
				dir = " " + o.getDirection();
			}
			/**
			 * this loop checks if there is a Return-Attribute equals to the OrderBy-Attribute
			 * If this is the case, the aggregation of the Return-Attribute is checked and if
			 * available matched to the OrderBy-Attribute
			 */
			for (ReturnAttribute r: rSet){
				if (r.getAttributeName().equals(o.getAttributeName())){
					if (!r.getAggregation().isEmpty()){
						agg = r.getAggregation();
					}
				}
			}
			
			/**
			 * OrderBy-Statement is added whether with or without aggregation to the Map
			 */
			if (!(agg.equalsIgnoreCase("none") || agg.equalsIgnoreCase(""))){
					orderStatements.put(o.getId(), agg + "(" +  synonyms.get(type) + "." + o.getAttributeName() + ")" + dir);
				} else {
					orderStatements.put(o.getId(), synonyms.get(type) + "." + o.getAttributeName() + dir);	
			}
		}
	}
	
	/**
	 * Method to get the Return-Statements out of the node or relation
	 * @param retSet the Set of Return-Attributes
	 * @param type the type of the calling Object (Node or Relation
	 */
	private void solveReturn (Set<ReturnAttribute> retSet, String type){
		int i = 1;
		for (ReturnAttribute r : retSet){
			
			String returnStatement = " ";

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
		
			if ((r.getAlias().isEmpty() || r.getAlias().equalsIgnoreCase(""))){
				returnStatement += (" AS " + type + r.getAttributeName() + i);
				i++;
			} else {
				returnStatement += (" AS " + r.getAlias());
			}		
			returnStatements.add(returnStatement);
		}
	}	
}
