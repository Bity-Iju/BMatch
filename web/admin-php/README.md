# PHP admin portal

This is the Bootstrap/PHP version of the BMatch admin dashboard. It includes separate pages for overview, users, payments and receipts, matches, chats, and settings.

1. Copy `config.example.php` to `config.php`.
2. Add the Supabase project URL and anon public key.
3. Run it with PHP from `web/admin-php`:

   ```powershell
   php -S localhost:8080
   ```

4. Open `http://localhost:8080/login.php`.
5. Sign in using the Supabase user that is present in `public.admin_users`.

Administrator registration is available at `register.php` and is limited by the database to two accounts. Run `supabase/migrations/0006_limit_admin_registration.sql` before using that page.

PHP must have the cURL extension enabled. The PHP server keeps the Supabase access token in a session and uses the existing RLS policies.
