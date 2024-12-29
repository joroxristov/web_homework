package uni.plovdiv.webserver.dto.garage;

public record UpdateGarageDTO(
        String name,
        String location,
        String city,
        Integer capacity
) {
}
