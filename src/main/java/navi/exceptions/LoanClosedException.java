package navi.exceptions;

public class LoanClosedException extends RuntimeException{
    public LoanClosedException(String msg){
        super(msg);
    }
}