package uni.plovdiv.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uni.plovdiv.webserver.model.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("""
            SELECT c FROM Car c LEFT JOIN c.garages g
            WHERE (:make IS NULL OR c.make = :make)
            AND (:garageId IS NULL OR g.id = :garageId)
            AND (:fromYear IS NULL OR c.productionYear >= :fromYear)
            AND (:toYear IS NULL OR c.productionYear <= :toYear)
            """)
    List<Car> findByMakeAndGaragesIdAndProductionYear(String make, Integer garageId, Integer fromYear, Integer toYear);
}
