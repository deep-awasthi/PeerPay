#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=================================================================${NC}"
echo -e "${BLUE}                     Starting PeerPay Platform                   ${NC}"
echo -e "${BLUE}=================================================================${NC}"

# Create logs directory if not exists
mkdir -p logs

# Step 1: Ensure Docker containers are running
echo -e "${YELLOW}[1/4] Starting Docker infrastructure (Postgres, Redis, Kafka, ELK)...${NC}"
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}Error: Docker daemon is not running. Please start Docker Desktop first.${NC}"
    exit 1
fi
docker-compose up -d
echo -e "${GREEN}Docker infrastructure is up and running.${NC}"

# Step 2: Build the project modules
echo -e "${YELLOW}[2/4] Compiling and packaging Maven modules...${NC}"
mvn clean install -DskipTests
echo -e "${GREEN}Build succeeded!${NC}"

# Step 3: Stop any existing services running from previous sessions
echo -e "${YELLOW}[3/4] Checking and stopping previously running PeerPay services...${NC}"
pids=$(pgrep -f "peerpay-.*-1.0.0-SNAPSHOT.jar" || true)
if [ -n "$pids" ]; then
    echo -e "${YELLOW}Killing existing processes: $pids${NC}"
    kill -9 $pids || true
fi

# Helper function to start a service in background
start_service() {
    local name=$1
    local jar_path=$2
    local port=$3
    
    echo -e "Starting ${BLUE}${name}${NC} on port ${YELLOW}${port}${NC}..."
    nohup java -jar "$jar_path" > "logs/${name}.log" 2>&1 &
    
    # Save PID
    echo $! >> logs/services.pids
}

# Clear previous PIDs file
rm -f logs/services.pids

# Step 4: Start services in background
echo -e "${YELLOW}[4/4] Launching microservices in the background...${NC}"

# Start databases/services with slight delays to ensure smooth startup sequence
start_service "user-service" "user-service/target/user-service-1.0.0-SNAPSHOT.jar" 8081
sleep 2

start_service "payment-service" "payment-service/target/payment-service-1.0.0-SNAPSHOT.jar" 8082
sleep 2

start_service "ledger-service" "ledger-service/target/ledger-service-1.0.0-SNAPSHOT.jar" 8083
sleep 1

start_service "notification-service" "notification-service/target/notification-service-1.0.0-SNAPSHOT.jar" 8084
sleep 1

start_service "reconciliation-service" "reconciliation-service/target/reconciliation-service-1.0.0-SNAPSHOT.jar" 8085
sleep 1

start_service "api-gateway" "api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar" 8080

echo -e "${BLUE}=================================================================${NC}"
echo -e "${GREEN}🎉 All PeerPay microservices launched successfully!${NC}"
echo -e "Logs are streaming to the ${YELLOW}logs/${NC} directory:"
echo -e "  - Gateway:  ${BLUE}logs/api-gateway.log${NC}"
echo -e "  - User:     ${BLUE}logs/user-service.log${NC}"
echo -e "  - Payment:  ${BLUE}logs/payment-service.log${NC}"
echo -e "  - Ledger:   ${BLUE}logs/ledger-service.log${NC}"
echo -e "To stop all services, run: ${YELLOW}./stop-all.sh${NC}"
echo -e "${BLUE}=================================================================${NC}"
