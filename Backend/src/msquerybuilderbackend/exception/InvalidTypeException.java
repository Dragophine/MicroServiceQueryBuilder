package msquerybuilderbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT)  // 409
public class InvalidTypeException extends RuntimeException{

	public InvalidTypeException(String msg){
		super(msg);
	}
}
