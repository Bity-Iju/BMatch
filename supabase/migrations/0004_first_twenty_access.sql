-- First 20 profiles by registration time receive payment-free access.
-- The view stays correct if an earlier profile is added later.

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

grant select on public.profile_access to authenticated;

comment on view public.profile_access is
'Derived access state: the earliest 20 registered profiles are payment exempt.';
