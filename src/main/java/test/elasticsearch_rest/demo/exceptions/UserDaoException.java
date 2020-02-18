package test.elasticsearch_rest.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserDaoException
    extends RuntimeException 
{

    private static final long serialVersionUID = 1L;

    public UserDaoException() {
        super();
    }

    public UserDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDaoException(String message) {
        super(message);
    }

    public UserDaoException(Throwable cause) {
        super(cause);
    }
    
}