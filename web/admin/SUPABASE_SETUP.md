# Supabase setup

1. Open [supabase.com](https://supabase.com), create an account, and create a project named `bmatch`.
2. In the project dashboard, open **SQL Editor**, paste `..\..\supabase\schema.sql`, and run it. This uses your original `profiles` columns (`age_range`, `phone`, `hiv_confirmed`, `terms_accepted`, `last_seen_at`, and `avatar`) and adds the fields required by the admin workflow.
   If you already ran the previous schema, run `..\..\supabase\migrations\0002_align_initial_profiles.sql` instead.
   Then run `..\..\supabase\migrations\0004_first_twenty_access.sql` to enable automatic free access for the earliest 20 users.
   If only `profiles` exists in your project, run `..\..\supabase\migrations\0005_add_application_tables.sql` to add payments, matches, chats, messages, settings, and admin users.
3. In **Authentication > Users**, create the first admin user with email and password.
4. Copy that user's UUID. In **SQL Editor**, run:

   ```sql
   insert into public.admin_users (user_id)
   values ('1586c71a-94ef-4556-b969-5f9372596d3d');
   ```

   Do not insert into `public.Test_1`; that table does not exist. If the schema has not been run yet, run `supabase\schema.sql` first.

   If the account already exists, verify that it is registered as an admin:

   ```sql
   select user_id from public.admin_users where user_id = 'YOUR_AUTH_USER_UUID';
   ```

   The result must contain the same UUID as the account in **Authentication > Users**.

5. Copy `supabase-config.example.js` to `supabase-config.js` and set the project URL and anon key from **Project Settings > API**.
6. Open `index.html` through a local web server. Do not open it with `file://` when connecting to Supabase.

For Android registration, set the same URL and anon key in `app/src/main/java/com/example/bmatematch/data/SupabaseClient.kt`. Disable email confirmation in **Authentication > Providers > Email** while testing, or the app will wait for confirmation before it can create the profile.

The SQL policies protect profiles, receipts, matches, chats, and settings. The browser must only use the public anon key; never put the service-role key in the Android app or web folder.
