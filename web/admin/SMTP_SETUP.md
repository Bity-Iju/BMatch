# Welcome email / SMTP setup

The Android app shows a welcome message after registration and the PHP admin portal handles administrator accounts. Do not email passwords or place SMTP credentials in the APK.

For production welcome emails, configure SMTP in a server-side endpoint or Supabase Auth SMTP settings:

- SMTP host
- SMTP port
- SMTP username
- SMTP password
- From address

Send a welcome/verification email with a password-reset link when needed. Never send the user's password by email.
