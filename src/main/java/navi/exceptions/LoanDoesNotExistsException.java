package navi.exceptions;

public class LoanDoesNotExistsException extends RuntimeException{
    public LoanDoesNotExistsException(String msg){
        super(msg);
    }
}
