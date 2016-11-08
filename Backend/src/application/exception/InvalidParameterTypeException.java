package application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT)  // 404
public class InvalidParameterTypeException extends RuntimeException{

	public InvalidParameterTypeException(String msg){
		super(msg);
	}
}
