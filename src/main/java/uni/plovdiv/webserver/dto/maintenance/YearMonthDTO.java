package uni.plovdiv.webserver.dto.maintenance;

public record YearMonthDTO(
        Integer year,
        String month,
        Boolean leapYear,
        Integer monthValue
) {
}
