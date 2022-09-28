package navi.exceptions;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String msg){
        super("Invalid input : " + msg);
    }
}
