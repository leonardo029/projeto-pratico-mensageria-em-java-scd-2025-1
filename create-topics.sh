#!/bin/bash

echo "Creating Kafka topics..."

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 30

# Create orders topic
docker exec kafka kafka-topics --create --topic orders --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# Create inventory-events topic
docker exec kafka kafka-topics --create --topic inventory-events --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# List topics to verify creation
echo "Created topics:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

echo "Topics created successfully!" 