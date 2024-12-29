package uni.plovdiv.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uni.plovdiv.webserver.model.Maintenance;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    List<Maintenance> findByGarageId(Integer garageId);

    List<Maintenance> findByGarageIdAndScheduledDate(Integer garageId, LocalDate scheduledDate);

    List<Maintenance> findByGarageIdAndScheduledDateBetween(Integer garageId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT m FROM Maintenance m
            WHERE (:garageId IS NULL OR m.garage.id = :garageId)
            AND (:carId IS NULL OR m.car.id = :carId)
            """)
    List<Maintenance> findByGarageIdAndCarId(Integer garageId, Integer carId);
}
