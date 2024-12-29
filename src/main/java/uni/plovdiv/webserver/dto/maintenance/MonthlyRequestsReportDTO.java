package uni.plovdiv.webserver.dto.maintenance;

public record MonthlyRequestsReportDTO(
        YearMonthDTO yearMonth,
        Integer requests
) {
}
