package com.waycation.controller;

import com.waycation.model.Booking;
import com.waycation.model.BookingStatus;
import com.waycation.model.Hotel;
import com.waycation.model.HotelPhoto;
import com.waycation.model.Review;
import com.waycation.model.RoomType;
import com.waycation.repository.BookingRepository;
import com.waycation.service.HotelService;
import com.waycation.service.ReviewService;
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
    // Spring MVC controller serving catalog search, property details, and owner dashboards
@Controller
public class HotelController {

    private static final String DEFAULT_OWNER_UID = "owner-demo-uid";

    private final HotelService hotelService;
    private final ReviewService reviewService;
    private final BookingRepository bookingRepository;

    public HotelController(HotelService hotelService, ReviewService reviewService, BookingRepository bookingRepository) {
        this.hotelService = hotelService;
        this.reviewService = reviewService;
        this.bookingRepository = bookingRepository;
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
    // Public endpoint for searching and browsing hotel listings
    @GetMapping("/hotels")
    public String catalogPage(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "minCapacity", required = false) Integer minCapacity,
            Model model) {
        List<Hotel> hotels = hotelService.getHotels(city, minCapacity);
        // Pass filtered list of hotels to Thymeleaf template context
        model.addAttribute("hotels", hotels);
        model.addAttribute("cityFilter", city != null ? city : "");
        model.addAttribute("minCapacity", minCapacity);
        // Render hotels catalog Thymeleaf view
        return "hotels/catalog";
    }

    /**
     * Screen 03: Hotel Property Detail, Room Type Selector, Reservation Sidebar & Verified Reviews.
     */
    // Public endpoint for viewing detailed hotel information and available rooms
    @GetMapping("/hotels/{id}")
    public String hotelDetailPage(
            @PathVariable("id") String id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Hotel> hotelOpt = hotelService.getHotelById(id);
        if (hotelOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Requested hotel property was not found.");
            return "redirect:/hotels";
        }
        Hotel hotel = hotelOpt.get();
        model.addAttribute("hotel", hotel);
        model.addAttribute("defaultCheckIn", LocalDate.now().plusDays(1).toString());
        model.addAttribute("defaultCheckOut", LocalDate.now().plusDays(3).toString());

        List<Review> reviews = reviewService != null ? reviewService.getPublishedReviewsForHotel(id) : List.of();
        double avg = reviews.isEmpty() ? 0.0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("averageRating", String.format("%.1f", avg));

        boolean canReview = false;
        String eligibleBookingId = null;
        if (session != null) {
            String uid = null;
            Object uidAttr = session.getAttribute("uid");
            if (uidAttr instanceof String s && !s.isBlank()) {
                uid = s;
            }
            if (uid != null && bookingRepository != null) {
                List<Booking> userBookings = bookingRepository.findByTouristUidOrderByCreatedAtDesc(uid);
                for (Booking b : userBookings) {
                    if (id.equals(b.getHotelId())) {
                        boolean eligibleStatus = b.getStatus() == BookingStatus.COMPLETED
                                || (b.getStatus() == BookingStatus.CONFIRMED && b.getCheckOut() != null && !b.getCheckOut().isAfter(LocalDate.now()));
                        if (eligibleStatus) {
                            boolean alreadyReviewed = reviews.stream().anyMatch(r -> b.getId().equals(r.getBookingId()));
                            if (!alreadyReviewed) {
                                canReview = true;
                                eligibleBookingId = b.getId();
                                break;
                            }
                        }
                    }
                }
            }
        }
        model.addAttribute("canReview", canReview);
        model.addAttribute("eligibleBookingId", eligibleBookingId);

        return "hotels/detail";
    }

    /**
     * Screen 15: Hotel Owner Properties Management Dashboard.
     */
    // Owner dashboard endpoint to view their registered properties
    @GetMapping("/owner/hotels")
    public String ownerHotelsDashboard(HttpSession session, Model model) {
        String ownerUid = resolveOwnerUid(session);
        List<Hotel> ownerHotels = hotelService.getHotelsForOwner(ownerUid);

        // Pass filtered list of hotels to Thymeleaf template context
        model.addAttribute("hotels", ownerHotels);
        model.addAttribute("ownerUid", ownerUid);
        return "owner/hotels";
    }

