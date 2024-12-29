package uni.plovdiv.webserver.dto.garage;

public record ResponseGarageDTO(
        Integer id,
        String name,
        String location,
        String city,
        Integer capacity
) {
}
