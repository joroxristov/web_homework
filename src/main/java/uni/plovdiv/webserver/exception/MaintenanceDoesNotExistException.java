package uni.plovdiv.webserver.exception;

public class MaintenanceDoesNotExistException extends ResourceDoesNotExistException {

    public MaintenanceDoesNotExistException(Integer id) {
        super("Missing maintenance with ID - %s".formatted(id));
    }
}
