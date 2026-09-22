-- Run this migration when public.profiles already exists but the other
-- application tables have not been created yet.

create extension if not exists "pgcrypto";

do $$
begin
  create type public.payment_status as enum ('pending', 'verified', 'rejected');
exception when duplicate_object then null;
end $$;

do $$
begin
  create type public.match_status as enum ('pending_review', 'active', 'declined');
exception when duplicate_object then null;
end $$;

create table if not exists public.payments (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  amount numeric(12, 2) not null check (amount >= 0),
  receipt_path text,
  status public.payment_status not null default 'pending',
  submitted_at timestamptz not null default now(),
  reviewed_at timestamptz,
  reviewed_by uuid references auth.users(id)
);

create table if not exists public.matches (
  id uuid primary key default gen_random_uuid(),
  first_user_id uuid not null references public.profiles(id) on delete cascade,
  second_user_id uuid not null references public.profiles(id) on delete cascade,
  status public.match_status not null default 'pending_review',
  created_at timestamptz not null default now(),
  constraint different_match_users check (first_user_id <> second_user_id)
);

create table if not exists public.conversations (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now()
);

create table if not exists public.conversation_members (
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  primary key (conversation_id, user_id)
);

create table if not exists public.messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  sender_id uuid not null references public.profiles(id) on delete cascade,
  message_text text not null check (length(trim(message_text)) > 0),
  attachment_path text,
  created_at timestamptz not null default now()
);

create table if not exists public.admin_settings (
  id boolean primary key default true check (id),
  payment_initiation_threshold numeric(12, 2) not null default 5000,
  currency text not null default 'NGN',
  auto_approve_users boolean not null default false,
  updated_at timestamptz not null default now()
);

create table if not exists public.admin_users (
  user_id uuid primary key references auth.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

insert into public.admin_settings (id) values (true)
on conflict (id) do nothing;

alter table public.payments enable row level security;
alter table public.matches enable row level security;
alter table public.conversations enable row level security;
alter table public.conversation_members enable row level security;
alter table public.messages enable row level security;
alter table public.admin_settings enable row level security;
alter table public.admin_users enable row level security;

-- This function is safe to create after admin_users exists.
create or replace function public.is_admin()
returns boolean
language sql stable security definer set search_path = public
as $$
  select exists (
    select 1 from public.admin_users where user_id = auth.uid()
  );
$$;

grant select on public.payments, public.matches, public.conversations,
  public.conversation_members, public.messages, public.admin_settings
  to authenticated;

-- Add policies only when they are not already present.
do $$
begin
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Users and admins can view payments') then
    create policy "Users and admins can view payments" on public.payments for select to authenticated
      using (user_id = auth.uid() or public.is_admin());
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Users can submit payments') then
    create policy "Users can submit payments" on public.payments for insert to authenticated
      with check (user_id = auth.uid());
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Admins can review payments') then
    create policy "Admins can review payments" on public.payments for update to authenticated
      using (public.is_admin()) with check (public.is_admin());
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Users can view relevant matches') then
    create policy "Users can view relevant matches" on public.matches for select to authenticated
      using (first_user_id = auth.uid() or second_user_id = auth.uid() or public.is_admin());
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Admins can manage matches') then
    create policy "Admins can manage matches" on public.matches for all to authenticated
      using (public.is_admin()) with check (public.is_admin());
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Members can view conversations') then
    create policy "Members can view conversations" on public.conversations for select to authenticated
      using (public.is_admin() or exists (
        select 1 from public.conversation_members cm
        where cm.conversation_id = id and cm.user_id = auth.uid()
      ));
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Members can view conversation membership') then
    create policy "Members can view conversation membership" on public.conversation_members for select to authenticated
      using (user_id = auth.uid() or public.is_admin());
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Members can view messages') then
    create policy "Members can view messages" on public.messages for select to authenticated
      using (public.is_admin() or sender_id = auth.uid() or exists (
        select 1 from public.conversation_members cm
        where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
      ));
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Members can send messages') then
    create policy "Members can send messages" on public.messages for insert to authenticated
      with check (sender_id = auth.uid() and exists (
        select 1 from public.conversation_members cm
        where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
      ));
  end if;
  if not exists (select 1 from pg_policies where schemaname = 'public' and policyname = 'Admins can manage settings') then
    create policy "Admins can manage settings" on public.admin_settings for all to authenticated
      using (public.is_admin()) with check (public.is_admin());
  end if;
end $$;

insert into storage.buckets (id, name, public)
values ('payment-receipts', 'payment-receipts', false)
on conflict (id) do nothing;
