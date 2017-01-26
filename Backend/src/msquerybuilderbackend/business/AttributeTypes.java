package msquerybuilderbackend.business;

import msquerybuilderbackend.entity.Alert;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.exception.InvalidTypeException;


/**
 * Class which contains method to check types if their value is from the given type and throws an execution if nots
 * @author drago
 *
 */
public class AttributeTypes {
	
	/**
	 * method which tests the types of a given alert if their value is from the given type
	 * @param a is the given alert to check
	 * @throws Exception when a given value is not from the given type
	 */
	 public static void testTypes(Alert a) throws Exception{
	    	switch(a.getType()){
		case "int":
		case "integer":
		case "Integer":
			try{
				
			/**
			 * Castet man einen Integer mittels (String) auf einen Stringwert kommt es zu einen Fehler:
			 * java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
			 * at msquerybuilderbackend.rest.AlertService.executeAlert(AlertService.java:95) ~[classes/:na]
			 * --> Stattdessen wird toString() verwendet.
			 */
			int i = Integer.parseInt(a.getValue().toString());
			a.setValue(i);
			}catch (Exception e){
				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
				
			}
		break;
		
		case "double":
		case "Double":
			try{
	    			double i = Double.parseDouble((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
		
		case "char":
		case "Char":
			try{
	    			char i=(char) a.getValue();
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
			
		case "boolean":
		case "Boolean":
			try{
	    			boolean i=(boolean) a.getValue();
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
			
		case "float":
		case "Float":
			try{
	    			float i = Float.parseFloat((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
		
		case "long":
		case "Long":
			try{
	    			long i = Long.parseLong((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
			
		case "short":
		case "Short":
			try{
	    			short i = Short.parseShort((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
			
		case "byte":
		case "Byte":
			try{
	    			byte i = Byte.parseByte((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
			break;
			
		default: 
			
			break;
		}
	    }	
	 
	 /**
	  * method which tests the types of a given parameter if their value is from the given type
	  * @param p is the given parameter to check
	  * @throws Exception when a gien value is not from the given type
	  */
	 public static void testTypes(Parameter p) throws Exception{
	    	switch(p.getType()){
    		case "int":
    		case "integer":
    		case "Integer":
    			try{
    			int i = Integer.parseInt((String)p.getValue());
    			p.setValue(i);
    			}catch (Exception e){
    				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
    				
    			}
    		break;
    		
    		case "double":
    		case "Double":
    			try{
	    			double i = Double.parseDouble((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    		
    		case "char":
    		case "Char":
    			try{
	    			char i=(char) p.getValue();
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "boolean":
    		case "Boolean":
    			try{
	    			boolean i=(boolean) p.getValue();
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "float":
    		case "Float":
    			try{
	    			float i = Float.parseFloat((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    		
    		case "long":
    		case "Long":
    			try{
	    			long i = Long.parseLong((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "short":
    		case "Short":
    			try{
	    			short i = Short.parseShort((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "byte":
    		case "Byte":
    			try{
	    			byte i = Byte.parseByte((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		default: 
    			
    			break;
    		}
	    }
}
