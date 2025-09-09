package mate.academy.car.sharing.controller;

import mate.academy.car.sharing.dto.request.CarRequestDto;
import mate.academy.car.sharing.dto.response.CarResponseDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import mate.academy.car.sharing.entity.Car;
import mate.academy.car.sharing.mapper.CarMapper;
import mate.academy.car.sharing.service.CarService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {
    private static final Long CAR_ID = 1L;

    @Mock
    private CarService carService;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarController carController;

    private CarRequestDto carRequestDto;
    private CarResponseDto carResponseDto;
    private Car car;

    @BeforeEach
    void setUp() {
        carRequestDto = new CarRequestDto();
        carRequestDto.setBrand("Peugeot");
        carRequestDto.setModel("107");
        carRequestDto.setType(String.valueOf(Car.CarType.HATCHBACK));
        carRequestDto.setInventory(4);
        carRequestDto.setDailyFee(BigDecimal.TEN);
    
        carResponseDto = new CarResponseDto();
        carResponseDto.setId(CAR_ID);
        carResponseDto.setBrand(carRequestDto.getBrand());
        carResponseDto.setModel(carRequestDto.getModel());
        carResponseDto.setType(carRequestDto.getType());
        carResponseDto.setInventory(carRequestDto.getInventory());
        carResponseDto.setDailyFee(carRequestDto.getDailyFee());
    
        car = new Car("Model S", "Tesla", Car.CarType.SEDAN, new BigDecimal("100.00"), 5, "images/tesla_model_s.jpg");
        car.setId(CAR_ID);  // Ensure the ID is set for proper comparison in the test
    }

    @Test
    void add_validCarRequestDto_ok() {
        // Arrange (stubbing)
        given(carMapper.toEntity(any(CarRequestDto.class))).willReturn(car);  // Mock mapping DTO to entity
        given(carService.add(any(Car.class))).willReturn(car);  // Mock the service call
        given(carMapper.toDto(any(Car.class))).willReturn(carResponseDto);  // Mock mapping entity to response DTO

        // Act (perform the actual call)
        CarResponseDto result = carController.add(carRequestDto);

        // Assert
        Assertions.assertEquals(carResponseDto, result);  // Assert the expected result

        // Verify interactions
        then(carMapper).should().toEntity(carRequestDto);  // Verify that the mapping occurred
        then(carService).should().add(car);  // Verify service method call
        then(carMapper).should().toDto(car);  // Verify DTO conversion
    }

@Test
void getAll_notEmptyDB_ok() {
    // Arrange: Mock the service to return a list of Car entities
    given(carService.getAll()).willReturn(Collections.singletonList(car));
    // Mock the mapper to convert Car entities to CarResponseDtos
    given(carMapper.toDto(any(Car.class))).willReturn(carResponseDto);

    // Act: Call the controller method
    ResponseEntity<?> responseEntity = carController.getAllCars();
    
    // Extract the body from the ResponseEntity and convert to List<CarResponseDto>
    List<CarResponseDto> actual = (List<CarResponseDto>) responseEntity.getBody();

    // Assert: Verify the service was called
    then(carService).should().getAll();

    // Expected result
    List<CarResponseDto> expected = Collections.singletonList(carResponseDto);
    Assertions.assertEquals(expected, actual);  // Compare the expected and actual results
}


@Test
void getAll_emptyDb_ok() {
    // Arrange: Mock the service to return an empty list of Car entities
    given(carService.getAll()).willReturn(Collections.emptyList());
    // Mock the mapper to convert Car entities to CarResponseDtos (no cars to convert in this case)
    given(carMapper.toDto(any(Car.class))).willReturn(carResponseDto);

    // Act: Call the controller method
    ResponseEntity<?> responseEntity = carController.getAllCars();

    // Extract the body from the ResponseEntity and cast to List<CarResponseDto>
    List<CarResponseDto> actual = (List<CarResponseDto>) responseEntity.getBody();

    // Assert: Verify the service was called
    then(carService).should().getAll();

    // Expected result: An empty list of CarResponseDto
    List<CarResponseDto> expected = Collections.emptyList();
    Assertions.assertEquals(expected, actual);  // Compare the expected and actual results
}


    @Test
    void updateCar_validId_ok() {
        // Arrange
        given(carMapper.toEntity(any(CarRequestDto.class))).willReturn(car);  // Mock mapping
        given(carService.update(car)).willReturn(car);  // Mock update service call
        given(carMapper.toDto(any(Car.class))).willReturn(carResponseDto);  // Mock DTO conversion

        // Act
        CarResponseDto actual = carController.updateCar(CAR_ID, carRequestDto);

        // Assert
        Assertions.assertEquals(carResponseDto, actual);  // Assert DTO comparison

        // Verify interactions
        then(carMapper).should().toEntity(carRequestDto);  // Check the mapping occurred
        then(carService).should().update(car);  // Ensure the service method was called
        then(carMapper).should().toDto(car);  // Verify the DTO conversion
    }

    @Test
    void delete_validId_ok() {
        // Arrange
        Mockito.doNothing().when(carService).delete(CAR_ID);  // Mock deletion

        // Act: Calling the controller to delete a car
        carController.delete(CAR_ID);

        // Assert: Ensure the delete method was called on the service
        Mockito.verify(carService).delete(CAR_ID);
    }
}
