-- Allows the first two authenticated users to claim an administrator slot.
-- The database lock prevents two simultaneous registrations from both taking
-- the final available slot.

create or replace function public.claim_admin_slot()
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  admin_count integer;
begin
  if auth.uid() is null then
    raise exception 'Authentication is required';
  end if;

  perform pg_advisory_xact_lock(hashtext('bmatch-admin-registration'));
  select count(*) into admin_count from public.admin_users;

  if admin_count >= 2 then
    return false;
  end if;

  insert into public.admin_users (user_id)
  values (auth.uid())
  on conflict (user_id) do nothing;

  return true;
end;
$$;

grant execute on function public.claim_admin_slot() to authenticated;

create or replace function public.admin_registration_open()
returns boolean
language sql
security definer
set search_path = public
as $$
  select (select count(*) from public.admin_users) < 2;
$$;

grant execute on function public.admin_registration_open() to anon, authenticated;
