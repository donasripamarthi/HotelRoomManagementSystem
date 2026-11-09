const API = "http://localhost:8080/api";
const $ = (id) => document.getElementById(id);

document.getElementById("today").textContent =
  new Date().toLocaleDateString(undefined, { weekday: "short", month: "short", day: "numeric", year: "numeric" });

function toast(message, error = false) {
  const el = $("toast");
  el.textContent = message;
  el.className = "toast show" + (error ? " error" : "");
  setTimeout(() => el.className = "toast", 3200);
}
function esc(value) {
  return String(value == null ? "" : value).replace(/[&<>"']/g, c =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}
async function api(path, options = {}) {
  const response = await fetch(API + path, options);
  const data = await response.json();
  if (!response.ok || data.success === false) throw new Error(data.message || "Request failed");
  return data;
}
function formBody(form) {
  return new URLSearchParams(new FormData(form)).toString();
}
async function post(path, form) {
  return api(path, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: formBody(form)
  });
}
function showPage(name) {
  document.querySelectorAll(".page").forEach(p => p.classList.remove("active"));
  document.querySelectorAll(".nav-link").forEach(b => b.classList.toggle("active", b.dataset.page === name));
  $(name + "-page").classList.add("active");
  const titles = { dashboard: "Dashboard", rooms: "Rooms", guests: "Guests", stays: "Check-in / Check-out", maintenance: "Maintenance" };
  $("page-title").textContent = titles[name];
  loadPage(name);
}
document.querySelectorAll(".nav-link").forEach(b => b.addEventListener("click", () => showPage(b.dataset.page)));
document.querySelectorAll("[data-goto]").forEach(b => b.addEventListener("click", () => showPage(b.dataset.goto)));

async function loadDashboard() {
  const d = await api("/dashboard");
  const c = d.roomCounts;
  $("totalRooms").textContent = c.totalRooms;
  $("availableRooms").textContent = c.availableRooms;
  $("occupiedRooms").textContent = c.occupiedRooms;
  $("maintenanceRooms").textContent = c.maintenanceRooms;
  renderActivity("recentCheckIns", d.recentCheckIns, "Checked in");
  renderActivity("recentCheckOuts", d.recentCheckOuts, "Checked out");
}
function renderActivity(id, records, action) {
  const el = $(id);
  if (!records.length) { el.innerHTML = '<div class="empty">No recent activity yet.</div>'; return; }
  el.innerHTML = records.map(r => `<div class="activity-item"><div><div class="activity-name">${esc(r.guestName)}</div><div class="activity-sub">${action} · Room ${esc(r.roomNumber)} · Stay #${esc(r.stayId)}</div></div><div class="activity-date">${esc(r.date)}</div></div>`).join("");
}
async function loadRooms() {
  const rows = await api("/rooms");
  $("roomsTable").innerHTML = rows.map(r => `<tr><td>${esc(r.roomNumber)} <small>(ID: ${r.roomId})</small></td><td>${esc(r.roomType)}</td><td>₹${Number(r.dailyPrice).toLocaleString("en-IN")}</td><td><span class="pill ${r.maintenanceStatus ? "yes" : "no"}">${r.maintenanceStatus ? "Under maintenance" : "No"}</span></td></tr>`).join("") || '<tr><td colspan="4" class="empty">No rooms found.</td></tr>';
}
async function loadGuests() {
  const rows = await api("/guests");
  $("guestsTable").innerHTML = rows.map(g => `<tr><td>${g.guestId}</td><td>${esc(g.name)}</td><td>${esc(g.phone)}</td><td>${esc(g.email)}</td></tr>`).join("") || '<tr><td colspan="4" class="empty">No guests registered.</td></tr>';
}
async function loadStays() {
  const rows = await api("/stays");
  $("staysTable").innerHTML = rows.map(s => `<tr><td>${s.stayId}</td><td>${s.guestId}</td><td>${s.roomId}</td><td>${esc(s.checkIn)}</td><td>${esc(s.checkOut || "—")}</td><td>${s.totalBill == null ? "—" : "₹" + Number(s.totalBill).toFixed(2)}</td><td><span class="pill ${s.checkOut ? "completed" : "occupied"}">${s.checkOut ? "Completed" : "Active"}</span></td></tr>`).join("") || '<tr><td colspan="7" class="empty">No stays recorded.</td></tr>';
}
async function loadMaintenance() {
  const rows = await api("/maintenance");
  $("maintenanceTable").innerHTML = rows.map(m => `<tr><td>${m.maintenanceId}</td><td>${m.roomId}</td><td>${esc(m.reason)}</td><td>${esc(m.startDate)}</td><td>${esc(m.endDate || "—")}</td><td><span class="pill ${m.status === "ACTIVE" ? "active" : "completed"}">${esc(m.status)}</span></td></tr>`).join("") || '<tr><td colspan="6" class="empty">No maintenance records.</td></tr>';
}
async function loadPage(name) {
  try {
    if (name === "dashboard") await loadDashboard();
    if (name === "rooms") await loadRooms();
    if (name === "guests") await loadGuests();
    if (name === "stays") await loadStays();
    if (name === "maintenance") await loadMaintenance();
  } catch (e) { toast("Could not load data: " + e.message, true); }
}
$("guestForm").addEventListener("submit", async e => {
  e.preventDefault();
  try { const d = await post("/guests", e.target); toast("Guest registered successfully. Guest ID: " + d.guestId); e.target.reset(); await loadGuests(); }
  catch (err) { toast(err.message, true); }
});
$("checkinForm").addEventListener("submit", async e => {
  e.preventDefault();
  try { await post("/checkin", e.target); toast("Guest checked in successfully."); e.target.reset(); await loadStays(); await loadDashboard(); }
  catch (err) { toast(err.message, true); }
});
$("checkoutForm").addEventListener("submit", async e => {
  e.preventDefault();
  try { await post("/checkout", e.target); toast("Guest checked out successfully."); e.target.reset(); await loadStays(); await loadDashboard(); }
  catch (err) { toast(err.message, true); }
});
$("maintenanceForm").addEventListener("submit", async e => {
  e.preventDefault();
  try { await post("/maintenance/start", e.target); toast("Maintenance started."); e.target.reset(); await loadMaintenance(); await loadDashboard(); await loadRooms(); }
  catch (err) { toast(err.message, true); }
});
$("completeMaintenanceForm").addEventListener("submit", async e => {
  e.preventDefault();
  try { await post("/maintenance/complete", e.target); toast("Maintenance completed."); e.target.reset(); await loadMaintenance(); await loadDashboard(); await loadRooms(); }
  catch (err) { toast(err.message, true); }
});
$("refreshBtn").addEventListener("click", () => loadPage(document.querySelector(".page.active").id.replace("-page", "")));
$("roomsRefresh").addEventListener("click", () => loadRooms().catch(e => toast(e.message, true)));
$("guestsRefresh").addEventListener("click", () => loadGuests().catch(e => toast(e.message, true)));
$("staysRefresh").addEventListener("click", () => loadStays().catch(e => toast(e.message, true)));
$("maintenanceRefresh").addEventListener("click", () => loadMaintenance().catch(e => toast(e.message, true)));

loadDashboard().catch(() => toast("Backend not connected. Start HotelServer on port 8080.", true));
