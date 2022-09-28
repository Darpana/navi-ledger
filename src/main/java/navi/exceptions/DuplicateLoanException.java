package navi.exceptions;

public class DuplicateLoanException extends RuntimeException{
    public DuplicateLoanException(String msg){
        super(msg);
    }
}
