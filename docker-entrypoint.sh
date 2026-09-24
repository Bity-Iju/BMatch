#!/bin/sh
set -eu

: "${SUPABASE_URL:?Set SUPABASE_URL in Render environment variables}"
: "${SUPABASE_ANON_KEY:?Set SUPABASE_ANON_KEY in Render environment variables}"

PORT="${PORT:-10000}"
sed -i -E "s/^Listen [0-9]+/Listen ${PORT}/" /etc/apache2/ports.conf
sed -i -E "s/:80>/:${PORT}>/g" /etc/apache2/sites-available/000-default.conf

cat > /var/www/html/config.php <<PHP
<?php
return [
    'supabase_url' => getenv('SUPABASE_URL'),
    'supabase_anon_key' => getenv('SUPABASE_ANON_KEY'),
];
PHP

exec "$@"
