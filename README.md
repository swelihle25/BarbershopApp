# 💈 Barbershop Queue Management System

A comprehensive queue and customer management platform for barbershops, built with modern technologies to streamline operations. This system enables barbershops to manage real-time queues, track services, and monitor performance metrics across multiple locations efficiently.

## 🚀 Features

- **Real-time Queue Management** - Live queue updates and customer tracking
- **Service Management** - Track and manage various barbershop services
- **Performance Analytics** - Monitor shop performance and customer metrics
- **Multi-location Support** - Manage multiple barbershop locations
- **Redis Caching** - Fast data retrieval with intelligent caching
- **Responsive Web Interface** - Modern HTML5/CSS/JavaScript frontend

## 📦 Tech Stack

### Backend
- **Java 17** - Latest LTS version
- **Spring Boot 3** - Modern Java framework
- **PostgreSQL** - Reliable relational database
- **Redis** - High-performance in-memory caching
- **Liquibase** - Database version control and migrations

### Frontend
- **HTML5** - Modern markup
- **CSS3** - Responsive styling
- **JavaScript** - Interactive user interface

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 🏗️ Architecture

The system consists of three main Docker containers:

| Service | Port | Description |
|---------|------|-------------|
| `barbershop-app` | 8081 | Spring Boot application server |
| `postgres` | 5432 | PostgreSQL database |
| `barbershop-redis` | 6379 | Redis cache server |

## 🚀 Getting Started

### Prerequisites

- Docker and Docker Compose installed
- Git for cloning the repository
- Linux/Ubuntu environment (or Windows with WSL)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/swelihle25/BarbershopApp.git
   cd "Barbershop Queue Management System"
   ```

2. **Quick Start (Ubuntu/Linux)**
   ```bash
   chmod +x start-barbershop.sh
   ./start-barbershop.sh
   ```

3. **Manual Docker Setup**
   ```bash
   docker compose up --build -d
   ```

4. **Access the Application**
   ```bash
   # Open in browser
   xdg-open http://localhost:8081/barbershop-api/
   
   # Or visit manually
   http://localhost:8081/barbershop-api/
   ```

## 🧠 Redis Caching Strategy

Redis is implemented to optimize performance and reduce database load through intelligent caching:

### Cached Components
- **Services List** - All available barbershop services
- **Staff Members** - Barber and staff information
- **Shop Locations** - Multi-location data (if applicable)
- **Queue Status** - Real-time queue information

### Cache Configuration
- **TTL (Time To Live)**: 10 minutes (600,000ms)
- **Invalidation**: Automatic cache clearing on data mutations
- **Strategy**: Write-through caching for consistency

### Cache Management
The cache is automatically invalidated when:
- New data is created
- Existing data is updated
- Data is deleted
- TTL expires

## 📊 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/queue` | Get current queue status |
| POST | `/api/queue/add` | Add customer to queue |
| DELETE | `/api/queue/{id}` | Remove customer from queue |
| GET | `/api/services` | List all services |
| GET | `/api/staff` | List all staff members |

## 🔧 Development

### Running in Development Mode

```bash
# Start only database and redis
docker compose up postgres barbershop-redis -d

# Run Spring Boot application locally
./mvnw spring-boot:run
```

### Database Migrations

Liquibase handles database schema changes automatically:

```bash
# Check migration status
./mvnw liquibase:status

# Apply pending migrations
./mvnw liquibase:update
```

## 📝 Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/           # Java source code
│   │   ├── resources/      # Configuration files
│   │   └── webapp/         # Static web assets
│   └── test/               # Test files
├── docker-compose.yml      # Docker services configuration
├── Dockerfile             # Application container setup
├── start-barbershop.sh    # Quick start script
└── README.md              # This file
```

## 🛠️ Troubleshooting

### Common Issues

**Port already in use**
```bash
# Check what's using port 8081
sudo lsof -i :8081
# Kill the process or change port in docker-compose.yml
```

**Database connection failed**
```bash
# Check if PostgreSQL container is running
docker ps
# Check container logs
docker logs barbershop-postgres
```

**Redis connection issues**
```bash
# Verify Redis container status
docker logs barbershop-redis
# Test Redis connection
docker exec -it barbershop-redis redis-cli ping
```

## 👨🏽‍💻 Maintainer

**Swelihle Khuzwayo**
- GitHub: [@swelihle25](https://github.com/swelihle25)
- Email: [Contact via email](swelihlekhuzwayo6@gmail.com)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- PostgreSQL team for robust database system
- Redis team for high-performance caching solution
- Docker team for containerization platform

---

⭐ **Star this repository if you find it helpful!**