package uni.plovdiv.webserver.exception;

import java.util.List;

public class GarageDoesNotExistException extends ResourceDoesNotExistException {

    public GarageDoesNotExistException(Integer id) {
        super("Missing garage with ID - %s".formatted(id));
    }

    public GarageDoesNotExistException(List<Integer> ids) {
        super("Missing garages with IDs - %s".formatted(ids));
    }
}
