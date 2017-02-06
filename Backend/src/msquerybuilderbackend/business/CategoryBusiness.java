package msquerybuilderbackend.business;

import java.util.List;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.repository.CategoryRepository;

/**
 * Class for all activities with neo4j database regarding the entity Category
 * @author drago
 *
 */
@Component
public class CategoryBusiness {

	
	@Autowired
	CategoryRepository categoryRepository;
	
	
	/**
	 * method which queries all the categories from the neo4j database
	 * @return a list of all categories found in the neo4j database
	 */
	public List<Category> getAllCategories(){
		List<Category> categories= categoryRepository.getAllCategories();
		return categories;
	}
	
	
	/**
	 * method which checks the name of the given category and returns 0L if the name already exists,
	 * otherwise it creates a given category object in the neo4j database
	 * @param category is the given category object to create
	 * @return the neo4j ID of the created category
	 */
	public Long createCategory(Category category){
		Category alreadyUsedName= categoryRepository.findByName(category.getName());
		if (alreadyUsedName != null){
			
			return 0L;
		}else{
			categoryRepository.save(category);
			Category newCategory = categoryRepository.findByName(category.getName());
			return newCategory.getId();
		}
	}
	
	
	/**
	 * method which creates a name list of all categories in the neo4j database
	 * @return a list of all names as strings
	 */
	public List<String> getNameList(){
		List<String> names = categoryRepository.getNames();
		return names;
	}
	
	/**
	 * method which updates a certain category in the neo4j database with the content of the given category object
	 * @param categoryId is the ID of the category to be updated; can be the neo4j ID or the unique name
	 * @param cat is the category object with the new content
	 * @return the updated category
	 */
	public Category updateCategory(String categoryId, Category cat){
		Category category=null;
		if (Long.parseLong(categoryId) >=0){
			 category= categoryRepository.findOne(Long.parseLong(categoryId));
		} else{
			 category= categoryRepository.findByName(categoryId);
		}
		

			Category alreadyUsedName=categoryRepository.findByName(cat.getName());
		if ((cat.getName().equals(category.getName()))||(alreadyUsedName==null)){	
			category.setName(cat.getName());
			category.setDescription(cat.getDescription());
			categoryRepository.save(category);		
			return category;
		}else{
			return null;
		}
	}
	
	
	/**
	 * method which deletes a certain category in the neo4j database
	 * @param categoryId is the ID of the category to be deleted; can be the neo4j ID or the unique name
	 */
	public void deleteCategory(String categoryId){
		Category category=null;
		if (Long.parseLong(categoryId) >=0){
			 category= categoryRepository.findOne(Long.parseLong(categoryId));
		} else{
			 category= categoryRepository.findByName(categoryId);
		}
		categoryRepository.delete(category);		
	}
}
