const API = "http://localhost:8080/api";

const $ = (id) => document.getElementById(id);

// ========================================
// INITIALIZATION
// ========================================

document.addEventListener("DOMContentLoaded", () => {

    // Display current date
    const today = $("today");

    if (today) {
        today.textContent = new Date().toLocaleDateString(
            undefined,
            {
                weekday: "short",
                month: "short",
                day: "numeric",
                year: "numeric"
            }
        );
    }

    // Load dashboard initially
    loadDashboard().catch(error => {
        toast(
            "Backend not connected. Start HotelServer on port 8080.",
            true
        );
    });

});


// ========================================
// TOAST NOTIFICATIONS
// ========================================

function toast(message, error = false) {

    const el = $("toast");

    if (!el) return;

    el.textContent = message;

    el.className = "toast show" + (error ? " error" : "");

    clearTimeout(el.toastTimer);

    el.toastTimer = setTimeout(() => {
        el.className = "toast";
    }, 3200);

}


// ========================================
// HTML ESCAPING
// ========================================

function esc(value) {

    return String(value == null ? "" : value).replace(
        /[&<>"']/g,
        character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[character]
    );

}


// ========================================
// BACKEND API CONNECTION
// ========================================

async function api(path, options = {}) {

    const response = await fetch(API + path, options);

    let data;

    try {
        data = await response.json();
    } catch {
        throw new Error("Invalid response from server.");
    }

    if (!response.ok || data.success === false) {

        throw new Error(
            data.message || "Something went wrong."
        );

    }

    return data;

}


// ========================================
// FORM DATA
// ========================================

function formBody(form) {

    return new URLSearchParams(
        new FormData(form)
    ).toString();

}


async function post(path, form) {

    return api(path, {

        method: "POST",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded;charset=UTF-8"
        },

        body: formBody(form)

    });

}


// ========================================
// PAGE NAVIGATION
// ========================================

const pageTitles = {

    dashboard: "Dashboard",
    rooms: "Rooms",
    guests: "Guests",
    stays: "Check-in / Check-out",
    maintenance: "Maintenance"

};


function showPage(name) {

    const targetPage = $(name + "-page");

    if (!targetPage) return;

    // Remove active class from all pages
    document.querySelectorAll(".page").forEach(page => {

        page.classList.remove("active");

    });

    // Update navigation buttons
    document.querySelectorAll(".nav-link").forEach(button => {

        button.classList.toggle(
            "active",
            button.dataset.page === name
        );

    });

    // Display selected page
    targetPage.classList.add("active");

    // Update heading
    $("page-title").textContent = pageTitles[name];

    // Load selected page information
    loadPage(name);

}


// ========================================
// DOOR OPENING ANIMATION
// ========================================

let doorAnimationRunning = false;

function openRoomPage() {

    const door = $("roomDoorTransition");

    if (!door || doorAnimationRunning) {

        showPage("rooms");
        return;

    }

    doorAnimationRunning = true;

    // Reset animation
    door.classList.remove("opening");

    // Make transition visible
    door.classList.add("show");

    // Start door animation
    requestAnimationFrame(() => {

        requestAnimationFrame(() => {

            door.classList.add("opening");

        });

    });

    // Show rooms page after animation
    setTimeout(() => {

        showPage("rooms");

    }, 650);

    // Hide transition
    setTimeout(() => {

        door.classList.remove("show", "opening");

        doorAnimationRunning = false;

    }, 1450);

}


// ========================================
// NAVIGATION EVENT LISTENERS
// ========================================

document.querySelectorAll(".nav-link").forEach(button => {

    button.addEventListener("click", () => {

        const page = button.dataset.page;

        if (page === "rooms") {

            openRoomPage();

        } else {

            showPage(page);

        }

    });

});


// Dashboard buttons and View All buttons
document.querySelectorAll("[data-goto]").forEach(button => {

    button.addEventListener("click", () => {

        const page = button.dataset.goto;

        if (page === "rooms") {

            openRoomPage();

        } else {

            showPage(page);

        }

    });

});


