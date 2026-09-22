FROM php:8.2-apache

# 1. Install system dependencies required for PHP extensions
RUN apt-get update && apt-get install -y \
    libpng-dev \
    libjpeg-dev \
    libfreetype6-dev \
    libzip-dev \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# 2. Configure and install PHP extensions (PDO, MySQLi, GD, Zip)
RUN docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install -j$(nproc) \
        pdo \
        pdo_mysql \
        mysqli \
        gd \
        zip

# 3. Enable Apache mod_rewrite for clean URLs / routing
RUN a2enmod rewrite

# 4. Copy application source files into the Apache document root
COPY . /var/www/html/

# 5. Set correct permissions for web files
RUN chown -R www-data:www-data /var/www/html \
    && chmod -R 755 /var/www/html

# 6. Expose port 80 for web traffic
EXPOSE 80
