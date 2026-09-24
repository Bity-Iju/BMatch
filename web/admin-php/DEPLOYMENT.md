# Render deployment

This portal is deployed as a Docker web service, not as a Node service.

1. In Render, set the service runtime to **Docker**.
2. Set the Dockerfile path to `./Dockerfile` and Docker context to `.`.
3. Add `SUPABASE_URL` and `SUPABASE_ANON_KEY` as environment variables.
4. Deploy the latest commit from `main`.

The container listens on Render's `PORT` value and serves the portal from `login.php`.