// ========================================
// DASHBOARD
// ========================================

async function loadDashboard() {

    const d = await api("/dashboard");

    const c = d.roomCounts;

    $("totalRooms").textContent = c.totalRooms;

    $("availableRooms").textContent = c.availableRooms;

    $("occupiedRooms").textContent = c.occupiedRooms;

    $("maintenanceRooms").textContent = c.maintenanceRooms;

    renderActivity(
        "recentCheckIns",
        d.recentCheckIns,
        "Checked in"
    );

    renderActivity(
        "recentCheckOuts",
        d.recentCheckOuts,
        "Checked out"
    );

}


// ========================================
// RECENT CHECK-IN / CHECK-OUT ACTIVITIES
// ========================================

function renderActivity(id, records, action) {

    const el = $(id);

    if (!el) return;

    if (!records || records.length === 0) {

        el.innerHTML =
            '<div class="empty">No recent activity yet.</div>';

        return;

    }

    el.innerHTML = records.map(r => `

        <div class="activity-item">

            <div>

                <div class="activity-name">
                    ${esc(r.guestName)}
                </div>

                <div class="activity-sub">
                    ${action}
                    · Room ${esc(r.roomNumber)}
                    · Stay #${esc(r.stayId)}
                </div>

            </div>

            <div class="activity-date">
                ${esc(r.date)}
            </div>

        </div>

    `).join("");

}


// ========================================
// ROOM MANAGEMENT
// ========================================

async function loadRooms() {

    const rows = await api("/rooms");

    $("roomsTable").innerHTML = rows.map(r => `

        <tr>

            <td>
                ${esc(r.roomNumber)}
                <small>(ID: ${esc(r.roomId)})</small>
            </td>

            <td>${esc(r.roomType)}</td>

            <td>
                ₹${Number(r.dailyPrice).toLocaleString("en-IN")}
            </td>

            <td>

                <span class="pill ${r.maintenanceStatus ? "yes" : "no"}">

                    ${r.maintenanceStatus
                        ? "Under maintenance"
                        : "No"}

                </span>

            </td>

        </tr>

    `).join("") ||

    '<tr><td colspan="4" class="empty">No rooms found.</td></tr>';

}


// ========================================
// GUEST MANAGEMENT
// ========================================

async function loadGuests() {

    const rows = await api("/guests");

    $("guestsTable").innerHTML = rows.map(g => `

        <tr>

            <td>${esc(g.guestId)}</td>

            <td>${esc(g.name)}</td>

            <td>${esc(g.phone)}</td>

            <td>${esc(g.email || "—")}</td>

        </tr>

    `).join("") ||

    '<tr><td colspan="4" class="empty">No guests registered.</td></tr>';

}


// ========================================
// STAY HISTORY
// ========================================

async function loadStays() {

    const rows = await api("/stays");

    $("staysTable").innerHTML = rows.map(s => `

        <tr>

            <td>${esc(s.stayId)}</td>

            <td>${esc(s.guestId)}</td>

            <td>${esc(s.roomId)}</td>

            <td>${esc(s.checkIn)}</td>

            <td>${esc(s.checkOut || "—")}</td>

            <td>
                ${s.totalBill == null
                    ? "—"
                    : "₹" + Number(s.totalBill).toFixed(2)}
            </td>

            <td>

                <span class="pill ${s.checkOut ? "completed" : "occupied"}">

                    ${s.checkOut ? "Completed" : "Active"}

                </span>

            </td>

        </tr>

    `).join("") ||

    '<tr><td colspan="7" class="empty">No stays recorded.</td></tr>';

}


// ========================================
// MAINTENANCE MANAGEMENT
// ========================================

async function loadMaintenance() {

    const rows = await api("/maintenance");

    $("maintenanceTable").innerHTML = rows.map(m => `

        <tr>

            <td>${esc(m.maintenanceId)}</td>

            <td>${esc(m.roomId)}</td>

            <td>${esc(m.reason)}</td>

            <td>${esc(m.startDate)}</td>

            <td>${esc(m.endDate || "—")}</td>

            <td>

                <span class="pill ${m.status === "ACTIVE" ? "active" : "completed"}">

                    ${esc(m.status)}

                </span>

            </td>

        </tr>

    `).join("") ||

    '<tr><td colspan="6" class="empty">No maintenance records.</td></tr>';

}


