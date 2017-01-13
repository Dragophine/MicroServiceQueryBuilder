package msquerybuilderbackend.business;

import java.util.List;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.repository.CategoryRepository;


@Component
public class CategoryBusiness {

	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<Category> getAllCategories(){
		List<Category> categories= categoryRepository.getAllCategories();
		return categories;
	}
	
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
	
	public List<String> getNameList(){
		List<String> names = categoryRepository.getNames();
		return names;
	}
	
	public Category updateCategory(String categoryId, Category cat){
		Category category=null;
		if (Long.parseLong(categoryId) >=0){
			 category= categoryRepository.findOne(Long.parseLong(categoryId));
		} else{
			 category= categoryRepository.findByName(categoryId);
		}
		
		category.setName(cat.getName());
		category.setDescription(cat.getDescription());
		categoryRepository.save(category);		
		return category;
	}
	
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
