package mate.academy.car.sharing.repository;
import java.util.List;
import mate.academy.car.sharing.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    //void deleteByUserId(Long userId);              // If cars are owned by users
   
}
