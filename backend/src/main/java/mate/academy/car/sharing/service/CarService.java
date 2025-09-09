package mate.academy.car.sharing.service;

import java.util.List;


import mate.academy.car.sharing.dto.request.CarRequestDto;
import mate.academy.car.sharing.dto.response.CarResponseDto;
import mate.academy.car.sharing.entity.Car;

public interface CarService extends AbstractService<Car> {
    Car increaseInventory(Car car);
    Car decreaseInventory(Car car);
    CarResponseDto save(CarRequestDto carRequestDto);
    Car save(Car car); // add this line
    List<Car> getAll();
    Car add(Car car);
    Car getById(Long id);
    void delete(Long id);
    Car update(Car car);
}
