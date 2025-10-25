#!/bin/bash

# Iran Bazaar Server Setup Script
# This script prepares the server for deployment

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Check if running as root
if [[ $EUID -ne 0 ]]; then
    log_error "This script must be run as root"
    exit 1
fi

log_info "🚀 Setting up Iran Bazaar deployment environment..."

# Update system
log_info "Updating system packages..."
apt update && apt upgrade -y

# Install required packages
log_info "Installing required packages..."
apt install -y openjdk-17-jdk postgresql postgresql-contrib nginx curl wget unzip

# Create tomcat user
log_info "Creating tomcat user..."
if ! id "tomcat" &>/dev/null; then
    useradd -m -U -d /opt/tomcat -s /bin/false tomcat
else
    log_info "Tomcat user already exists"
fi

# Download and install Tomcat
TOMCAT_VERSION="10.1.28"
log_info "Installing Tomcat $TOMCAT_VERSION..."

if [ ! -d "/opt/tomcat" ]; then
    cd /tmp
    wget https://downloads.apache.org/tomcat/tomcat-10/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz
    tar -xf apache-tomcat-$TOMCAT_VERSION.tar.gz
    mv apache-tomcat-$TOMCAT_VERSION /opt/tomcat
    chown -R tomcat:tomcat /opt/tomcat
    chmod +x /opt/tomcat/bin/*.sh
else
    log_info "Tomcat already installed"
fi

# Create application directories
log_info "Creating application directories..."
mkdir -p /opt/iran-bazaar/{uploads,logs,backups,temp}
chown -R tomcat:tomcat /opt/iran-bazaar
chmod -R 755 /opt/iran-bazaar

# Setup PostgreSQL
log_info "Setting up PostgreSQL..."
sudo -u postgres psql -c "CREATE DATABASE IF NOT EXISTS bazaar;" 2>/dev/null || true
sudo -u postgres psql -c "ALTER USER postgres PASSWORD '5Azar1360';" 2>/dev/null || true

# Install systemd service
log_info "Installing Tomcat systemd service..."
if [ -f "tomcat.service" ]; then
    cp tomcat.service /etc/systemd/system/
    systemctl daemon-reload
    systemctl enable tomcat
    log_success "Tomcat service installed and enabled"
else
    log_warning "tomcat.service file not found, skipping service installation"
fi

# Copy Tomcat context configuration
log_info "Installing Tomcat context configuration..."
if [ -f "context.xml" ]; then
    cp context.xml /opt/tomcat/conf/
    chown tomcat:tomcat /opt/tomcat/conf/context.xml
    log_success "Context configuration installed"
fi

# Configure firewall
log_info "Configuring firewall..."
ufw allow 22
ufw allow 80
ufw allow 443
ufw allow 8080
ufw --force enable

# Setup nginx (optional reverse proxy)
log_info "Setting up nginx reverse proxy..."
cat > /etc/nginx/sites-available/iran-bazaar << 'EOF'
server {
    listen 80;
    server_name _;
    
    location / {
        proxy_pass http://localhost:8080/iran-bazaar;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # Static files
    location /iran-bazaar/css/ {
        proxy_pass http://localhost:8080/iran-bazaar/css/;
    }
    
    location /iran-bazaar/js/ {
        proxy_pass http://localhost:8080/iran-bazaar/js/;
    }
    
    location /iran-bazaar/images/ {
        proxy_pass http://localhost:8080/iran-bazaar/images/;
    }
}
EOF

ln -sf /etc/nginx/sites-available/iran-bazaar /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl restart nginx

# Set up log rotation
log_info "Setting up log rotation..."
cat > /etc/logrotate.d/iran-bazaar << 'EOF'
/opt/iran-bazaar/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 0644 tomcat tomcat
    postrotate
        systemctl reload tomcat
    endscript
}
EOF

# Create maintenance script
log_info "Creating maintenance scripts..."
cat > /opt/iran-bazaar/maintenance.sh << 'EOF'
#!/bin/bash

# Iran Bazaar Maintenance Script

log_info() { echo -e "\033[0;34m[INFO]\033[0m $1"; }

log_info "Running Iran Bazaar maintenance..."

# Clean up old logs
find /opt/iran-bazaar/logs/ -name "*.log.*" -mtime +30 -delete

# Clean up old backups
find /opt/iran-bazaar/backups/ -name "*.backup.*" -mtime +7 -delete

# Clean up temp files
find /opt/iran-bazaar/temp/ -type f -mtime +1 -delete

# Database maintenance (optional)
# sudo -u postgres vacuumdb bazaar

log_info "Maintenance completed"
EOF

chmod +x /opt/iran-bazaar/maintenance.sh

# Set up cron job for maintenance
echo "0 2 * * * root /opt/iran-bazaar/maintenance.sh >> /opt/iran-bazaar/logs/maintenance.log 2>&1" > /etc/cron.d/iran-bazaar-maintenance

# Create deployment info
cat > /opt/iran-bazaar/deployment-info.txt << EOF
Iran Bazaar Deployment Information
=================================

Deployment Date: $(date)
Tomcat Home: /opt/tomcat
Application Directory: /opt/iran-bazaar
Upload Directory: /opt/iran-bazaar/uploads
Log Directory: /opt/iran-bazaar/logs
Backup Directory: /opt/iran-bazaar/backups

Application URLs:
- Main Application: http://localhost:8080/iran-bazaar
- Through Nginx: http://localhost/
- Swagger UI: http://localhost:8080/iran-bazaar/swagger-ui.html
- Admin Panel: http://localhost:8080/iran-bazaar/admin

Database:
- PostgreSQL on localhost:5432
- Database: bazaar
- User: postgres

Services:
- Tomcat: systemctl {start|stop|restart|status} tomcat
- Nginx: systemctl {start|stop|restart|status} nginx
- PostgreSQL: systemctl {start|stop|restart|status} postgresql

Deployment Commands:
- Deploy: sudo ./deploy.sh
- Rollback: sudo ./deploy.sh rollback  
- Logs: sudo ./deploy.sh logs
- Status: sudo ./deploy.sh status

Log Files:
- Application: /opt/iran-bazaar/logs/application.log
- Tomcat: /opt/tomcat/logs/catalina.out
- Nginx: /var/log/nginx/access.log

EOF

log_success "✅ Server setup completed successfully!"
echo ""
log_info "📋 Deployment information saved to: /opt/iran-bazaar/deployment-info.txt"
log_info "🔧 To deploy the application, run: sudo ./deploy.sh"
log_info "📊 To monitor logs: sudo ./deploy.sh logs"
echo ""
log_warning "Don't forget to:"
log_warning "1. Update database password in production"
log_warning "2. Configure email settings"  
log_warning "3. Set up SSL certificate for production"
log_warning "4. Configure backup strategy"
