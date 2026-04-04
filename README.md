# SDR Security API Verification System

## 🔹 Overview
This project is a security-focused system designed to verify SDR (Software Defined Radio) devices using an API-based authentication mechanism. It ensures that only authorized devices can access the system and logs all verification attempts for monitoring purposes.

---

## 🔹 Features
- SDR device authentication using API
- Verification of Device ID and API Key
- Authorization status (AUTHORIZED / REJECTED)
- Admin dashboard for monitoring verification logs
- Role-based access (Admin / User)
- Real-time logging of verification attempts
- Docker-based backend deployment

---

## 🔹 Use Case (Defence Context)
This system simulates how SDR devices in defence environments are authenticated before accessing secure communication systems. Unauthorized devices are rejected, and all attempts are logged for security monitoring.

---

## 🔹 Tech Stack
- Frontend: Angular
- Backend: Spring Boot
- Database: H2
- Deployment: Docker

---

## 🔹 Project Structure
backend → Spring Boot API  
frontend → Angular UI  

---

## 🔹 How to Run

### Backend
cd backend  
./mvnw spring-boot:run  

### Frontend
cd frontend  
npm install  
ng serve  

Open: http://localhost:4200

---

## 🔹 Login Credentials

Admin:  
username: admin  
password: admin123  

User:  
username: user  
password: user123  

---

## 🔹 Docker (Backend)
cd backend  
docker build -t sdr-backend .  
docker run -p 8080:8080 sdr-backend  

---

## 🔹 Author
Pallak Khullar
