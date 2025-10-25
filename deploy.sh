#!/bin/bash

# Iran Bazaar Deployment Script
# Usage: ./deploy.sh [production|staging]

set -e  # Exit on any error

# Configuration
PROJECT_NAME="iran-bazaar"
TOMCAT_HOME="/opt/tomcat"
TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"
BACKUP_DIR="/opt/iran-bazaar/backups"
LOG_DIR="/opt/iran-bazaar/logs"
UPLOAD_DIR="/opt/iran-bazaar/uploads"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running as root or with sudo
check_permissions() {
    if [[ $EUID -ne 0 ]]; then
        log_error "This script must be run as root or with sudo"
        exit 1
    fi
}

# Create necessary directories
create_directories() {
    log_info "Creating necessary directories..."
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$LOG_DIR" 
    mkdir -p "$UPLOAD_DIR"
    
    # Set ownership to tomcat user
    chown -R tomcat:tomcat "/opt/iran-bazaar"
    chmod -R 755 "/opt/iran-bazaar"
    
    log_success "Directories created successfully"
}

# Build project
build_project() {
    log_info "Building WAR file..."
    
    # Set profile based on parameter
    PROFILE=${1:-production}
    
    # Clean and build
    ./mvnw clean package -DskipTests -Dspring.profiles.active=$PROFILE
    
    if [ $? -eq 0 ]; then
        log_success "WAR file built successfully"
    else
        log_error "Failed to build WAR file"
        exit 1
    fi
}

# Backup existing deployment
backup_deployment() {
    if [ -f "$TOMCAT_WEBAPPS/$PROJECT_NAME.war" ]; then
        log_info "Backing up existing deployment..."
        TIMESTAMP=$(date +%Y%m%d_%H%M%S)
        cp "$TOMCAT_WEBAPPS/$PROJECT_NAME.war" "$BACKUP_DIR/$PROJECT_NAME.war.backup.$TIMESTAMP"
        log_success "Backup created: $PROJECT_NAME.war.backup.$TIMESTAMP"
    fi
}

# Stop Tomcat
stop_tomcat() {
    log_info "Stopping Tomcat..."
    systemctl stop tomcat
    
    # Wait for process to stop
    sleep 5
    
    if systemctl is-active --quiet tomcat; then
        log_warning "Tomcat is still running, forcing stop..."
        systemctl kill tomcat
        sleep 3
    fi
    
    log_success "Tomcat stopped"
}

# Deploy new WAR
deploy_war() {
    log_info "Deploying new WAR file..."
    
    # Remove old deployment
    if [ -f "$TOMCAT_WEBAPPS/$PROJECT_NAME.war" ]; then
        rm -f "$TOMCAT_WEBAPPS/$PROJECT_NAME.war"
    fi
    
    if [ -d "$TOMCAT_WEBAPPS/$PROJECT_NAME" ]; then
        rm -rf "$TOMCAT_WEBAPPS/$PROJECT_NAME"
    fi
    
    # Copy new WAR
    cp "target/$PROJECT_NAME-*.war" "$TOMCAT_WEBAPPS/$PROJECT_NAME.war"
    chown tomcat:tomcat "$TOMCAT_WEBAPPS/$PROJECT_NAME.war"
    
    log_success "WAR file deployed"
}

# Start Tomcat
start_tomcat() {
    log_info "Starting Tomcat..."
    systemctl start tomcat
    
    # Wait for startup
    log_info "Waiting for Tomcat to start..."
    sleep 15
    
    if systemctl is-active --quiet tomcat; then
        log_success "Tomcat started successfully"
    else
        log_error "Failed to start Tomcat"
        systemctl status tomcat
        exit 1
    fi
}

# Check deployment health
check_health() {
    log_info "Checking deployment health..."
    
    # Wait a bit more for full initialization
    sleep 10
    
    # Check if application is responding
    HEALTH_URL="http://localhost:8080/$PROJECT_NAME/actuator/health"
    HTTP_STATUS=$(curl -o /dev/null -s -w "%{http_code}" "$HEALTH_URL" || echo "000")
    
    if [ "$HTTP_STATUS" = "200" ]; then
        log_success "Application is healthy and responding"
    else
        log_warning "Health check failed (HTTP $HTTP_STATUS), but Tomcat is running"
        log_info "Application might still be initializing..."
    fi
    
    # Show recent logs
    log_info "Recent application logs:"
    tail -20 "$LOG_DIR/application.log" 2>/dev/null || echo "No application logs found yet"
}

# Rollback function
rollback() {
    log_warning "Rolling back to previous deployment..."
    
    LATEST_BACKUP=$(ls -t "$BACKUP_DIR"/$PROJECT_NAME.war.backup.* 2>/dev/null | head -1)
    
    if [ -n "$LATEST_BACKUP" ]; then
        stop_tomcat
        rm -f "$TOMCAT_WEBAPPS/$PROJECT_NAME.war"
        rm -rf "$TOMCAT_WEBAPPS/$PROJECT_NAME"
        cp "$LATEST_BACKUP" "$TOMCAT_WEBAPPS/$PROJECT_NAME.war"
        chown tomcat:tomcat "$TOMCAT_WEBAPPS/$PROJECT_NAME.war"
        start_tomcat
        log_success "Rollback completed"
    else
        log_error "No backup found for rollback"
        exit 1
    fi
}

# Main deployment function
deploy() {
    local PROFILE=${1:-production}
    
    log_info "🚀 Starting $PROJECT_NAME deployment (Profile: $PROFILE)..."
    echo "=================================="
    
    check_permissions
    create_directories
    build_project $PROFILE
    backup_deployment
    stop_tomcat
    deploy_war
    start_tomcat
    check_health
    
    echo "=================================="
    log_success "✅ Deployment completed successfully!"
    log_info "🌐 Application URL: http://localhost:8080/$PROJECT_NAME"
    log_info "📚 Swagger UI: http://localhost:8080/$PROJECT_NAME/swagger-ui.html"
    log_info "📋 Admin Panel: http://localhost:8080/$PROJECT_NAME/admin"
    log_info "📊 Logs: tail -f $LOG_DIR/application.log"
}

# Script usage
usage() {
    echo "Usage: $0 [COMMAND] [PROFILE]"
    echo ""
    echo "Commands:"
    echo "  deploy [production|staging]  - Deploy application (default: production)"
    echo "  rollback                     - Rollback to previous deployment"
    echo "  logs                         - Show application logs"
    echo "  status                       - Show Tomcat and application status"
    echo "  help                         - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 deploy production"
    echo "  $0 rollback"
    echo "  $0 logs"
}

# Handle commands
case "${1:-deploy}" in
    "deploy")
        deploy ${2:-production}
        ;;
    "rollback")
        rollback
        ;;
    "logs")
        tail -f "$LOG_DIR/application.log" 2>/dev/null || echo "No logs found"
        ;;
    "status")
        echo "Tomcat Status:"
        systemctl status tomcat --no-pager
        echo ""
        echo "Application Health:"
        curl -s "http://localhost:8080/$PROJECT_NAME/actuator/health" || echo "Application not responding"
        ;;
    "help"|"-h"|"--help")
        usage
        ;;
    *)
        log_error "Unknown command: $1"
        usage
        exit 1
        ;;
esac