// ========================================
// LOAD SELECTED PAGE
// ========================================

async function loadPage(name) {

    try {

        if (name === "dashboard") {

            await loadDashboard();

        }

        else if (name === "rooms") {

            await loadRooms();

        }

        else if (name === "guests") {

            await loadGuests();

        }

        else if (name === "stays") {

            await loadStays();

        }

        else if (name === "maintenance") {

            await loadMaintenance();

        }

    }

    catch (error) {

        toast(
            "Could not load data: " + error.message,
            true
        );

    }

}


// ========================================
// GUEST REGISTRATION
// ========================================

$("guestForm").addEventListener("submit", async e => {

    e.preventDefault();

    const form = e.target;

    try {

        // Validate guest name
        const guestName = form.elements.name.value.trim();

        if (!guestName) {

            toast("Please enter the guest name.", true);

            form.elements.name.focus();

            return;

        }

        // Validate phone number
        const phone = form.elements.phone.value.trim();

        if (!/^[0-9]{10,15}$/.test(phone)) {

            toast("Enter a valid phone number (10–15 digits).", true);

            form.elements.phone.focus();

            return;

        }

        // Submit guest information
        const d = await post("/guests", form);

        toast(
            "Guest registered successfully! Guest ID: " + d.guestId
        );

        form.reset();

        await loadGuests();

    }

    catch (error) {

        toast(error.message, true);

    }

});


// ========================================
// GUEST CHECK-IN
// ========================================

$("checkinForm").addEventListener("submit", async e => {

    e.preventDefault();

    try {

        await post("/checkin", e.target);

        toast("Guest checked in successfully!");

        e.target.reset();

        await loadStays();

        await loadDashboard();

    }

    catch (error) {

        toast(error.message, true);

    }

});


// ========================================
// GUEST CHECK-OUT
// ========================================

$("checkoutForm").addEventListener("submit", async e => {

    e.preventDefault();

    try {

        await post("/checkout", e.target);

        toast("Guest checked out successfully!");

        e.target.reset();

        await loadStays();

        await loadDashboard();

    }

    catch (error) {

        toast(error.message, true);

    }

});


// ========================================
// START MAINTENANCE
// ========================================

$("maintenanceForm").addEventListener("submit", async e => {

    e.preventDefault();

    try {

        await post("/maintenance/start", e.target);

        toast("Maintenance started successfully!");

        e.target.reset();

        await loadMaintenance();

        await loadDashboard();

        await loadRooms();

    }

    catch (error) {

        toast(error.message, true);

    }

});


// ========================================
// COMPLETE MAINTENANCE
// ========================================

$("completeMaintenanceForm").addEventListener("submit", async e => {

    e.preventDefault();

    try {

        await post("/maintenance/complete", e.target);

        toast("Maintenance completed successfully!");

        e.target.reset();

        await loadMaintenance();

        await loadDashboard();

        await loadRooms();

    }

    catch (error) {

        toast(error.message, true);

    }

});


// ========================================
// REFRESH BUTTONS
// ========================================

$("refreshBtn").addEventListener("click", () => {

    const activePage = document.querySelector(".page.active");

    if (activePage) {

        const pageName = activePage.id.replace("-page", "");

        loadPage(pageName);

    }

});


$("roomsRefresh").addEventListener("click", () => {

    loadRooms().catch(error => toast(error.message, true));

});


$("guestsRefresh").addEventListener("click", () => {

    loadGuests().catch(error => toast(error.message, true));

});


$("staysRefresh").addEventListener("click", () => {

    loadStays().catch(error => toast(error.message, true));

});


$("maintenanceRefresh").addEventListener("click", () => {

    loadMaintenance().catch(error => toast(error.message, true));

});
