# 🅿️ Smart Parking System - Setup & Launch Guide

Welcome to the **Smart Parking System**! This guide will help you get the system up and running in your local environment.

## 🚀 Quick Start

### 1. Prerequisites
- **Java 17+** (Required for the Backend)
- **Node.js 18+** (Required for the Frontend)
- **Maven** (optional, you can use `./mvnw`)

### 2. Start the Backend (Spring Boot)
Open a terminal in the `backend` directory and run:
```bash
./mvnw spring-boot:run
```
> [!NOTE]
> The backend will start on **http://localhost:9999**.
> It uses an in-memory **H2 Database** by default, so no database setup is required for the initial run.

### 3. Start the Frontend (Vite + React)
Open a second terminal in the `frontend` directory and run:
```bash
npm install
npm run dev
```
> [!NOTE]
> The frontend will start on **http://localhost:5173**.
> It is pre-configured to communicate with the backend on port 9999.

## 🛠 Features

- **Dashboard**: Real-time overview of occupancy across all locations.
- **Interactive Map**: View and book parking slots in real-time.
- **Admin Panel**: Control slot status and manage active bookings.
- **Live Simulation**: The system automatically simulates parking activity (vehicles entering/leaving) every 5 seconds.

## 🎨 Design Aesthetics
The system features a **Premium Dark Mode** with:
- **Glassmorphism**: Elegant translucent cards and sidebars.
- **Dynamic Animations**: Smooth transitions and pulse indicators.
- **Responsive Layout**: Optimized for both desktop and mobile devices.

## 🧪 Verification
Once both servers are running:
1. Navigate to `http://localhost:5173`.
2. You should see the **Dashboard** fetching live data from the backend.
3. Go to the **Slot Map**, select a location, and try booking a slot.
4. Verify the booking in the **Admin Panel**.

---
*Built with ❤️ by Antigravity*
