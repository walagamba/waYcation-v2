/**
 * UI Enhancements for waYcation 2.0
 * Nothing OS & Apple Minimalism:
 * - Theme toggling (Dark & White Mode)
 * - Top page progress bar animation
 * - Minimalist action loading overlay (Nothing OS style)
 * - Modal dialog open / close transitions
 * - Live client-side hotel search & price sort with skeleton indicators
 * - Dynamic booking price estimator
 */
document.addEventListener("DOMContentLoaded", () => {
  // 1. Top Page Loading Progress Bar
  let progressBar = document.getElementById("page-progress-bar");
  if (!progressBar) {
    progressBar = document.createElement("div");
    progressBar.id = "page-progress-bar";
    document.body.prepend(progressBar);
  }

  function triggerProgress() {
    progressBar.style.opacity = "1";
    progressBar.style.width = "30%";
    setTimeout(() => { progressBar.style.width = "75%"; }, 120);
    setTimeout(() => {
      progressBar.style.width = "100%";
      setTimeout(() => {
        progressBar.style.opacity = "0";
        setTimeout(() => { progressBar.style.width = "0%"; }, 300);
      }, 200);
    }, 350);
  }

  // Animate on initial load
  triggerProgress();

  // Animate on internal links
  document.querySelectorAll("a[href]").forEach((link) => {
    link.addEventListener("click", (e) => {
      const href = link.getAttribute("href");
      if (href && !href.startsWith("#") && !href.startsWith("javascript") && !link.target) {
        progressBar.style.opacity = "1";
        progressBar.style.width = "65%";
      }
    });
  });

  // 2. Minimalist Loading Screen Overlay (Nothing OS style)
  let loadingScreen = document.getElementById("global-loading-screen");
  if (!loadingScreen) {
    loadingScreen = document.createElement("div");
    loadingScreen.id = "global-loading-screen";
    loadingScreen.innerHTML = `
      <div class="loading-pill animate-fade-up">
        <span class="signal-dot signal-dot-pulse"></span>
        <span id="loading-screen-label">Syncing Stays...</span>
      </div>
    `;
    document.body.appendChild(loadingScreen);
  }

  window.showLoadingScreen = function(label) {
    const labelEl = document.getElementById("loading-screen-label");
    if (labelEl && label) labelEl.textContent = label;
    loadingScreen.classList.add("active");
  };

  window.hideLoadingScreen = function() {
    loadingScreen.classList.remove("active");
  };

  // Attach loading screen to important forms
  document.querySelectorAll("form").forEach((form) => {
    form.addEventListener("submit", (e) => {
      // If form is valid
      if (form.checkValidity && form.checkValidity()) {
        const submitBtn = form.querySelector("button[type='submit']");
        if (submitBtn) {
          submitBtn.style.opacity = "0.7";
          submitBtn.style.pointerEvents = "none";
        }
        triggerProgress();
        const action = form.getAttribute("action") || "";
        let label = "Processing...";
        if (action.includes("bookings")) label = "Locking Room Inventory...";
        else if (action.includes("packages")) label = "Curating Itinerary...";
        else if (action.includes("reviews")) label = "Publishing Feedback...";
        else if (action.includes("hotels")) label = "Updating Inventory...";
        window.showLoadingScreen(label);
      }
    });
  });

  // 3. Dark / White Theme Toggle
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

  // 4. Modal Dialog Open / Close Triggers
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

  // 5. Live Client-Side Hotel Search, Basis Filter & Price Sort
  const citySearchInput = document.getElementById("hotel-city-filter");
  const priceSortSelect = document.getElementById("hotel-price-sort");
  const hotelGrid = document.getElementById("hotel-cards-container");
  let activeBasisFilter = "all";

  function filterAndSortHotels() {
    if (!hotelGrid) return;
    const cards = Array.from(hotelGrid.querySelectorAll("[data-hotel-card]"));
    const query = citySearchInput ? citySearchInput.value.trim().toLowerCase() : "";
    const sortOrder = priceSortSelect ? priceSortSelect.value : "asc";

    let visibleCount = 0;
    cards.forEach((card) => {
      const city = (card.getAttribute("data-city") || "").toLowerCase();
      const name = (card.getAttribute("data-name") || "").toLowerCase();
      const basis = (card.getAttribute("data-basis") || "").toUpperCase();

      const matchesQuery = !query || city.includes(query) || name.includes(query);
      const matchesBasis = activeBasisFilter === "all" || basis === activeBasisFilter || !basis;

      if (matchesQuery && matchesBasis) {
        card.style.display = "";
        card.classList.add("animate-fade-up");
        visibleCount++;
      } else {
        card.style.display = "none";
      }
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

  if (citySearchInput) {
    citySearchInput.addEventListener("input", filterAndSortHotels);
  }
  if (priceSortSelect) {
    priceSortSelect.addEventListener("change", filterAndSortHotels);
  }

  // Basis Pill Chips Click Handler
  document.querySelectorAll("[data-basis-filter]").forEach((pill) => {
    pill.addEventListener("click", () => {
      document.querySelectorAll("[data-basis-filter]").forEach((p) => p.classList.remove("active"));
      pill.classList.add("active");
      activeBasisFilter = pill.getAttribute("data-basis-filter") || "all";
      filterAndSortHotels();
    });
  });

  // Quick Destination Preset Chips
  document.querySelectorAll("[data-destination-chip]").forEach((chip) => {
    chip.addEventListener("click", () => {
      const targetInputId = chip.getAttribute("data-target-input") || "hero-city";
      const targetInput = document.getElementById(targetInputId) || citySearchInput;
      const cityVal = chip.getAttribute("data-destination-chip") || "";
      if (targetInput) {
        targetInput.value = cityVal === "all" ? "" : cityVal;
        targetInput.dispatchEvent(new Event("input", { bubbles: true }));
      }
    });
  });

  // Clear Input Buttons (✕)
  document.querySelectorAll("[data-clear-input]").forEach((clearBtn) => {
    clearBtn.addEventListener("click", () => {
      const targetId = clearBtn.getAttribute("data-clear-input");
      const targetInput = document.getElementById(targetId);
      if (targetInput) {
        targetInput.value = "";
        targetInput.focus();
        targetInput.dispatchEvent(new Event("input", { bubbles: true }));
      }
    });
  });

  // 6. Booking.com-Style Interactive Date Range Selector
  function initBookingCalendar(container) {
    const trigger = container.querySelector(".booking-date-trigger");
    const popover = container.querySelector(".booking-calendar-popover");
    const checkInInput = container.querySelector("[name='checkIn'], .check-in-input");
    const checkOutInput = container.querySelector("[name='checkOut'], .check-out-input");
    const displayRange = container.querySelector(".booking-date-range-text");
    const nightBadge = container.querySelector(".night-count-pill");

    if (!trigger || !popover) return;

    const monthNames = [
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
    ];
    const dayNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let startDate = checkInInput && checkInInput.value ? new Date(checkInInput.value + "T00:00:00") : new Date(today.getTime() + 86400000);
    let endDate = checkOutInput && checkOutInput.value ? new Date(checkOutInput.value + "T00:00:00") : new Date(today.getTime() + 86400000 * 3);

    let viewYear = startDate.getFullYear();
    let viewMonth = startDate.getMonth();
    let pickingEnd = false;
    let tempStart = new Date(startDate);
    let tempEnd = new Date(endDate);

    function formatDateIso(d) {
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const day = String(d.getDate()).padStart(2, "0");
      return `${y}-${m}-${day}`;
    }

    function formatHuman(d) {
      return `${dayNames[d.getDay()]}, ${d.getDate()} ${monthNames[d.getMonth()].slice(0, 3)}`;
    }

    function syncDisplay() {
      if (checkInInput) checkInInput.value = formatDateIso(startDate);
      if (checkOutInput) checkOutInput.value = formatDateIso(endDate);

      const diffNights = Math.max(1, Math.round((endDate - startDate) / (1000 * 60 * 60 * 24)));
      if (displayRange) {
        displayRange.textContent = `${formatHuman(startDate)} — ${formatHuman(endDate)}`;
      }
      if (nightBadge) {
        nightBadge.textContent = `${diffNights} NIGHT${diffNights > 1 ? "S" : ""}`;
      }

      if (checkInInput) checkInInput.dispatchEvent(new Event("change", { bubbles: true }));
      if (checkOutInput) checkOutInput.dispatchEvent(new Event("change", { bubbles: true }));
    }

    function renderCalendar() {
      popover.innerHTML = `
        <div class="cal-header">
          <button type="button" class="cal-nav-btn cal-prev" title="Previous Month">‹</button>
          <span class="font-headline font-bold text-xs uppercase tracking-wider text-[var(--text-primary)]">
            ${monthNames[viewMonth]} ${viewYear}
          </span>
          <button type="button" class="cal-nav-btn cal-next" title="Next Month">›</button>
        </div>
        <div class="cal-weekdays">
          <div>Su</div><div>Mo</div><div>Tu</div><div>We</div><div>Th</div><div>Fr</div><div>Sa</div>
        </div>
        <div class="cal-days-grid"></div>
        <div class="mt-3 pt-2.5 border-t border-[var(--border-subtle)] flex flex-wrap items-center gap-1 text-[10px] font-mono">
          <span class="text-[var(--text-muted)] uppercase tracking-wider mr-1">Quick:</span>
          <button type="button" class="cal-quick-btn destination-chip text-[10px]" data-quick="weekend">Weekend</button>
          <button type="button" class="cal-quick-btn destination-chip text-[10px]" data-quick="3days">3 Days</button>
          <button type="button" class="cal-quick-btn destination-chip text-[10px]" data-quick="week">1 Week</button>
        </div>
      `;

      const daysGrid = popover.querySelector(".cal-days-grid");
      const firstDay = new Date(viewYear, viewMonth, 1).getDay();
      const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate();

      for (let i = 0; i < firstDay; i++) {
        const blank = document.createElement("div");
        daysGrid.appendChild(blank);
      }

      for (let d = 1; d <= daysInMonth; d++) {
        const cellDate = new Date(viewYear, viewMonth, d);
        cellDate.setHours(0, 0, 0, 0);

        const cell = document.createElement("button");
        cell.type = "button";
        cell.className = "cal-day-cell";
        cell.textContent = String(d);

        if (cellDate < today) {
          cell.classList.add("disabled");
        } else {
          if (cellDate.getTime() === today.getTime()) {
            cell.classList.add("today");
          }

          const isStart = cellDate.getTime() === tempStart.getTime();
          const isEnd = tempEnd && cellDate.getTime() === tempEnd.getTime();
          const inRange = tempEnd && cellDate > tempStart && cellDate < tempEnd;

          if (isStart) cell.classList.add("selected-start");
          if (isEnd) cell.classList.add("selected-end");
          if (inRange) cell.classList.add("in-range");

          cell.addEventListener("click", (e) => {
            e.stopPropagation();
            if (!pickingEnd) {
              tempStart = new Date(cellDate);
              tempEnd = null;
              pickingEnd = true;
              renderCalendar();
            } else {
              if (cellDate <= tempStart) {
                tempStart = new Date(cellDate);
                tempEnd = null;
                pickingEnd = true;
                renderCalendar();
              } else {
                tempEnd = new Date(cellDate);
                startDate = new Date(tempStart);
                endDate = new Date(tempEnd);
                pickingEnd = false;
                syncDisplay();
                renderCalendar();
                setTimeout(() => { popover.classList.remove("active"); }, 200);
              }
            }
          });
        }
        daysGrid.appendChild(cell);
      }

      popover.querySelector(".cal-prev").addEventListener("click", (e) => {
        e.stopPropagation();
        viewMonth--;
        if (viewMonth < 0) {
          viewMonth = 11;
          viewYear--;
        }
        renderCalendar();
      });

      popover.querySelector(".cal-next").addEventListener("click", (e) => {
        e.stopPropagation();
        viewMonth++;
        if (viewMonth > 11) {
          viewMonth = 0;
          viewYear++;
        }
        renderCalendar();
      });

      popover.querySelectorAll("[data-quick]").forEach((qBtn) => {
        qBtn.addEventListener("click", (e) => {
          e.stopPropagation();
          const q = qBtn.getAttribute("data-quick");
          const now = new Date();
          now.setHours(0, 0, 0, 0);

          if (q === "weekend") {
            const day = now.getDay();
            const distToFri = (5 - day + 7) % 7 || 7;
            tempStart = new Date(now.getTime() + distToFri * 86400000);
            tempEnd = new Date(tempStart.getTime() + 2 * 86400000);
          } else if (q === "3days") {
            tempStart = new Date(now.getTime() + 86400000);
            tempEnd = new Date(tempStart.getTime() + 3 * 86400000);
          } else if (q === "week") {
            tempStart = new Date(now.getTime() + 86400000);
            tempEnd = new Date(tempStart.getTime() + 7 * 86400000);
          }

          startDate = new Date(tempStart);
          endDate = new Date(tempEnd);
          pickingEnd = false;
          syncDisplay();
          renderCalendar();
          setTimeout(() => { popover.classList.remove("active"); }, 180);
        });
      });
    }

    function toggleCalendar(e) {
      if (popover.contains(e.target)) return;
      e.stopPropagation();
      const willBeActive = !popover.classList.contains("active");
      document.querySelectorAll(".occupancy-popover, .booking-calendar-popover").forEach((p) => {
        p.classList.remove("active");
        p.closest(".console-cell")?.classList.remove("active");
      });
      if (willBeActive) {
        popover.classList.add("active");
        container.classList.add("active");
        tempStart = new Date(startDate);
        tempEnd = new Date(endDate);
        viewYear = startDate.getFullYear();
        viewMonth = startDate.getMonth();
        pickingEnd = false;
        renderCalendar();
      } else {
        popover.classList.remove("active");
        container.classList.remove("active");
      }
    }

    container.addEventListener("click", toggleCalendar);

    document.addEventListener("click", (e) => {
      if (!container.contains(e.target)) {
        popover.classList.remove("active");
        container.classList.remove("active");
      }
    });

    syncDisplay();
  }

  document.querySelectorAll(".booking-date-container").forEach(initBookingCalendar);

  // 7. Interactive Occupancy Stepper Popover
  document.querySelectorAll(".occupancy-container").forEach((container) => {
    const trigger = container.querySelector(".occupancy-trigger");
    const popover = container.querySelector(".occupancy-popover");
    const summaryText = container.querySelector(".occupancy-summary-text");
    const guestsInput = container.querySelector("[name='minCapacity'], [name='guests']");
    const roomsInput = container.querySelector("[name='roomCount']");
    const adultsValEl = container.querySelector(".stepper-adults-val");
    const roomsValEl = container.querySelector(".stepper-rooms-val");

    let adultsCount = parseInt(guestsInput?.value || "2", 10);
    let roomsCount = parseInt(roomsInput?.value || "1", 10);

    function syncOccupancyUI() {
      if (adultsValEl) adultsValEl.textContent = String(adultsCount).padStart(2, "0");
      if (roomsValEl) roomsValEl.textContent = String(roomsCount).padStart(2, "0");
      if (guestsInput) guestsInput.value = String(adultsCount);
      if (roomsInput) roomsInput.value = String(roomsCount);
      if (summaryText) {
        summaryText.textContent = `${adultsCount} Traveler${adultsCount > 1 ? "s" : ""} · ${roomsCount} Room${roomsCount > 1 ? "s" : ""}`;
      }
    }

    function toggleOccupancy(e) {
      if (popover.contains(e.target)) return;
      e.stopPropagation();
      const willBeActive = !popover.classList.contains("active");
      document.querySelectorAll(".occupancy-popover, .booking-calendar-popover").forEach((p) => {
        p.classList.remove("active");
        p.closest(".console-cell")?.classList.remove("active");
      });
      if (willBeActive) {
        popover.classList.add("active");
        container.classList.add("active");
      } else {
        popover.classList.remove("active");
        container.classList.remove("active");
      }
    }

    if (trigger && popover) {
      container.addEventListener("click", toggleOccupancy);

      document.addEventListener("click", (e) => {
        if (!container.contains(e.target)) {
          popover.classList.remove("active");
          container.classList.remove("active");
        }
      });
    }

    container.querySelectorAll("[data-stepper-target]").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        e.stopPropagation();
        const target = btn.getAttribute("data-stepper-target");
        const delta = parseInt(btn.getAttribute("data-stepper-delta") || "1", 10);

        if (target === "adults") {
          adultsCount = Math.max(1, Math.min(20, adultsCount + delta));
        } else if (target === "rooms") {
          roomsCount = Math.max(1, Math.min(10, roomsCount + delta));
        }
        syncOccupancyUI();
      });
    });

    container.querySelectorAll("[data-preset-guests]").forEach((presetBtn) => {
      presetBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        adultsCount = parseInt(presetBtn.getAttribute("data-preset-guests") || "2", 10);
        roomsCount = parseInt(presetBtn.getAttribute("data-preset-rooms") || "1", 10);
        syncOccupancyUI();
        if (popover) popover.classList.remove("active");
      });
    });

    syncOccupancyUI();
  });

  // 8. Dynamic Booking Price Estimator on Hotel Detail & Checkout
  const bookingForm = document.getElementById("dynamic-booking-form");
  if (bookingForm) {
    const checkInInput = bookingForm.querySelector("[name='checkIn']");
    const checkOutInput = bookingForm.querySelector("[name='checkOut']");
    const roomCountInput = bookingForm.querySelector("[name='roomCount']");
    const roomTypeRadios = bookingForm.querySelectorAll("[name='roomTypeId']");
    const mealBasisRadios = bookingForm.querySelectorAll("[name='mealBasis']");
    const totalDisplay = document.getElementById("estimated-booking-total");
    const nightsDisplay = document.getElementById("estimated-booking-nights");
    const couponInput = document.getElementById("couponCode");
    const applyCouponBtn = document.getElementById("detail-apply-coupon-btn");
    const couponMsg = document.getElementById("detail-coupon-msg");
    let currentDiscount = 0;

    function recalculateBookingTotal() {
      let basePrice = parseFloat(bookingForm.getAttribute("data-default-price") || "0");
      roomTypeRadios.forEach((radio) => {
        if (radio.checked) {
          basePrice = parseFloat(radio.getAttribute("data-room-price") || String(basePrice));
        }
      });

      let mealModifier = 0;
      mealBasisRadios.forEach((radio) => {
        if (radio.checked) {
          mealModifier = parseFloat(radio.getAttribute("data-meal-price") || "0");
        }
      });

      const effectiveRate = basePrice + mealModifier;
      const rooms = Math.max(1, parseInt(roomCountInput?.value || "1", 10));
      let nights = 1;
      if (checkInInput?.value && checkOutInput?.value) {
        const start = new Date(checkInInput.value);
        const end = new Date(checkOutInput.value);
        const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
        if (diff > 0) nights = diff;
      }

      const subtotal = Math.round(effectiveRate * nights * rooms);
      const totalPayable = Math.max(0, subtotal - currentDiscount);

      if (totalDisplay) {
        if (currentDiscount > 0) {
          totalDisplay.innerHTML = `<span class="line-through text-xs text-[var(--text-muted)] mr-2 font-normal">LKR ${subtotal.toLocaleString()}</span><span>LKR ${totalPayable.toLocaleString()}</span>`;
        } else {
          totalDisplay.textContent = `LKR ${subtotal.toLocaleString()}`;
        }
      }
      if (nightsDisplay) nightsDisplay.textContent = `${nights} night(s)`;
    }

    function applyDetailCoupon() {
      if (!couponInput) return;
      const code = (couponInput.value || "").trim().toUpperCase();
      if (!code) {
        currentDiscount = 0;
        if (couponMsg) couponMsg.innerHTML = '<span class="text-amber-500 font-bold">Please enter a promo code.</span>';
        recalculateBookingTotal();
        return;
      }

      if (applyCouponBtn) {
        applyCouponBtn.disabled = true;
        applyCouponBtn.textContent = "Checking...";
      }
      if (couponMsg) couponMsg.innerHTML = '<span class="text-[var(--text-muted)]">Checking coupon...</span>';

      let basePrice = parseFloat(bookingForm.getAttribute("data-default-price") || "0");
      roomTypeRadios.forEach((radio) => {
        if (radio.checked) basePrice = parseFloat(radio.getAttribute("data-room-price") || String(basePrice));
      });
      let mealModifier = 0;
      mealBasisRadios.forEach((radio) => {
        if (radio.checked) mealModifier = parseFloat(radio.getAttribute("data-meal-price") || "0");
      });
      const rooms = Math.max(1, parseInt(roomCountInput?.value || "1", 10));
      let nights = 1;
      if (checkInInput?.value && checkOutInput?.value) {
        const start = new Date(checkInInput.value);
        const end = new Date(checkOutInput.value);
        const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
        if (diff > 0) nights = diff;
      }
      const rawSubtotal = Math.round((basePrice + mealModifier) * nights * rooms);

      fetch(`/api/coupons/validate?code=${encodeURIComponent(code)}&amount=${rawSubtotal}`)
        .then(res => res.json())
        .then(data => {
          if (applyCouponBtn) {
            applyCouponBtn.disabled = false;
            applyCouponBtn.textContent = "Apply";
          }
          if (data.valid) {
            currentDiscount = data.discount || 0;
            if (couponMsg) {
              couponMsg.innerHTML = `<span class="text-emerald-500 font-bold">âœ“ ${data.message} (- LKR ${Math.round(currentDiscount).toLocaleString()})</span>`;
            }
          } else {
            currentDiscount = 0;
            if (couponMsg) {
              couponMsg.innerHTML = `<span class="text-[var(--signal-red)] font-bold">âœ• ${data.message || 'Invalid coupon'}</span>`;
            }
          }
          recalculateBookingTotal();
        })
        .catch(err => {
          if (applyCouponBtn) {
            applyCouponBtn.disabled = false;
            applyCouponBtn.textContent = "Apply";
          }
          if (couponMsg) couponMsg.innerHTML = '<span class="text-[var(--signal-red)]">Error validating promo code</span>';
          currentDiscount = 0;
          recalculateBookingTotal();
        });
    }

    if (applyCouponBtn) {
      applyCouponBtn.addEventListener("click", applyDetailCoupon);
    }
    if (couponInput) {
      couponInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
          e.preventDefault();
          applyDetailCoupon();
        }
      });
    }
    [checkInInput, checkOutInput, roomCountInput].forEach((el) => {
      if (el) el.addEventListener("change", recalculateBookingTotal);
    });
    roomTypeRadios.forEach((radio) => {
      radio.addEventListener("change", recalculateBookingTotal);
    });
    mealBasisRadios.forEach((radio) => {
      radio.addEventListener("change", recalculateBookingTotal);
    });

    recalculateBookingTotal();
  }
});
