package uni.plovdiv.webserver.dto.garage;

public record CreateGarageDTO(
        String name,
        String location,
        String city,
        Integer capacity
) {
}
