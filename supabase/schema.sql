-- BMatch Supabase schema
-- Run this file in Supabase Dashboard > SQL Editor.

create extension if not exists "pgcrypto";

create type public.account_status as enum ('pending', 'active', 'suspended');
create type public.payment_status as enum ('pending', 'verified', 'rejected');
create type public.match_status as enum ('pending_review', 'active', 'declined');

create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  full_name text not null default '',
  age_range text not null default '',
  sex text not null default '',
  phone text not null default '',
  email text not null default '',
  religion text not null default '',
  state text not null default '',
  lga text not null default '',
  address text not null default '',
  facility_name text not null default '',
  art_id text not null unique,
  hiv_confirmed boolean not null default false,
  terms_accepted boolean not null default false,
  last_seen_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  avatar text,
  unique_version_number text unique,
  status public.account_status not null default 'pending',
  status_update text not null default '',
  avatar_url text,
  age text not null default '',
  dob date
);

create or replace view public.profile_access as
select
  p.*,
  row_number() over (order by p.created_at asc, p.id asc) <= 20 as payment_exempt,
  case
    when row_number() over (order by p.created_at asc, p.id asc) <= 20 then 'first_20_users'
    else 'standard_payment'
  end as access_tier
from public.profiles p;

alter view public.profile_access set (security_invoker = true);

create table public.payments (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  amount numeric(12, 2) not null check (amount >= 0),
  receipt_path text,
  status public.payment_status not null default 'pending',
  submitted_at timestamptz not null default now(),
  reviewed_at timestamptz,
  reviewed_by uuid references auth.users(id)
);

create table public.matches (
  id uuid primary key default gen_random_uuid(),
  first_user_id uuid not null references public.profiles(id) on delete cascade,
  second_user_id uuid not null references public.profiles(id) on delete cascade,
  status public.match_status not null default 'pending_review',
  created_at timestamptz not null default now(),
  constraint different_match_users check (first_user_id <> second_user_id)
);

create table public.conversations (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now()
);

create table public.conversation_members (
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  primary key (conversation_id, user_id)
);

create table public.messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  sender_id uuid not null references public.profiles(id) on delete cascade,
  message_text text not null check (length(trim(message_text)) > 0),
  attachment_path text,
  created_at timestamptz not null default now()
);

create table public.admin_settings (
  id boolean primary key default true check (id),
  payment_initiation_threshold numeric(12, 2) not null default 5000,
  currency text not null default 'NGN',
  auto_approve_users boolean not null default false,
  updated_at timestamptz not null default now()
);

insert into public.admin_settings (id)
values (true)
on conflict (id) do nothing;

create table public.admin_users (
  user_id uuid primary key references auth.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1 from public.admin_users
    where user_id = auth.uid()
  );
$$;

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create trigger profiles_updated_at
before update on public.profiles
for each row execute function public.set_updated_at();

alter table public.profiles enable row level security;
alter table public.payments enable row level security;
alter table public.matches enable row level security;
alter table public.conversations enable row level security;
alter table public.conversation_members enable row level security;
alter table public.messages enable row level security;
alter table public.admin_settings enable row level security;
alter table public.admin_users enable row level security;

create policy "Users can view their own profile"
on public.profiles for select to authenticated
using (id = auth.uid() or public.is_admin());

create policy "Users can create their own profile"
on public.profiles for insert to authenticated
with check (id = auth.uid());

create policy "Users can update their own profile"
on public.profiles for update to authenticated
using (id = auth.uid() or public.is_admin())
with check (id = auth.uid() or public.is_admin());

grant select on public.profile_access to authenticated;

create policy "Admins can view all matches"
on public.matches for select to authenticated
using (public.is_admin());

create policy "Users and admins can view payments"
on public.payments for select to authenticated
using (user_id = auth.uid() or public.is_admin());

create policy "Users can submit payments"
on public.payments for insert to authenticated
with check (user_id = auth.uid());

create policy "Admins can review payments"
on public.payments for update to authenticated
using (public.is_admin())
with check (public.is_admin());

create policy "Users can view relevant matches"
on public.matches for select to authenticated
using (first_user_id = auth.uid() or second_user_id = auth.uid() or public.is_admin());

create policy "Admins can manage matches"
on public.matches for all to authenticated
using (public.is_admin())
with check (public.is_admin());

create policy "Members can view conversations"
on public.conversations for select to authenticated
using (exists (
  select 1 from public.conversation_members cm
  where cm.conversation_id = id and cm.user_id = auth.uid()
) or public.is_admin());

create policy "Members can view conversation membership"
on public.conversation_members for select to authenticated
using (user_id = auth.uid() or public.is_admin());

create policy "Members can view messages"
on public.messages for select to authenticated
using (sender_id = auth.uid() or exists (
  select 1 from public.conversation_members cm
  where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
) or public.is_admin());

create policy "Members can send messages"
on public.messages for insert to authenticated
with check (sender_id = auth.uid() and exists (
  select 1 from public.conversation_members cm
  where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
));

create policy "Admins can manage settings"
on public.admin_settings for all to authenticated
using (public.is_admin())
with check (public.is_admin());

create policy "Users can view their own admin membership"
on public.admin_users for select to authenticated
using (user_id = auth.uid());

insert into storage.buckets (id, name, public)
values ('payment-receipts', 'payment-receipts', false)
on conflict (id) do nothing;

create policy "Users can upload their own receipts"
on storage.objects for insert to authenticated
with check (
  bucket_id = 'payment-receipts'
  and (storage.foldername(name))[1] = auth.uid()::text
);

create policy "Users and admins can view receipts"
on storage.objects for select to authenticated
using (
  bucket_id = 'payment-receipts'
  and ((storage.foldername(name))[1] = auth.uid()::text or public.is_admin())
);
