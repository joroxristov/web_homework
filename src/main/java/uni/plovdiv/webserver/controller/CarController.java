package uni.plovdiv.webserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uni.plovdiv.webserver.dto.car.CreateCarDTO;
import uni.plovdiv.webserver.dto.car.ResponseCarDTO;
import uni.plovdiv.webserver.dto.car.UpdateCarDTO;
import uni.plovdiv.webserver.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public List<ResponseCarDTO> findAll(@RequestParam(required = false) String carMake, @RequestParam(required = false) Integer garageId,
                                        @RequestParam(required = false) Integer fromYear, @RequestParam(required = false) Integer toYear
    ) {
        return carService.findAll(carMake, garageId, fromYear, toYear);
    }

    @GetMapping("/{id}")
    public ResponseCarDTO findById(@PathVariable Integer id) {
        return carService.findById(id);
    }

    @PostMapping
    public ResponseCarDTO createCar(@RequestBody CreateCarDTO createCarDTO) {
        return carService.createCar(createCarDTO);
    }

    @PutMapping("/{id}")
    public ResponseCarDTO updateCar(@PathVariable Integer id, @RequestBody UpdateCarDTO updateCarDTO) {
        return carService.updateCar(id, updateCarDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteCar(@PathVariable Integer id) {
        return carService.deleteCar(id);
    }
}
