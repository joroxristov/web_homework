package uni.plovdiv.webserver.exception;

public class CarDoesNotExistException extends ResourceDoesNotExistException {

    public CarDoesNotExistException(Integer id) {
        super("Missing car with ID - %s".formatted(id));
    }
}
