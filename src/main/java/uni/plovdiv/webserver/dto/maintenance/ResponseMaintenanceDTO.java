package uni.plovdiv.webserver.dto.maintenance;

import java.time.LocalDate;

public record ResponseMaintenanceDTO(
        Integer id,
        Integer carId,
        String carName,
        String serviceType,
        LocalDate scheduledDate,
        Integer garageId,
        String garageName
) {
}
