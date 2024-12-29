package uni.plovdiv.webserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uni.plovdiv.webserver.model.Garage;

import java.util.List;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Integer> {

    @Query("""
            SELECT g FROM Garage g
            WHERE (:city IS NULL OR g.city = :city)
            """)
    List<Garage> findByCity(String city);
}
