package com.waycation.repository;

import com.waycation.model.RoomInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RoomInventory} entities.
 */
@Repository
public interface RoomInventoryRepository extends JpaRepository<RoomInventory, String> {

    Optional<RoomInventory> findByRoomTypeIdAndDate(String roomTypeId, LocalDate date);
}
