# FlowSense MPC - Data-Driven Predictive Control System

A real-time Model Predictive Control (MPC) system for water quality monitoring in wastewater treatment plants. This full-stack application uses Recursive Least Squares (RLS) for system identification and implements predictive control algorithms to maintain optimal water quality parameters.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Control Algorithm](#control-algorithm)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Deployment](#deployment)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

## 🎯 Overview

FlowSense MPC is designed for predictive control of linear processes in water treatment facilities. The system monitors four critical water quality parameters:

- **Turbidity** (NTU) - Water clarity measurement
- **Flow Rate** (m³/hr) - Volume flow measurement
- **Temperature** (°C) - Thermal monitoring
- **Dissolved Oxygen** (mg/L) - DO level tracking

The application combines real-time data ingestion, automatic model training using RLS, and MPC-based control computation to provide actionable control instructions.

## ✨ Features

### Core Functionality

- **Multi-Parameter Monitoring**: Supports 4 separate sensor types with independent data streams
- **Automatic Model Training**: RLS algorithm updates system models automatically when new measurements arrive
- **Predictive Control**: Computes optimal control inputs using Model Predictive Control
- **Real-Time Data Visualization**: Display recent measurements with formatted timestamps
- **Color-Coded Instructions**: Actionable control guidance with visual indicators
- **Closed-Loop Simulation**: Automatic control value storage for continuous operation

### Technical Features

- Separate database tables per sensor type (data isolation)
- Independent RLS models for each sensor
- RESTful API architecture
- Responsive web interface
- Environment variable configuration for deployment

## 🛠️ Technology Stack

### Backend

- **Java 21** - Core programming language
- **Spring Boot 4.0.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Hibernate 7.1.8** - ORM framework
- **Maven** - Dependency management

### Database

- **MySQL 8.0** - Relational database
- Separate tables: `turbidity_measurements`, `flow_measurements`, `temperature_measurements`, `do_measurements`

### Frontend

- **HTML5** - Structure
- **CSS3** - Styling with Flexbox
- **Vanilla JavaScript** - ES6+ with async/await
- **Fetch API** - HTTP client

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (Browser)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │ Send Data    │  │ View Data    │  │ Compute Control │   │
│  └──────┬───────┘  └──────┬───────┘  └────────┬────────┘   │
└─────────┼──────────────────┼───────────────────┼────────────┘
          │                  │                   │
          ▼                  ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                   REST API (Spring Boot)                     │
│  ┌─────────────────┐              ┌────────────────────┐    │
│  │ DataController  │              │ ControlController  │    │
│  │  /api/data/*    │              │  /api/control/*    │    │
│  └────────┬────────┘              └──────────┬─────────┘    │
│           │                                   │              │
│  ┌────────▼──────────┐    ┌──────────────────▼──────┐      │
│  │  IngestService    │    │    ControlService        │      │
│  │  - Route data     │    │    - Predict y(k+1)      │      │
│  │  - Save to DB     │    │    - Compute u(k)        │      │
│  │  - Train model    │    │    - Get latest state    │      │
│  └────────┬──────────┘    └──────────────────┬───────┘      │
│           │                                   │              │
│  ┌────────▼────────────────────────────────┬─┘              │
│  │           ModelService                  │                │
│  │  - HashMap<SensorId, RLSModel>          │                │
│  │  - Update θ = [θ₀, θ₁, θ₂]              │                │
│  │  - 20-point sliding window               │                │
│  └────────────────┬────────────────────────┘                │
└───────────────────┼─────────────────────────────────────────┘
                    │
          ┌─────────▼─────────────┐
          │   MySQL Database      │
          │  ┌──────────────────┐ │
          │  │ turbidity_meas.  │ │
          │  │ flow_meas.       │ │
          │  │ temperature_meas.│ │
          │  │ do_meas.         │ │
          │  └──────────────────┘ │
          └───────────────────────┘
```

## 📐 Control Algorithm

### System Model (ARX Structure)

```
y(k) = θ₀·y(k-1) + θ₁·y(k-2) + θ₂·u(k-1)
```

Where:

- `y(k)` = Current output (measured value)
- `u(k)` = Control input
- `θ = [θ₀, θ₁, θ₂]` = Model parameters

### Recursive Least Squares (RLS)

- **Window Size**: 20 most recent measurements
- **Regression Vector**: `φ(k) = [y(k-1), y(k-2), u(k-1)]`
- **Parameter Update**: `θ = (ΦᵀΦ)⁻¹Φᵀy`

### Predictive Control Law

```
ŷ(k+1) = θ₀·y(k) + θ₁·y(k-1) + θ₂·u(k)
u(k) = Kₚ·(r - ŷ(k+1))
```

Where:

- `r` = Reference setpoint
- `Kₚ = 0.8` = Proportional gain
- `ŷ(k+1)` = Predicted output

### Control Instructions

- **|u(k)| < 0.5**: ✅ Maintain current levels (GREEN)
- **u(k) > 0**: 🟢 Increase parameter value (GREEN)
- **u(k) < 0**: 🟠 Decrease parameter value (ORANGE)

## 💻 Installation

### Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git

### Local Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/FlowSenseMPC.git
   cd FlowSenseMPC
   ```

2. **Configure MySQL Database**

   ```sql
   mysql -u root -p
   CREATE DATABASE flowsense;
   ```

3. **Update application.properties** (if needed)

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/flowsense
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Build the project**

   ```bash
   ./mvnw clean package
   ```

5. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

   Or:

   ```bash
   java -jar target/FlowSenseMPC-0.0.1-SNAPSHOT.jar
   ```

6. **Access the application**
   - Open browser: `http://localhost:8080`

## 🚀 Usage

### 1. Send Measurement Data

- Select sensor type (Turbidity/Flow/Temperature/Dissolved Oxygen)
- Enter sensor ID (e.g., `turbidity_sensor_1`)
- Enter measured value
- Click **Send Measurement**
- Model automatically trains with new data

### 2. View Recent Measurements

- Select sensor to view
- Click **Load Measurements**
- See last 20 data points with timestamps

### 3. Compute Control Action

- Select sensor for control
- Click **Load Latest State** (auto-populates y(k), y(k-1), u(k-1))
- Enter desired reference value
- Click **Compute Control**
- View predicted output and control instruction

### Example Workflow

```
1. Send measurement: turbidity_sensor_1, value=15 NTU
2. Send measurement: turbidity_sensor_1, value=18 NTU
3. Load latest state for turbidity_sensor_1
4. Set reference=12 NTU
5. Compute → System predicts y(k+1)=17.5, recommends u(k)=-4.4
6. Instruction: "DECREASE turbidity by 4.4 units" (ORANGE)
```

## 🔌 API Endpoints

### Data Ingestion

**POST** `/api/data/measure`

```json
{
  "sensorId": "turbidity_sensor_1",
  "value": 15.5,
  "timestamp": "2025-12-24T10:30:00"
}
```

**GET** `/api/data/recent/{sensorId}`

```json
[
  {
    "sensorId": "turbidity_sensor_1",
    "value": 15.5,
    "timestamp": "2025-12-24  10:30:00"
  }
]
```

### Control

**POST** `/api/control/compute`

```json
{
  "reference": 12.0,
  "yk": 15.5,
  "yk1": 14.2,
  "uk": 0.0,
  "sensorId": "turbidity_sensor_1"
}
```

**Response**:

```json
{
  "predicted": 14.8,
  "control": -2.24
}
```

**GET** `/api/control/latest-state/{sensorId}`

```json
{
  "yk": 15.5,
  "yk1": 14.2,
  "uk": -2.24
}
```

## 🌐 Deployment

### Railway Deployment

1. **Push to GitHub**

   ```bash
   git push origin main
   ```

2. **Create Railway Project**

   - Go to [railway.app](https://railway.app)
   - New Project → Deploy from GitHub
   - Select FlowSenseMPC repository

3. **Add MySQL Database**

   - Click **+ New** → Database → MySQL
   - Railway auto-generates credentials

4. **Link Variables**

   - Select your web service
   - Variables tab → Add Reference
   - Link: `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`

5. **Configure Build**

   - Build Command: `./mvnw clean package`
   - Start Command: `java -jar target/FlowSenseMPC-0.0.1-SNAPSHOT.jar`

6. **Generate Domain**
   - Settings → Networking → Generate Domain
   - Access via: `https://your-app.railway.app`

### Environment Variables (Production)

```
MYSQLHOST=${{MySQL.HOST}}
MYSQLPORT=${{MySQL.PORT}}
MYSQLDATABASE=${{MySQL.MYSQLDBNAME}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
PORT=8080
```

## 📁 Project Structure

```
FlowSenseMPC/
├── src/
│   ├── main/
│   │   ├── java/com/example/FlowSenseMPC/
│   │   │   ├── controller/
│   │   │   │   ├── ControlController.java    # Control endpoints
│   │   │   │   └── DataController.java       # Data ingestion
│   │   │   ├── model/
│   │   │   │   ├── TurbidityMeasurement.java
│   │   │   │   ├── FlowMeasurement.java
│   │   │   │   ├── TemperatureMeasurement.java
│   │   │   │   ├── DOMeasurement.java
│   │   │   │   ├── MeasurementDto.java
│   │   │   │   └── RLSModel.java             # RLS algorithm
│   │   │   ├── repository/
│   │   │   │   ├── TurbidityRepository.java
│   │   │   │   ├── FlowRepository.java
│   │   │   │   ├── TemperatureRepository.java
│   │   │   │   └── DORepository.java
│   │   │   ├── service/
│   │   │   │   ├── ControlService.java       # MPC logic
│   │   │   │   ├── IngestService.java        # Data routing
│   │   │   │   └── ModelService.java         # RLS model manager
│   │   │   └── FlowSenseMpcApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html                # Main UI
│   │           ├── app.js                    # Frontend logic
│   │           ├── styles.css                # Styling
│   │           └── toplogo.png               # Logo
│   └── test/
│       └── java/com/example/FlowSenseMPC/
│           └── FlowSenseMpcApplicationTests.java
├── pom.xml                                   # Maven dependencies
├── mvnw                                      # Maven wrapper (Linux/Mac)
├── mvnw.cmd                                  # Maven wrapper (Windows)
└── README.md                                 # This file
```

## 🧪 Testing

Run unit tests:

```bash
./mvnw test
```

## 🔧 Configuration

### Database Tables Schema

**turbidity_measurements**

```sql
CREATE TABLE turbidity_measurements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sensor_id VARCHAR(255) NOT NULL,
  value DOUBLE NOT NULL,
  control_input DOUBLE,
  timestamp DATETIME NOT NULL
);
```

Similar structure for: `flow_measurements`, `temperature_measurements`, `do_measurements`

### Model Parameters

- **RLS Window**: 20 measurements
- **Control Gain (Kp)**: 0.8
- **Update Mode**: Automatic on new data

## 📊 Performance Considerations

- **Response Time**: < 100ms for control computation
- **Data Retention**: Unlimited (all measurements stored)
- **Concurrent Users**: Supports multiple simultaneous connections
- **Model Update**: O(n³) where n=20 (negligible overhead)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Kathirvel** - Initial work

## 🙏 Acknowledgments

- Based on Model Predictive Control theory
- Recursive Least Squares algorithm implementation
- Spring Boot framework for rapid development
- Railway platform for seamless deployment

## 📞 Support

For issues, questions, or contributions:

- Open an issue on GitHub
- Contact: [your-email@example.com]

---

**Project Status**: ✅ Active Development | 🚀 Production Ready

Made with ❤️ for water treatment optimization
