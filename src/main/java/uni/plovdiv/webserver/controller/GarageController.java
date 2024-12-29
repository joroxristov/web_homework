package uni.plovdiv.webserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uni.plovdiv.webserver.dto.garage.CreateGarageDTO;
import uni.plovdiv.webserver.dto.garage.DailyAvailabilityReportDTO;
import uni.plovdiv.webserver.dto.garage.ResponseGarageDTO;
import uni.plovdiv.webserver.dto.garage.UpdateGarageDTO;
import uni.plovdiv.webserver.service.GarageService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @GetMapping
    public List<ResponseGarageDTO> findAll(@RequestParam(required = false) String city) {
        return garageService.findAll(city);
    }

    @GetMapping("/{id}")
    public ResponseGarageDTO findById(@PathVariable Integer id) {
        return garageService.findById(id);
    }

    @GetMapping("/dailyAvailabilityReport")
    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(
            @RequestParam Integer garageId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return garageService.getDailyAvailabilityReport(garageId, startDate, endDate);
    }

    @PostMapping
    public ResponseGarageDTO createGarage(@RequestBody CreateGarageDTO createGarageDTO) {
        return garageService.createGarage(createGarageDTO);
    }

    @PutMapping("/{id}")
    public ResponseGarageDTO updateGarage(@PathVariable Integer id, @RequestBody UpdateGarageDTO updateGarageDTO) {
        return garageService.updateGarage(id, updateGarageDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteGarage(@PathVariable Integer id) {
        return garageService.deleteGarage(id);
    }
}
