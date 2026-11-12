package com.waycation.actions;

import com.waycation.model.Hotel;
import com.waycation.model.HotelPhoto;
import com.waycation.model.RoomType;
import com.waycation.service.HotelService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Module 2: Hotel Listing Management Actions
 * Provides server action entry points for creating hotel properties, updating details,
 * soft-deactivating listings, and querying owner accommodation portfolios.
 */
@Component
    // Facade Design Pattern: provides high-level actions for hotel management
public class HotelAction {

    private final HotelService hotelService;

    public HotelAction(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public Hotel createHotelAction(
            String ownerUid,
            Hotel hotelData,
            List<RoomType> roomTypes,
            List<HotelPhoto> photos
    ) {
        return hotelService.createHotel(ownerUid, hotelData, roomTypes, photos);
    }

    public Hotel updateHotelAction(String ownerUid, String hotelId, Hotel updatedData) {
        return hotelService.updateHotel(ownerUid, hotelId, updatedData);
    }

    public Hotel deactivateHotelAction(String ownerUid, String hotelId) {
        return hotelService.deactivateHotel(ownerUid, hotelId);
    }

    public List<Hotel> getOwnerHotelsAction(String ownerUid) {
        return hotelService.getHotelsForOwner(ownerUid);
    }

    public List<Hotel> searchHotelsAction(String city, Integer minCapacity) {
        return hotelService.getHotels(city, minCapacity);
    }
}