    /**
     * Screen 16: New Hotel Listing & Room Types Creation Form.
     */
    @GetMapping("/owner/hotels/new")
    public String newHotelForm(HttpSession session, Model model) {
        model.addAttribute("ownerUid", resolveOwnerUid(session));
        return "owner/hotel-new";
    }

    /**
     * Handles submission of a new hotel listing along with one or more room categories.
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
            @RequestParam(name = "roomName", required = false) List<String> roomNames,
            @RequestParam(name = "roomDescription", required = false) List<String> roomDescriptions,
            @RequestParam(name = "roomCapacity", required = false) List<Integer> roomCapacities,
            @RequestParam(name = "basePrice", required = false) List<BigDecimal> basePrices,
            @RequestParam(name = "totalRooms", required = false) List<Integer> totalRoomsList,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String ownerUid = resolveOwnerUid(session);

        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setCity(city);
        hotel.setAddress(address);
        hotel.setDescription(description);
        hotel.setFeaturedImage(featuredImage);
        hotel.setCheckInTime(checkInTime);
        hotel.setCheckOutTime(checkOutTime);
        hotel.setCancellationPolicy(cancellationPolicy);

        List<RoomType> rooms = new ArrayList<>();
        if (roomNames != null && !roomNames.isEmpty()) {
            for (int i = 0; i < roomNames.size(); i++) {
                String rName = roomNames.get(i);
                if (rName != null && !rName.isBlank()) {
                    String rDesc = (roomDescriptions != null && roomDescriptions.size() > i && roomDescriptions.get(i) != null)
                            ? roomDescriptions.get(i)
                            : "Private sanctuary with modern amenities.";
                    int rCap = (roomCapacities != null && roomCapacities.size() > i && roomCapacities.get(i) != null)
                            ? roomCapacities.get(i)
                            : 2;
                    BigDecimal rPrice = (basePrices != null && basePrices.size() > i && basePrices.get(i) != null)
                            ? basePrices.get(i)
                            : BigDecimal.valueOf(35000);
                    int rTotal = (totalRoomsList != null && totalRoomsList.size() > i && totalRoomsList.get(i) != null)
                            ? totalRoomsList.get(i)
                            : 5;
                    rooms.add(new RoomType(rName.trim(), rDesc, rCap, rPrice, rTotal));
                }
            }
        }
        if (rooms.isEmpty()) {
            rooms.add(new RoomType("Deluxe Ocean Suite", "Spacious private suite with modern Sri Lankan hospitality.", 2, BigDecimal.valueOf(38000), 5));
        }

        List<HotelPhoto> photos = new ArrayList<>();
        if (featuredImage != null && !featuredImage.isBlank()) {
            photos.add(new HotelPhoto(featuredImage, name + " — Main Exterior", 1));
        }

        Hotel saved = hotelService.createHotel(ownerUid, hotel, rooms, photos);
        redirectAttributes.addFlashAttribute("successMessage",
                "Published property '" + saved.getName() + "' with " + rooms.size() + " room categories.");
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
            String callerUid = resolveOwnerUid(session);   // who is logged in, not who owns the hotel
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

    @GetMapping("/owner/hotels/{id}/edit")
    public String editHotelForm(
            @PathVariable("id") String id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Hotel> hotelOpt = hotelService.getHotelById(id);
        if (hotelOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hotel not found.");
            return "redirect:/owner/hotels";
        }
        Hotel hotel = hotelOpt.get();
        // Only the owner of this hotel may open its edit page
        if (!hotel.getOwnerUid().equals(resolveOwnerUid(session))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only the owner of this hotel can edit it.");
            return "redirect:/owner/hotels";
        }
        model.addAttribute("hotel", hotel);
        model.addAttribute("roomTypes", hotel.getRoomTypes());
        model.addAttribute("ownerUid", resolveOwnerUid(session));
        return "owner/hotel-edit";
    }

    @PostMapping("/owner/hotels/{id}/edit")
    public String updateHotel(
            @PathVariable("id") String id,
            @RequestParam("name") String name,
            @RequestParam("city") String city,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "featuredImage", required = false) String featuredImage,
            @RequestParam(name = "checkInTime", defaultValue = "14:00") String checkInTime,
            @RequestParam(name = "checkOutTime", defaultValue = "11:00") String checkOutTime,
            @RequestParam(name = "cancellationPolicy", required = false) String cancellationPolicy,
            @RequestParam(name = "newRoomName", required = false) List<String> newRoomNames,
            @RequestParam(name = "newRoomDescription", required = false) List<String> newRoomDescriptions,
            @RequestParam(name = "newRoomCapacity", required = false) List<Integer> newRoomCapacities,
            @RequestParam(name = "newBasePrice", required = false) List<BigDecimal> newBasePrices,
            @RequestParam(name = "newTotalRooms", required = false) List<Integer> newTotalRoomsList,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Hotel> hotelOpt = hotelService.getHotelById(id);
            if (hotelOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Hotel not found.");
                return "redirect:/owner/hotels";
            }
            // The caller is the logged-in user, never the hotel's own owner field
            String callerUid = resolveOwnerUid(session);

            Hotel updateData = new Hotel();
            // new Hotel() starts as ACTIVE / "Sri Lanka"; null means "keep the current value"
            updateData.setStatus(null);
            updateData.setCountry(null);
            updateData.setName(name);
            updateData.setCity(city);
            updateData.setAddress(address);
            updateData.setDescription(description);
            if (featuredImage != null && !featuredImage.isBlank()) {
                updateData.setFeaturedImage(featuredImage);
            }
            updateData.setCheckInTime(checkInTime);
            updateData.setCheckOutTime(checkOutTime);
            updateData.setCancellationPolicy(cancellationPolicy);

            List<RoomType> addedRooms = new ArrayList<>();
            if (newRoomNames != null && !newRoomNames.isEmpty()) {
                for (int i = 0; i < newRoomNames.size(); i++) {
                    String rName = newRoomNames.get(i);
                    if (rName != null && !rName.isBlank()) {
                        String rDesc = (newRoomDescriptions != null && newRoomDescriptions.size() > i && newRoomDescriptions.get(i) != null)
                                ? newRoomDescriptions.get(i)
                                : "Private suite with luxury comforts.";
                        int rCap = (newRoomCapacities != null && newRoomCapacities.size() > i && newRoomCapacities.get(i) != null)
                                ? newRoomCapacities.get(i)
                                : 2;
                        BigDecimal rPrice = (newBasePrices != null && newBasePrices.size() > i && newBasePrices.get(i) != null)
                                ? newBasePrices.get(i)
                                : BigDecimal.valueOf(35000);
                        int rTotal = (newTotalRoomsList != null && newTotalRoomsList.size() > i && newTotalRoomsList.get(i) != null)
                                ? newTotalRoomsList.get(i)
                                : 5;
                        addedRooms.add(new RoomType(rName.trim(), rDesc, rCap, rPrice, rTotal));
                    }
                }
            }

            hotelService.updateHotelWithRooms(callerUid, id, updateData, addedRooms);
            redirectAttributes.addFlashAttribute("successMessage", "Property '" + name + "' updated successfully.");
            return "redirect:/hotels/" + id;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/owner/hotels/" + id + "/edit";
        }
    }

    @PostMapping("/owner/hotels/{hotelId}/rooms/{roomId}/delete")
    public String deleteRoomCategory(
            @PathVariable("hotelId") String hotelId,
            @PathVariable("roomId") String roomId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            String callerUid = resolveOwnerUid(session);   // who is logged in, not who owns the hotel
            hotelService.deleteRoomType(callerUid, hotelId, roomId);
            redirectAttributes.addFlashAttribute("successMessage", "Room category removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/hotels/" + hotelId + "/edit";
    }
}
