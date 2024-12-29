package uni.plovdiv.webserver.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.plovdiv.webserver.dto.garage.CreateGarageDTO;
import uni.plovdiv.webserver.dto.garage.DailyAvailabilityReportDTO;
import uni.plovdiv.webserver.dto.garage.ResponseGarageDTO;
import uni.plovdiv.webserver.dto.garage.UpdateGarageDTO;
import uni.plovdiv.webserver.exception.GarageDoesNotExistException;
import uni.plovdiv.webserver.model.Garage;
import uni.plovdiv.webserver.model.Maintenance;
import uni.plovdiv.webserver.repository.GarageRepository;
import uni.plovdiv.webserver.repository.MaintenanceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final GarageRepository garageRepository;

    private final MaintenanceRepository maintenanceRepository;

    public List<ResponseGarageDTO> findAll(@Nullable String city) {
        List<Garage> garages = garageRepository.findByCity(city);
        return garages.stream()
                .map(garage -> new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity()))
                .toList();
    }

    public ResponseGarageDTO findById(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageDoesNotExistException(id));
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(Integer garageId, LocalDate startDate, LocalDate endDate) {
        Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageDoesNotExistException(garageId));
        Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate = maintenanceRepository.findByGarageIdAndScheduledDateBetween(garageId, startDate, endDate).stream()
                .collect(Collectors.groupingBy(Maintenance::getScheduledDate));
        return getDailyAvailabilityReport(maintenancesGroupedByScheduledDate, startDate, endDate, garage);
    }

    private List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate, LocalDate startDate, LocalDate endDate, Garage garage) {
        List<DailyAvailabilityReportDTO> dailyAvailabilityReports = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (maintenancesGroupedByScheduledDate.containsKey(currentDate)) {
                int maintenanceRequests = maintenancesGroupedByScheduledDate.get(currentDate).size();
                int availableCapacity = garage.getCapacity();
                dailyAvailabilityReports.add(new DailyAvailabilityReportDTO(currentDate, maintenanceRequests, availableCapacity));
            } else {
                dailyAvailabilityReports.add(new DailyAvailabilityReportDTO(currentDate, 0, garage.getCapacity()));
            }
            currentDate = currentDate.plusDays(1);
        }
        return dailyAvailabilityReports;
    }

    @Transactional
    public ResponseGarageDTO createGarage(CreateGarageDTO dto) {
        Garage garage = new Garage();
        garage.setName(dto.name());
        garage.setLocation(dto.location());
        garage.setCity(dto.city());
        garage.setCapacity(dto.capacity());
        garageRepository.save(garage);
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public ResponseGarageDTO updateGarage(Integer id, UpdateGarageDTO dto) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageDoesNotExistException(id));

        if (dto.name() != null) {
            garage.setName(dto.name());
        }

        if (dto.location() != null) {
            garage.setLocation(dto.location());
        }

        if (dto.city() != null) {
            garage.setCity(dto.city());
        }

        if (dto.capacity() != null) {
            garage.setCapacity(dto.capacity());
        }

        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public boolean deleteGarage(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageDoesNotExistException(id));
        try {
            garage.getCars().forEach(car -> car.getGarages().remove(garage));
            garageRepository.delete(garage);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
