# StayEase - Hotel Room Management Frontend

## Requirements
- Java backend running at `http://localhost:8080`
- MySQL database and schema already configured
- A modern browser

## Run
1. Start MySQL.
2. Start the Java backend from the `backend` directory:
   `java -cp "out;lib/*" controller.HotelServer`
3. Open this `frontend` folder in VS Code.
4. Right-click `index.html` and choose **Open with Live Server**.
   Alternatively, open `index.html` directly in a browser.

The frontend calls `http://localhost:8080/api` and sends POST data as
`application/x-www-form-urlencoded`, matching the Java controller.

## Important
This is a hackathon/demo frontend. Before production use, add authentication,
authorization, stronger input validation, restricted CORS origins, and safer
handling of guest personal information.
