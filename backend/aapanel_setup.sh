#!/usr/bin/env bash
# ==============================================================================
# aaPanel / VPS Setup Helper for /www/wwwroot/173.249.28.110
# ==============================================================================

set -e

TARGET_DIR="/www/wwwroot/173.249.28.110"

echo ">>> [1/3] Checking target directory: ${TARGET_DIR}..."
if [ ! -d "${TARGET_DIR}" ]; then
    echo "Directory ${TARGET_DIR} not found. Creating it..."
    mkdir -p "${TARGET_DIR}"
fi

echo ">>> [2/3] Setting permissions for www user..."
chown -R www:www "${TARGET_DIR}" 2>/dev/null || chown -R www-data:www-data "${TARGET_DIR}" 2>/dev/null || true
chmod -R 755 "${TARGET_DIR}"

echo ">>> [3/3] Checking if database schema needs to be imported..."
if [ -f "${TARGET_DIR}/schema.sql" ]; then
    echo "Found schema.sql in ${TARGET_DIR}."
    echo "You can import it in aaPanel Database tab, or via terminal using:"
    echo "mysql -u [your_db_user] -p [your_db_name] < ${TARGET_DIR}/schema.sql"
fi

echo "All files in ${TARGET_DIR} are verified and ready."
