package test.elasticsearch_rest.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserDaoInternalException
    extends RuntimeException 
{

    private static final long serialVersionUID = 1L;

    public UserDaoInternalException() {
        super();
    }

    public UserDaoInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDaoInternalException(String message) {
        super(message);
    }

    public UserDaoInternalException(Throwable cause) {
        super(cause);
    }
    
}