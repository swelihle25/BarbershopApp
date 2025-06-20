#!/bin/bash
docker compose up -d
sleep 10
xdg-open http://localhost:8081/barbershop-api/
