package com.waycation.repository;

import com.waycation.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link RoomType} entities.
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, String> {

    List<RoomType> findByHotelId(String hotelId);
}
