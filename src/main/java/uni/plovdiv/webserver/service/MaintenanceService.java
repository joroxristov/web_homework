package uni.plovdiv.webserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.plovdiv.webserver.dto.maintenance.*;
import uni.plovdiv.webserver.exception.CarDoesNotExistException;
import uni.plovdiv.webserver.exception.GarageDoesNotExistException;
import uni.plovdiv.webserver.exception.MaintenanceDoesNotExistException;
import uni.plovdiv.webserver.exception.RequestValidationException;
import uni.plovdiv.webserver.model.Car;
import uni.plovdiv.webserver.model.Garage;
import uni.plovdiv.webserver.model.Maintenance;
import uni.plovdiv.webserver.repository.CarRepository;
import uni.plovdiv.webserver.repository.GarageRepository;
import uni.plovdiv.webserver.repository.MaintenanceRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<ResponseMaintenanceDTO> findAll(Integer carId, Integer garageId, LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findByGarageIdAndCarId(garageId, carId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startDate, endDate))
                .map(this::mapToResponseMaintenanceDTO)
                .toList();
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, LocalDate startDate, LocalDate endDate) {
        boolean isAfterStartDate = true;
        if (startDate != null) {
            isAfterStartDate = scheduledDate.isAfter(startDate);
        }
        boolean isBeforeEndDate = true;
        if (endDate != null) {
            isBeforeEndDate = scheduledDate.isBefore(endDate);
        }
        return isAfterStartDate && isBeforeEndDate;
    }

    public ResponseMaintenanceDTO findById(Integer id) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceDoesNotExistException(id));
        return mapToResponseMaintenanceDTO(maintenance);
    }

    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(Integer garageId, YearMonth startMonth, YearMonth endMonth) {
        if (!garageRepository.existsById(garageId)) {
            throw new RequestValidationException("garage with ID %s does not exist".formatted(garageId));
        }
        Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate = maintenanceRepository.findByGarageId(garageId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startMonth, endMonth))
                .collect(groupingBy(maintenance -> YearMonth.from(maintenance.getScheduledDate())));
        return produceMonthlyRequestsReport(maintenancesGroupedByScheduledDate, startMonth, endMonth);
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, YearMonth startMonth, YearMonth endMonth) {
        YearMonth scheduledDateInYearMonthFormat = YearMonth.from(scheduledDate);
        return (startMonth.isBefore(scheduledDateInYearMonthFormat) || startMonth.equals(scheduledDateInYearMonthFormat)) &&
               (endMonth.isAfter(scheduledDateInYearMonthFormat) || endMonth.equals(scheduledDateInYearMonthFormat));
    }

    private List<MonthlyRequestsReportDTO> produceMonthlyRequestsReport(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth startMonth, YearMonth endMonth) {
        List<MonthlyRequestsReportDTO> result = new ArrayList<>();
        YearMonth currentMonth = startMonth;
        while (currentMonth.isBefore(endMonth) || currentMonth.equals(endMonth)) {
            if (maintenanceForGivenYearMonthExist(maintenancesGroupedByScheduledDate, currentMonth)) {
                result.add(createReportWithRequests(maintenancesGroupedByScheduledDate, currentMonth));
            } else {
                result.add(createReportWithoutRequests(currentMonth));
            }
            currentMonth = currentMonth.plusMonths(1);
        }
        return result;
    }

    private boolean maintenanceForGivenYearMonthExist(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth yearMonth) {
        return maintenancesGroupedByScheduledDate.containsKey(yearMonth);
    }

    private MonthlyRequestsReportDTO createReportWithRequests(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth currentMonth) {
        List<Maintenance> maintenancesForCurrentMonth = maintenancesGroupedByScheduledDate.get(currentMonth);
        Integer numberOfRequests = maintenancesForCurrentMonth.size();
        YearMonthDTO yearMonthDTO = mapToYearMonthDTO(currentMonth);
        return new MonthlyRequestsReportDTO(yearMonthDTO, numberOfRequests);
    }

    private MonthlyRequestsReportDTO createReportWithoutRequests(YearMonth currentMonth) {
        YearMonthDTO yearMonthDTO = mapToYearMonthDTO(currentMonth);
        int numberOfRequests = 0;
        return new MonthlyRequestsReportDTO(yearMonthDTO, numberOfRequests);
    }

    private YearMonthDTO mapToYearMonthDTO(YearMonth yearMonth) {
        Integer year = yearMonth.getYear();
        String month = yearMonth.getMonth().toString();
        Boolean isLeapYear = yearMonth.isLeapYear();
        Integer monthValue = yearMonth.getMonthValue();
        return new YearMonthDTO(year, month, isLeapYear, monthValue);
    }

    @Transactional
    public ResponseMaintenanceDTO createMaintenance(CreateMaintenanceDTO dto) {
        Garage garage = garageRepository.findById(dto.garageId()).orElseThrow(IllegalArgumentException::new);
        Car car = carRepository.findById(dto.carId()).orElseThrow(IllegalArgumentException::new);

        verifyGarageHasEnoughCapacity(garage, dto.scheduledDate());
        verifyCarIsRegisteredInSpecificGarage(car, garage);

        Maintenance maintenance = new Maintenance();
        maintenance.setCar(car);
        maintenance.setGarage(garage);
        maintenance.setServiceType(dto.serviceType());
        maintenance.setScheduledDate(dto.scheduledDate());

        maintenanceRepository.save(maintenance);
        return mapToResponseMaintenanceDTO(maintenance);
    }

    @Transactional
    public ResponseMaintenanceDTO updateMaintenance(Integer id, UpdateMaintenanceDTO dto) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceDoesNotExistException(id));

        Garage garage = dto.garageId() != null ? garageRepository.findById(dto.garageId()).orElseThrow(() -> new GarageDoesNotExistException(dto.garageId())) : maintenance.getGarage();
        Car car = dto.carId() != null ? carRepository.findById(dto.carId()).orElseThrow(() -> new CarDoesNotExistException(dto.carId())) : maintenance.getCar();
        LocalDate scheduledDate = dto.scheduledDate() != null ? dto.scheduledDate() : maintenance.getScheduledDate();
        String serviceType = dto.serviceType() != null ? dto.serviceType() : maintenance.getServiceType();

        verifyGarageHasEnoughCapacity(garage, scheduledDate);
        verifyCarIsRegisteredInSpecificGarage(car, garage);

        updateMaintenanceEntity(maintenance, garage, car, scheduledDate, serviceType);
        return mapToResponseMaintenanceDTO(maintenance);
    }

    private void updateMaintenanceEntity(Maintenance maintenance, Garage garage, Car car, LocalDate scheduledDate, String serviceType) {
        maintenance.setGarage(garage);
        maintenance.setCar(car);
        maintenance.setScheduledDate(scheduledDate);
        maintenance.setServiceType(serviceType);
    }

    private void verifyGarageHasEnoughCapacity(Garage garage, LocalDate scheduledDate) {
        List<Maintenance> maintenances = maintenanceRepository.findByGarageIdAndScheduledDate(garage.getId(), scheduledDate);
        if (garage.getCapacity() <= maintenances.size()) {
            throw new RequestValidationException("garage with ID %s does not have capacity for date %s".formatted(garage.getId(), scheduledDate));
        }
    }

    private void verifyCarIsRegisteredInSpecificGarage(Car car, Garage garage) {
        if (!garage.getCars().contains(car)) {
            throw new RequestValidationException("car with ID %s it not registered in the garage with ID %s".formatted(car.getId(), garage.getId()));
        }
    }

    @Transactional
    public boolean deleteMaintenance(Integer id) {
        try {
            Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceDoesNotExistException(id));
            maintenance.setGarage(null);
            maintenance.setCar(null);
            maintenanceRepository.delete(maintenance);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private ResponseMaintenanceDTO mapToResponseMaintenanceDTO(Maintenance maintenance) {
        return new ResponseMaintenanceDTO(maintenance.getId(),
                maintenance.getCar().getId(),
                constructCarName(maintenance.getCar()),
                maintenance.getServiceType(),
                maintenance.getScheduledDate(),
                maintenance.getGarage().getId(),
                maintenance.getGarage().getName());
    }

    private String constructCarName(Car car) {
        return car.getMake() + car.getModel() + car.getLicensePlate();
    }
}
