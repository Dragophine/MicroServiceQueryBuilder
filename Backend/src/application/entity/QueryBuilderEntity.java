package application.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;


@NodeEntity
public class QueryBuilderEntity {
	
		@GraphId private Long id;



		public QueryBuilderEntity(){
			
		}
}
