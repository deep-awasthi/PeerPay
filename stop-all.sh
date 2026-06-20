#!/usr/bin/env bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}Stopping all PeerPay microservices...${NC}"

# Read PIDs and kill them
if [ -f logs/services.pids ]; then
    while read -r pid; do
        if ps -p "$pid" > /dev/null; then
            echo "Stopping service process: $pid"
            kill "$pid" || kill -9 "$pid"
        fi
    done < logs/services.pids
    rm -f logs/services.pids
    echo -e "${GREEN}All background java services stopped.${NC}"
else
    # Fallback to pgrep
    pids=$(pgrep -f "peerpay-.*-1.0.0-SNAPSHOT.jar" || true)
    if [ -n "$pids" ]; then
        echo "Killing processes: $pids"
        kill -9 $pids || true
        echo -e "${GREEN}All background java services stopped.${NC}"
    else
        echo -e "${YELLOW}No running services found.${NC}"
    fi
fi

# Ask if they want to shut down docker containers
read -p "Do you also want to stop Docker containers? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Stopping Docker containers...${NC}"
    docker-compose down
    echo -e "${GREEN}Docker containers stopped.${NC}"
fi
