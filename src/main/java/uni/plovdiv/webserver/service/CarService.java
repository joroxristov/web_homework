package uni.plovdiv.webserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.plovdiv.webserver.dto.car.CreateCarDTO;
import uni.plovdiv.webserver.dto.car.ResponseCarDTO;
import uni.plovdiv.webserver.dto.car.UpdateCarDTO;
import uni.plovdiv.webserver.dto.garage.ResponseGarageDTO;
import uni.plovdiv.webserver.exception.CarDoesNotExistException;
import uni.plovdiv.webserver.exception.GarageDoesNotExistException;
import uni.plovdiv.webserver.model.Car;
import uni.plovdiv.webserver.model.Garage;
import uni.plovdiv.webserver.repository.CarRepository;
import uni.plovdiv.webserver.repository.GarageRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<ResponseCarDTO> findAll(String carMake, Integer garageId, Integer fromYear, Integer toYear) {
        return carRepository.findByMakeAndGaragesIdAndProductionYear(carMake, garageId, fromYear, toYear).stream()
                .map(this::mapToResponseCarDTO)
                .toList();
    }

    public ResponseCarDTO findById(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarDoesNotExistException(id));
        return mapToResponseCarDTO(car);
    }

    @Transactional
    public ResponseCarDTO createCar(CreateCarDTO dto) {
        Set<Garage> garages = findGarages(dto.garageIds());
        Car car = new Car();
        car.setMake(dto.make());
        car.setModel(dto.model());
        car.setProductionYear(dto.productionYear());
        car.setLicensePlate(dto.licensePlate());
        car.setGarages(garages);
        carRepository.save(car);
        return mapToResponseCarDTO(car);
    }

    @Transactional
    public ResponseCarDTO updateCar(Integer id, UpdateCarDTO dto) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarDoesNotExistException(id));

        if (dto.make() != null) {
            car.setMake(dto.make());
        }

        if (dto.model() != null) {
            car.setModel(dto.model());
        }

        if (dto.productionYear() != null) {
            car.setProductionYear(dto.productionYear());
        }

        if (dto.licensePlate() != null) {
            car.setLicensePlate(dto.licensePlate());
        }

        if (dto.garageIds() != null) {
            car.setGarages(findGarages(dto.garageIds()));
        }

        return mapToResponseCarDTO(car);
    }

    private Set<Garage> findGarages(List<Integer> garageIds) {
        List<Garage> garages = garageRepository.findAllById(garageIds);

        Set<Integer> existingGarageIds = garages.stream()
                .map(Garage::getId)
                .collect(Collectors.toSet());
        List<Integer> missingGarageIds = garageIds.stream()
                .filter(garageId -> !existingGarageIds.contains(garageId))
                .toList();

        if (!missingGarageIds.isEmpty()) {
            throw new GarageDoesNotExistException(missingGarageIds);
        }

        return new HashSet<>(garages);
    }

    @Transactional
    public boolean deleteCar(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarDoesNotExistException(id));
        try {
            car.getGarages().forEach(garage -> garage.getCars().remove(car));
            carRepository.delete(car);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private ResponseCarDTO mapToResponseCarDTO(Car car) {
        return new ResponseCarDTO(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getProductionYear(),
                car.getLicensePlate(),
                car.getGarages()
                        .stream()
                        .map(garage -> new ResponseGarageDTO(
                                garage.getId(),
                                garage.getName(),
                                garage.getLocation(),
                                garage.getCity(),
                                garage.getCapacity()))
                        .toList()
        );
    }
}
