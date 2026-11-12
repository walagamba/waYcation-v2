package com.waycation.repository;

import com.waycation.model.Hotel;
import com.waycation.model.HotelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Hotel} entities.
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {

    List<Hotel> findByStatusOrderByCreatedAtDesc(HotelStatus status);

    List<Hotel> findByStatusAndCityContainingIgnoreCaseOrderByCreatedAtDesc(HotelStatus status, String city);

    List<Hotel> findByOwnerUidOrderByCreatedAtDesc(String ownerUid);
}
