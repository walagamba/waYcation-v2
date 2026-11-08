/**
 * UI Enhancements for waYcation 2.0
 * Handles theme toggling, modal dialogs, live hotel search/sort filtering,
 * dynamic booking price calculations, and Tinder-style group decision card swiping.
 */
document.addEventListener("DOMContentLoaded", () => {
  // 1. Dark / Light Theme Toggle
  const savedTheme = localStorage.getItem("waycation_theme");
  if (savedTheme === "dark") {
    document.documentElement.classList.add("dark");
  }

  document.querySelectorAll("[data-theme-toggle]").forEach((btn) => {
    btn.addEventListener("click", () => {
      document.documentElement.classList.toggle("dark");
      const isDark = document.documentElement.classList.contains("dark");
      localStorage.setItem("waycation_theme", isDark ? "dark" : "light");
    });
  });

  // 2. Modal Dialog Open / Close Triggers
  document.querySelectorAll("[data-modal-open]").forEach((trigger) => {
    trigger.addEventListener("click", () => {
      const targetId = trigger.getAttribute("data-modal-open");
      const modal = document.getElementById(targetId);
      if (modal) {
        modal.classList.add("active");
        modal.classList.remove("hidden");
      }
    });
  });

  document.querySelectorAll("[data-modal-close]").forEach((closer) => {
    closer.addEventListener("click", () => {
      const targetId = closer.getAttribute("data-modal-close");
      const modal = targetId
        ? document.getElementById(targetId)
        : closer.closest(".modal-backdrop");
      if (modal) {
        modal.classList.remove("active");
        modal.classList.add("hidden");
      }
    });
  });

  // 3. Live Client-Side Hotel City Filter & Price Sort
  const citySearchInput = document.getElementById("hotel-city-filter");
  const priceSortSelect = document.getElementById("hotel-price-sort");
  const hotelGrid = document.getElementById("hotel-cards-container");

  function filterAndSortHotels() {
    if (!hotelGrid) return;
    const cards = Array.from(hotelGrid.querySelectorAll("[data-hotel-card]"));
    const query = citySearchInput ? citySearchInput.value.trim().toLowerCase() : "";
    const sortOrder = priceSortSelect ? priceSortSelect.value : "asc";

    let visibleCount = 0;
    cards.forEach((card) => {
      const city = (card.getAttribute("data-city") || "").toLowerCase();
      const name = (card.getAttribute("data-name") || "").toLowerCase();
      const matches = !query || city.includes(query) || name.includes(query);
      card.style.display = matches ? "" : "none";
      if (matches) visibleCount++;
    });

    cards.sort((a, b) => {
      const priceA = parseFloat(a.getAttribute("data-price") || "0");
      const priceB = parseFloat(b.getAttribute("data-price") || "0");
      return sortOrder === "desc" ? priceB - priceA : priceA - priceB;
    });

    cards.forEach((card) => hotelGrid.appendChild(card));
    const counter = document.getElementById("hotel-results-count");
    if (counter) counter.textContent = String(visibleCount);
  }

  if (citySearchInput) citySearchInput.addEventListener("input", filterAndSortHotels);
  if (priceSortSelect) priceSortSelect.addEventListener("change", filterAndSortHotels);

  // 4. Dynamic Booking Price Estimator on Hotel Detail & Checkout
  const bookingForm = document.getElementById("dynamic-booking-form");
  if (bookingForm) {
    const checkInInput = bookingForm.querySelector("[name='checkIn']");
    const checkOutInput = bookingForm.querySelector("[name='checkOut']");
    const roomCountInput = bookingForm.querySelector("[name='roomCount']");
    const roomTypeRadios = bookingForm.querySelectorAll("[name='roomTypeId']");
    const totalDisplay = document.getElementById("estimated-booking-total");
    const nightsDisplay = document.getElementById("estimated-booking-nights");

    function recalculateBookingTotal() {
      let basePrice = parseFloat(bookingForm.getAttribute("data-default-price") || "0");
      roomTypeRadios.forEach((radio) => {
        if (radio.checked) {
          basePrice = parseFloat(radio.getAttribute("data-room-price") || String(basePrice));
        }
      });

      const rooms = Math.max(1, parseInt(roomCountInput?.value || "1", 10));
      let nights = 1;
      if (checkInInput?.value && checkOutInput?.value) {
        const start = new Date(checkInInput.value);
        const end = new Date(checkOutInput.value);
        const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
        if (diff > 0) nights = diff;
      }

      const total = Math.round(basePrice * nights * rooms);
      if (totalDisplay) totalDisplay.textContent = `LKR ${total.toLocaleString()}`;
      if (nightsDisplay) nightsDisplay.textContent = `${nights} night(s)`;
    }

    [checkInInput, checkOutInput, roomCountInput].forEach((el) => {
      if (el) el.addEventListener("change", recalculateBookingTotal);
    });
    roomTypeRadios.forEach((radio) => {
      radio.addEventListener("change", recalculateBookingTotal);
    });
  }

  // 5. Tinder-Style Group Voting Deck Enhancement
  const swipeDeck = document.getElementById("tinder-swipe-deck");
  if (swipeDeck) {
    const cards = Array.from(swipeDeck.querySelectorAll("[data-swipe-card]"));
    const winnerPanel = document.getElementById("tinder-winner-panel");
    let currentIndex = 0;

    function updateDeckView() {
      cards.forEach((card, idx) => {
        card.style.display = idx === currentIndex ? "block" : "none";
      });
      if (currentIndex >= cards.length && winnerPanel) {
        swipeDeck.style.display = "none";
        winnerPanel.classList.remove("hidden");
      }
    }

    document.querySelectorAll("[data-swipe-action]").forEach((btn) => {
      btn.addEventListener("click", () => {
        currentIndex++;
        updateDeckView();
      });
    });

    updateDeckView();
  }
});
