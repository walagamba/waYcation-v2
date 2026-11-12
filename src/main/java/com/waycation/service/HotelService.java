package com.waycation.service;

import com.waycation.exception.HotelAccessDeniedException;
import com.waycation.exception.HotelNotFoundException;
import com.waycation.model.Hotel;
import com.waycation.model.HotelPhoto;
import com.waycation.model.HotelStatus;
import com.waycation.model.RoomType;
import com.waycation.repository.HotelRepository;
import com.waycation.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Domain service managing hotel property listings, room types, photo galleries,
 * and owner lifecycle operations.
 */
    // Business logic service managing hotel onboarding, room types, and photos
@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;

    public HotelService(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    /**
     * Creates a new hotel listing along with its initial room types and photo gallery.
     */
    public Hotel createHotel(String ownerUid, Hotel hotelData, List<RoomType> roomTypes, List<HotelPhoto> photos) {
        if (ownerUid == null || ownerUid.isBlank()) {
            throw new IllegalArgumentException("Owner UID is required to create a hotel listing.");
        }
        if (hotelData == null || hotelData.getName() == null || hotelData.getName().isBlank()) {
            throw new IllegalArgumentException("Hotel name is required.");
        }
        if (hotelData.getCity() == null || hotelData.getCity().isBlank()) {
            throw new IllegalArgumentException("Hotel city is required.");
        }

        // Bind newly created hotel to the currently authenticated owner's UID
        hotelData.setOwnerUid(ownerUid);
        // Set hotel status to ACTIVE upon creation
        if (hotelData.getStatus() == null) {
            hotelData.setStatus(HotelStatus.ACTIVE);
        }
        if (hotelData.getCountry() == null || hotelData.getCountry().isBlank()) {
            hotelData.setCountry("Sri Lanka");
        }
        if (hotelData.getCheckInTime() == null || hotelData.getCheckInTime().isBlank()) {
            hotelData.setCheckInTime("14:00");
        }
        if (hotelData.getCheckOutTime() == null || hotelData.getCheckOutTime().isBlank()) {
            hotelData.setCheckOutTime("11:00");
        }
        if (hotelData.getFeaturedImage() == null || hotelData.getFeaturedImage().isBlank()) {
            hotelData.setFeaturedImage("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80");
        }

        if (roomTypes != null && !roomTypes.isEmpty()) {
            for (RoomType rt : roomTypes) {
                if (rt != null && rt.getName() != null && !rt.getName().isBlank()) {
                    hotelData.addRoomType(rt);
                }
            }
        }

        if (photos != null) {
            for (HotelPhoto photo : photos) {
                hotelData.addPhoto(photo);
            }
        }

        return hotelRepository.save(hotelData);
    }

    /**
     * Retrieves active hotel listings filtered optionally by city substring and minimum room capacity.
     */
    @Transactional(readOnly = true)
    public List<Hotel> getHotels(String cityFilter, Integer minCapacity) {
        List<Hotel> activeHotels;
        if (cityFilter != null && !cityFilter.isBlank()) {
            activeHotels = hotelRepository.findByStatusAndCityContainingIgnoreCaseOrderByCreatedAtDesc(
                    HotelStatus.ACTIVE, cityFilter.trim());
        } else {
            activeHotels = hotelRepository.findByStatusOrderByCreatedAtDesc(HotelStatus.ACTIVE);
        }

        if (minCapacity != null && minCapacity > 0) {
            return activeHotels.stream()
                    .filter(hotel -> hotel.getRoomTypes() == null
                            || hotel.getRoomTypes().isEmpty()
                            || hotel.getRoomTypes().stream().anyMatch(rt -> rt.getCapacity() >= minCapacity))
                    .collect(Collectors.toList());
        }

        return activeHotels;
    }

    /**
     * Retrieves a single hotel by its primary ID.
     */
    @Transactional(readOnly = true)
    public Optional<Hotel> getHotelById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return hotelRepository.findById(id);
    }

    /**
     * Retrieves all hotels owned by a given owner UID (including active and inactive properties).
     */
    @Transactional(readOnly = true)
    public List<Hotel> getHotelsForOwner(String ownerUid) {
        if (ownerUid == null || ownerUid.isBlank()) {
            return List.of();
        }
        return hotelRepository.findByOwnerUidOrderByCreatedAtDesc(ownerUid);
    }

    /**
     * Updates mutable fields of an existing hotel after verifying ownership.
     */
    public Hotel updateHotel(String callerUid, String hotelId, Hotel updatedFields) {
        return updateHotelWithRooms(callerUid, hotelId, updatedFields, null);
    }

    /**
     * Updates mutable fields of an existing hotel and appends or updates its room categories.
     */
    public Hotel updateHotelWithRooms(String callerUid, String hotelId, Hotel updatedFields, List<RoomType> newRoomTypes) {
        Hotel existing = findHotelOrThrow(hotelId);
        requireOwner(callerUid, existing, "update it");

        if (updatedFields.getName() != null && !updatedFields.getName().isBlank()) {
            existing.setName(updatedFields.getName());
        }
        if (updatedFields.getDescription() != null) {
            existing.setDescription(updatedFields.getDescription());
        }
        if (updatedFields.getAddress() != null) {
            existing.setAddress(updatedFields.getAddress());
        }
        if (updatedFields.getCity() != null && !updatedFields.getCity().isBlank()) {
            existing.setCity(updatedFields.getCity());
        }
        if (updatedFields.getCountry() != null && !updatedFields.getCountry().isBlank()) {
            existing.setCountry(updatedFields.getCountry());
        }
        if (updatedFields.getLatitude() != null) {
            existing.setLatitude(updatedFields.getLatitude());
        }
        if (updatedFields.getLongitude() != null) {
            existing.setLongitude(updatedFields.getLongitude());
        }
        if (updatedFields.getCheckInTime() != null && !updatedFields.getCheckInTime().isBlank()) {
            existing.setCheckInTime(updatedFields.getCheckInTime());
        }
        if (updatedFields.getCheckOutTime() != null && !updatedFields.getCheckOutTime().isBlank()) {
            existing.setCheckOutTime(updatedFields.getCheckOutTime());
        }
        if (updatedFields.getCancellationPolicy() != null) {
            existing.setCancellationPolicy(updatedFields.getCancellationPolicy());
        }
        if (updatedFields.getFeaturedImage() != null && !updatedFields.getFeaturedImage().isBlank()) {
            existing.setFeaturedImage(updatedFields.getFeaturedImage());
        }
        if (updatedFields.getStatus() != null) {
            existing.setStatus(updatedFields.getStatus());
        }

        if (newRoomTypes != null && !newRoomTypes.isEmpty()) {
            for (RoomType rt : newRoomTypes) {
                if (rt != null && rt.getName() != null && !rt.getName().isBlank()) {
                    existing.addRoomType(rt);
                }
            }
        }

        existing.setUpdatedAt(Instant.now());
        return hotelRepository.save(existing);
    }

    /**
     * Deletes a room type from a hotel after ownership verification.
     */
    public void deleteRoomType(String callerUid, String hotelId, String roomTypeId) {
        Hotel existing = findHotelOrThrow(hotelId);
        requireOwner(callerUid, existing, "remove its room categories");

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new HotelNotFoundException("Room category", roomTypeId));

        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new IllegalArgumentException("Room category does not belong to this property.");
        }

        existing.getRoomTypes().remove(roomType);
        roomTypeRepository.delete(roomType);
    }

    /**
     * Soft-deactivates a hotel by setting its status to INACTIVE, preserving historical booking references.
     */
    public Hotel deactivateHotel(String callerUid, String hotelId) {
        Hotel existing = findHotelOrThrow(hotelId);
        requireOwner(callerUid, existing);

        existing.setStatus(HotelStatus.INACTIVE);
        existing.setUpdatedAt(Instant.now());
        return hotelRepository.save(existing);
    }

    // ---------- ownership checks (used by update, delete and deactivate) ----------

    /**
     * Loads a hotel or throws HotelNotFoundException.
     */
    private Hotel findHotelOrThrow(String hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
    }

    /**
     * METHOD OVERLOADING (1 of 2): checks the caller owns the hotel, with a default action text.
     */
    private void requireOwner(String callerUid, Hotel hotel) {
        requireOwner(callerUid, hotel, "change it");
    }

    /**
     * METHOD OVERLOADING (2 of 2): same name, extra parameter for the message.
     * Refuses when the caller is missing (not logged in) or is not the hotel's owner.
     */
    private void requireOwner(String callerUid, Hotel hotel, String action) {
        if (callerUid == null || callerUid.isBlank() || !callerUid.equals(hotel.getOwnerUid())) {
            throw new HotelAccessDeniedException(action, hotel.getId());
        }
    }

}
