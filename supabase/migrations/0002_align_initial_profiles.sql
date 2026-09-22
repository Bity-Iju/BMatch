-- Align an existing BMatch database with the original profiles schema.
-- Run after the initial schema if the profiles table already exists.

do $$
begin
  create type public.account_status as enum ('pending', 'active', 'suspended');
exception
  when duplicate_object then null;
end
$$;

alter table public.profiles
  add column if not exists age_range text not null default '',
  add column if not exists phone text not null default '',
  add column if not exists hiv_confirmed boolean not null default false,
  add column if not exists terms_accepted boolean not null default false,
  add column if not exists last_seen_at timestamptz,
  add column if not exists avatar text,
  add column if not exists unique_version_number text,
  add column if not exists status_update text not null default '',
  add column if not exists avatar_url text,
  add column if not exists age text not null default '',
  add column if not exists dob date,
  add column if not exists status public.account_status not null default 'pending';

update public.profiles
set age_range = age
where age_range = '' and age <> '';

alter table public.profiles
  alter column full_name set default '',
  alter column sex set default '',
  alter column email set default '',
  alter column religion set default '',
  alter column state set default '',
  alter column lga set default '',
  alter column address set default '',
  alter column facility_name set default '',
  alter column art_id set default '';

create unique index if not exists profiles_unique_version_number_key
on public.profiles (unique_version_number)
where unique_version_number is not null;

comment on table public.profiles is
'Original BMatch profile schema plus approval, avatar, and unique identity fields.';
