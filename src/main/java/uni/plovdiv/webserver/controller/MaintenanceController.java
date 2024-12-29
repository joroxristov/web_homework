package uni.plovdiv.webserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import uni.plovdiv.webserver.dto.maintenance.CreateMaintenanceDTO;
import uni.plovdiv.webserver.dto.maintenance.MonthlyRequestsReportDTO;
import uni.plovdiv.webserver.dto.maintenance.ResponseMaintenanceDTO;
import uni.plovdiv.webserver.dto.maintenance.UpdateMaintenanceDTO;
import uni.plovdiv.webserver.service.MaintenanceService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/{id}")
    public ResponseMaintenanceDTO findById(@PathVariable Integer id) {
        return maintenanceService.findById(id);
    }

    @GetMapping
    public List<ResponseMaintenanceDTO> findAll(
            @RequestParam(required = false) Integer carId,
            @RequestParam(required = false) Integer garageId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return maintenanceService.findAll(carId, garageId, startDate, endDate);
    }

    @GetMapping("/monthlyRequestsReport")
    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(
            @RequestParam Integer garageId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth) {
        return maintenanceService.getMonthlyRequestsReport(garageId, startMonth, endMonth);
    }

    @PostMapping
    public ResponseMaintenanceDTO createMaintenance(@RequestBody CreateMaintenanceDTO createMaintenanceDTO) {
        return maintenanceService.createMaintenance(createMaintenanceDTO);
    }

    @PutMapping("/{id}")
    public ResponseMaintenanceDTO updateMaintenance(@PathVariable Integer id, @RequestBody UpdateMaintenanceDTO updateMaintenanceDTO) {
        return maintenanceService.updateMaintenance(id, updateMaintenanceDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteMaintenance(@PathVariable Integer id) {
        return maintenanceService.deleteMaintenance(id);
    }
}
