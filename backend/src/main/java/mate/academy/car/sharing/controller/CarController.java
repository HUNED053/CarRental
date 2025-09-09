package mate.academy.car.sharing.controller;

//import java.io.File;
//import java.math.BigDecimal;
//import java.io.IOException;
import org.springframework.http.HttpStatus;
//import mate.academy.car.sharing.repository.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.dto.request.CarRequestDto;
import mate.academy.car.sharing.dto.response.CarResponseDto;
import mate.academy.car.sharing.entity.Car;
import mate.academy.car.sharing.mapper.CarMapper;
import mate.academy.car.sharing.service.CarService;
import mate.academy.car.sharing.service.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.validation.BindingResult;
//import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
//import java.util.stream.Collectors;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;
    private final CarMapper carMapper;
    //private final CarRepository carRepository;
    private final ImageUploadService imageUploadService;

    @Operation(summary = "Add car", description = "Add a new car with image upload")
    @PostMapping
    public ResponseEntity<CarResponseDto> addCar(
            @RequestPart("car") CarRequestDto carDto,
            @RequestPart("image") MultipartFile imageFile) {

        String imageUrl = imageUploadService.uploadImage(imageFile);
        carDto.setCarImage(imageUrl);

        CarResponseDto savedCar = carService.save(carDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
    }



    @PostMapping("/json")
    public CarResponseDto add(@Valid @RequestBody CarRequestDto carRequestDto) {
        Car car = carMapper.toEntity(carRequestDto);
        Car savedCar = carService.add(car);
        return carMapper.toDto(savedCar);
    }
    
    


    @Operation(summary = "Get all car", description = "List of all car")
    @GetMapping("/")
public ResponseEntity<?> getAllCars() {
    try {
        List<Car> cars = carService.getAll();
        return ResponseEntity.ok(cars); // just raw entity
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred: " + e.getMessage());
    }
}


    @Operation(summary = "Get car by id", description = "Get car by id")
    @GetMapping("/{id}")
    public CarResponseDto get(@PathVariable Long id) {
        return carMapper.toDto(carService.getById(id));
    }


    @Operation(summary = "Delete car by id", description = "Delete car by id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }

    @Operation(summary = "Update car by id", description = "Update car by id")
    @PutMapping("/{id}")
    public CarResponseDto updateCar(@PathVariable Long id,
                                    @Valid @RequestBody CarRequestDto dto) {
        Car carFromDto = carMapper.toEntity(dto);
        carFromDto.setId(id);
        return carMapper.toDto(carService.update(carFromDto));
    }
}
