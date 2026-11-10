package com.waycation.controller;

import com.waycation.model.Hotel;
import com.waycation.model.HotelPhoto;
import com.waycation.model.RoomType;
import com.waycation.service.HotelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring MVC controller handling public hotel discovery, detail pages,
 * and the Hotel Owner property management console.
 */
@Controller
public class HotelController {

    private static final String DEFAULT_OWNER_UID = "owner-demo-uid";

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    /**
     * Screen 01: Public Discovery Landing Page with featured Sri Lankan hotels.
     */
    @GetMapping("/")
    public String landingPage(Model model) {
        List<Hotel> allHotels = hotelService.getHotels(null, null);
        List<Hotel> featuredHotels = allHotels.size() > 6 ? allHotels.subList(0, 6) : allHotels;
        model.addAttribute("featuredHotels", featuredHotels);
        model.addAttribute("totalActiveHotels", allHotels.size());
        return "index";
    }

    /**
     * Screen 02: Hotel Catalog & Live Search with optional city and capacity query filters.
     */
    @GetMapping("/hotels")
    public String catalogPage(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "minCapacity", required = false) Integer minCapacity,
            Model model) {
        List<Hotel> hotels = hotelService.getHotels(city, minCapacity);
        model.addAttribute("hotels", hotels);
        model.addAttribute("cityFilter", city != null ? city : "");
        model.addAttribute("minCapacity", minCapacity);
        return "hotels/catalog";
    }

    /**
     * Screen 03: Hotel Property Detail, Room Type Selector & Reservation Sidebar.
     */
    @GetMapping("/hotels/{id}")
    public String hotelDetailPage(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Hotel> hotelOpt = hotelService.getHotelById(id);
        if (hotelOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Requested hotel property was not found.");
            return "redirect:/hotels";
        }
        Hotel hotel = hotelOpt.get();
        model.addAttribute("hotel", hotel);
        model.addAttribute("defaultCheckIn", LocalDate.now().plusDays(1).toString());
        model.addAttribute("defaultCheckOut", LocalDate.now().plusDays(3).toString());
        return "hotels/detail";
    }

    /**
     * Screen 15: Hotel Owner Properties Management Dashboard.
     */
    @GetMapping("/owner/hotels")
    public String ownerHotelsDashboard(HttpSession session, Model model) {
        String ownerUid = resolveOwnerUid(session);
        List<Hotel> ownerHotels = hotelService.getHotelsForOwner(ownerUid);
        if (ownerHotels.isEmpty()) {
            ownerHotels = hotelService.getHotels(null, null);
        }
        model.addAttribute("hotels", ownerHotels);
        model.addAttribute("ownerUid", ownerUid);
        return "owner/hotels";
    }

    /**
     * Screen 16: New Hotel Listing & Initial Room Type Creation Form.
     */
    @GetMapping("/owner/hotels/new")
    public String newHotelForm(HttpSession session, Model model) {
        model.addAttribute("ownerUid", resolveOwnerUid(session));
        return "owner/hotel-new";
    }

    /**
     * Handles submission of a new hotel listing along with its primary room category.
     */
    @PostMapping("/owner/hotels")
    public String createHotelListing(
            @RequestParam("name") String name,
            @RequestParam("city") String city,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "featuredImage", required = false) String featuredImage,
            @RequestParam(name = "checkInTime", defaultValue = "14:00") String checkInTime,
            @RequestParam(name = "checkOutTime", defaultValue = "11:00") String checkOutTime,
            @RequestParam(name = "cancellationPolicy", required = false) String cancellationPolicy,
            @RequestParam(name = "roomName", defaultValue = "Deluxe Ocean Suite") String roomName,
            @RequestParam(name = "roomDescription", required = false) String roomDescription,
            @RequestParam(name = "roomCapacity", defaultValue = "2") int roomCapacity,
            @RequestParam(name = "basePrice", defaultValue = "38000") BigDecimal basePrice,
            @RequestParam(name = "totalRooms", defaultValue = "5") int totalRooms,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String ownerUid = resolveOwnerUid(session);

        Hotel hotel = new Hotel(ownerUid, name, description, address, city, featuredImage);
        hotel.setCheckInTime(checkInTime);
        hotel.setCheckOutTime(checkOutTime);
        hotel.setCancellationPolicy(cancellationPolicy != null && !cancellationPolicy.isBlank()
                ? cancellationPolicy
                : "Free cancellation up to 48 hours before check-in.");

        RoomType primaryRoom = new RoomType(
                roomName,
                roomDescription != null && !roomDescription.isBlank()
                        ? roomDescription
                        : "Spacious private suite with modern Sri Lankan hospitality.",
                roomCapacity,
                basePrice,
                totalRooms
        );

        List<HotelPhoto> photos = new ArrayList<>();
        if (featuredImage != null && !featuredImage.isBlank()) {
            photos.add(new HotelPhoto(featuredImage, name + " — Main Exterior", 1));
        }

        Hotel saved = hotelService.createHotel(ownerUid, hotel, List.of(primaryRoom), photos);
        redirectAttributes.addFlashAttribute("successMessage",
                "Published property '" + saved.getName() + "' with room type '" + primaryRoom.getName() + "'.");
        return "redirect:/owner/hotels";
    }

    /**
     * Soft-deactivates an existing hotel listing so historical bookings remain intact.
     */
    @PostMapping("/owner/hotels/{id}/deactivate")
    public String deactivateHotel(
            @PathVariable("id") String id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Hotel> target = hotelService.getHotelById(id);
            String callerUid = target.map(Hotel::getOwnerUid).orElse(resolveOwnerUid(session));
            Hotel updated = hotelService.deactivateHotel(callerUid, id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Property '" + updated.getName() + "' has been set to INACTIVE.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/hotels";
    }

    private String resolveOwnerUid(HttpSession session) {
        if (session != null) {
            Object uidAttr = session.getAttribute("uid");
            if (uidAttr instanceof String s && !s.isBlank()) {
                return s;
            }
            Object userUidAttr = session.getAttribute("userUid");
            if (userUidAttr instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return DEFAULT_OWNER_UID;
    }
}
