FROM php:8.2-apache

RUN docker-php-ext-install curl

COPY web/admin-php/ /var/www/html/
COPY docker-entrypoint.sh /usr/local/bin/bmatch-entrypoint

RUN chmod +x /usr/local/bin/bmatch-entrypoint \
    && chown -R www-data:www-data /var/www/html

ENV APACHE_DOCUMENT_ROOT=/var/www/html

EXPOSE 80

ENTRYPOINT ["bmatch-entrypoint"]
CMD ["apache2-foreground"]
