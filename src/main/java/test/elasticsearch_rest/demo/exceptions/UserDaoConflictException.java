package test.elasticsearch_rest.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserDaoConflictException
    extends RuntimeException 
{

    private static final long serialVersionUID = 1L;

    public UserDaoConflictException() {
        super();
    }

    public UserDaoConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDaoConflictException(String message) {
        super(message);
    }

    public UserDaoConflictException(Throwable cause) {
        super(cause);
    }
    
}