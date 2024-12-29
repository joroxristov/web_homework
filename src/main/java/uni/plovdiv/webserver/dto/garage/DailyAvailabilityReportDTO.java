package uni.plovdiv.webserver.dto.garage;

import java.time.LocalDate;

public record DailyAvailabilityReportDTO(
        LocalDate date,
        Integer requests,
        Integer availableCapacity
) {
}
