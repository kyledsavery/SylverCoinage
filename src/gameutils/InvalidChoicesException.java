package gameutils;

/**
 * Class [InvalidChoicesException] Exception that is raised when 
 * initial choices do not follow the game's rules
 * 
 * @author Kyle Savery
 */
public class InvalidChoicesException extends Exception{
    
    public InvalidChoicesException(String errMsg){
        super(errMsg);
    }
}
