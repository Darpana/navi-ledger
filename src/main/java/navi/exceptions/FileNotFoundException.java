package navi.exceptions;

public class FileNotFoundException extends RuntimeException{
    public FileNotFoundException(String msg){
        super(msg);
    }
}
