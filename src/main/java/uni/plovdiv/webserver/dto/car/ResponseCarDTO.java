package uni.plovdiv.webserver.dto.car;

import uni.plovdiv.webserver.dto.garage.ResponseGarageDTO;

import java.util.List;

public record ResponseCarDTO(
        Integer id,
        String make,
        String model,
        Integer productionYear,
        String licensePlate,
        List<ResponseGarageDTO> garages
) {
}
