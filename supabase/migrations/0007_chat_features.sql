alter table public.messages drop constraint if exists messages_message_text_check;
alter table public.messages alter column message_text set default '';
alter table public.messages add column if not exists message_type text not null default 'text';
alter table public.messages add column if not exists metadata jsonb not null default '{}'::jsonb;
alter table public.conversations add column if not exists creator_id uuid references auth.users(id);

create table if not exists public.chat_settings (
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  disappearing_days integer check (disappearing_days is null or disappearing_days in (7)),
  locked boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  primary key (conversation_id, user_id)
);

alter table public.chat_settings enable row level security;
create policy "Users can manage their chat settings"
on public.chat_settings for all to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());

create policy "Users can view matched profiles"
on public.profiles for select to authenticated
using (
  id = auth.uid()
  or public.is_admin()
  or exists (
    select 1 from public.matches m
    where m.status = 'active'
      and ((m.first_user_id = auth.uid() and m.second_user_id = profiles.id)
        or (m.second_user_id = auth.uid() and m.first_user_id = profiles.id))
  )
);

create policy "Members can create conversations"
on public.conversations for insert to authenticated
with check (creator_id = auth.uid());

create policy "Users can view conversations they created"
on public.conversations for select to authenticated
using (creator_id = auth.uid());

drop policy if exists "Members can view conversation membership" on public.conversation_members;
create policy "Members can view conversation membership"
on public.conversation_members for select to authenticated
using (user_id = auth.uid() or exists (
  select 1 from public.conversation_members member
  where member.conversation_id = conversation_members.conversation_id
    and member.user_id = auth.uid()
));

create policy "Members can create membership"
on public.conversation_members for insert to authenticated
with check (user_id = auth.uid() or exists (
  select 1 from public.conversation_members existing
  where existing.conversation_id = conversation_members.conversation_id
    and existing.user_id = auth.uid()
));

create policy "Members can delete messages"
on public.messages for delete to authenticated
using (exists (
  select 1 from public.conversation_members cm
  where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
));

alter table public.chat_settings add column if not exists updated_at timestamptz not null default now();

insert into storage.buckets (id, name, public)
values ('chat-media', 'chat-media', false)
on conflict (id) do nothing;

create policy "Chat members can upload media"
on storage.objects for insert to authenticated
with check (bucket_id = 'chat-media' and (storage.foldername(name))[1] = auth.uid()::text);

create policy "Chat members can read media"
on storage.objects for select to authenticated
using (
  bucket_id = 'chat-media'
  and (
    (storage.foldername(name))[1] = auth.uid()::text
    or exists (
      select 1
      from public.messages msg
      join public.conversation_members cm
        on cm.conversation_id = msg.conversation_id
      where msg.attachment_path = name
        and cm.user_id = auth.uid()
    )
  )
);
