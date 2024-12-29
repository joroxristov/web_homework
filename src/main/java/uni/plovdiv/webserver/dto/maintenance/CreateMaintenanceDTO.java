package uni.plovdiv.webserver.dto.maintenance;

import java.time.LocalDate;

public record CreateMaintenanceDTO(
        Integer carId,
        String serviceType,
        LocalDate scheduledDate,
        Integer garageId
) {
}
