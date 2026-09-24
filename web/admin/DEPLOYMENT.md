# Render deployment

The static admin portal runs on a Render **Node** web service.

Leave the Render service **Root Directory** blank. The repository root must be
the working directory so `web/server.js` resolves correctly.

Set the Render service commands to:

- Build command: `echo "No build required"`
- Start command: `node web/server.js`

Add these environment variables in Render:

- `SUPABASE_URL`: your Supabase project URL
- `SUPABASE_ANON_KEY`: your Supabase anon/public key

Do not use the Supabase service-role key in the browser. The server writes
`web/admin/supabase-config.js` at startup, so the key does not need to be
committed to GitHub.
