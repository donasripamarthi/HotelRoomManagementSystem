# StayEase – Hotel Management System

A hotel-management frontend built with HTML, CSS and JavaScript. It is designed to connect to a Core Java/JDBC/MySQL backend.

## Project files

- `index.html` – page structure and forms
- `style.css` – friendly small-hotel theme, responsive layout and animations
- `script.js` – navigation, API calls, dashboard data and form handling

## Run the frontend

1. Keep all three frontend files (`index.html`, `style.css`, `script.js`) in the same folder.
2. Start your Java backend and MySQL database.
3. Open the folder in VS Code.
4. Run `index.html` with the Live Server extension, or serve the folder with any local web server.
5. Open the browser developer console if you need to inspect connection errors.

## Backend configuration

At the top of `script.js`, update:

```js
const API_BASE = "http://localhost:8080/api";
```

The frontend currently expects these REST routes:

| Feature | Method | Route |
|---|---|---|
| List rooms | GET | `/api/rooms` |
| List guests | GET | `/api/guests` |
| Register guest | POST | `/api/guests` |
| List stays | GET | `/api/stays` |
| Check-in | POST | `/api/stays/checkin` |
| Check-out | POST | `/api/stays/checkout` |
| List maintenance | GET | `/api/maintenance` |
| Start maintenance | POST | `/api/maintenance` |
| Complete maintenance | POST | `/api/maintenance/{id}/complete` |

**Important:** These are assumed example API routes. Your Java backend must expose matching routes and JSON field names, or you must change `API_PATHS` and the data-field mappings in `script.js` to match your actual Java controllers.

## Expected JSON fields

The frontend uses these common fields:

- Room: `roomId`, `roomNumber`, `roomType`, `dailyPrice`, `maintenanceStatus`
- Guest: `guestId`, `name`, `phone`, `email`
- Stay: `stayId`, `guestId`, `roomId`, `checkIn`, `checkOut`, `bill`, `status`
- Maintenance: `maintenanceId`, `roomId`, `reason`, `startDate`, `endDate`, `status`

The browser must be allowed to call the backend. If the frontend and backend use different ports, configure CORS in your Java backend for the frontend origin.

## Design features

- Approachable navy, warm cream and soft-gold small-hotel theme
- A welcoming, modest hotel-room photograph in the dashboard and rooms section
- Responsive sidebar and content layout
- Button hover and press animations
- Animated double doors when opening the Rooms page
- Toast messages for API success and errors
