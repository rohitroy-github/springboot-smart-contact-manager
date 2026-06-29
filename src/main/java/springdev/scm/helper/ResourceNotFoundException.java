package springdev.scm.helper;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){

        super(message);

    }

    public ResourceNotFoundException() { 

        super("Requested resource was not found. Please have a look carefully.");

    }

}
