#!/usr/bin/env bash
# ==============================================================================
# Modol Connect Production Backend VPS Installer (PHP 8.2 + Nginx + MariaDB)
# Designed for Ubuntu 22.04 / 24.04 LTS on VPS (IP: 173.249.28.110)
# ==============================================================================

set -e

echo ">>> [1/7] Updating system packages..."
export DEBIAN_FRONTEND=noninteractive
apt update && apt upgrade -y

echo ">>> [2/7] Installing Nginx, MariaDB, PHP 8.2, and required extensions..."
apt install -y software-properties-common curl git unzip ufw

add-apt-repository -y ppa:ondrej/php
apt update

apt install -y nginx mariadb-server mariadb-client \
    php8.2 php8.2-fpm php8.2-mysql php8.2-common php8.2-curl \
    php8.2-json php8.2-mbstring php8.2-xml php8.2-zip php8.2-opcache

echo ">>> [3/7] Configuring MariaDB Database..."
systemctl start mariadb
systemctl enable mariadb

# Create Database & User
DB_NAME="modol_connect"
DB_USER="modol_user"
DB_PASS="ModolConnect@2026#Secure"

mariadb -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mariadb -e "CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';"
mariadb -e "GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* TO '${DB_USER}'@'localhost';"
mariadb -e "FLUSH PRIVILEGES;"

echo ">>> Database created: ${DB_NAME} (User: ${DB_USER})"

echo ">>> [4/7] Setting up web directory at /var/www/modol_backend..."
mkdir -p /var/www/modol_backend

# If backend files exist locally in current directory, copy them
if [ -d "./backend" ]; then
    cp -r ./backend/* /var/www/modol_backend/
elif [ -f "./schema.sql" ]; then
    cp -r ./* /var/www/modol_backend/
fi

# Import schema.sql if present
if [ -f "/var/www/modol_backend/schema.sql" ]; then
    echo ">>> Importing schema.sql into ${DB_NAME}..."
    mariadb -u"${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" < /var/www/modol_backend/schema.sql
    echo ">>> Database schema imported successfully!"
fi

chown -R www-data:www-data /var/www/modol_backend
chmod -R 755 /var/www/modol_backend

echo ">>> [5/7] Configuring Nginx VirtualHost..."
cat << 'EOF' > /etc/nginx/sites-available/modol_backend
server {
    listen 80;
    listen [::]:80;
    server_name 173.249.28.110;

    root /var/www/modol_backend;
    index index.php index.html;

    # CORS Headers for Mobile App
    add_header 'Access-Control-Allow-Origin' '*' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With' always;

    location / {
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' '*';
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
            add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With';
            add_header 'Content-Length' 0;
            add_header 'Content-Type' 'text/plain charset=UTF-8';
            return 204;
        }
        try_files $uri $uri/ /index.php?$query_string;
    }

    location ~ \.php$ {
        include snippets/fastcgi-php.conf;
        fastcgi_pass unix:/run/php/php8.2-fpm.sock;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
        include fastcgi_params;
    }

    location ~ /\.ht {
        deny all;
    }
}
EOF

ln -sf /etc/nginx/sites-available/modol_backend /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default

nginx -t
systemctl restart php8.2-fpm
systemctl restart nginx

echo ">>> [6/7] Configuring Firewall (UFW)..."
ufw allow OpenSSH || true
ufw allow 'Nginx Full' || true
ufw --force enable || true

echo "=============================================================================="
echo ">>> [7/7] SETUP COMPLETE!"
echo "Backend API URL: http://173.249.28.110/api/"
echo "Admin Panel:     http://173.249.28.110/admin/"
echo "Agent Portal:    http://173.249.28.110/agent/"
echo "Database:        ${DB_NAME} | User: ${DB_USER}"
echo "=============================================================================="
