<?php
require __DIR__ . '/bootstrap.php'; require_login();
try {
    $profiles = supabase('GET', 'profile_access?select=*&order=created_at.desc');
    $payments = supabase('GET', 'payments?select=id,amount,status,submitted_at&order=submitted_at.desc');
    $matches = supabase('GET', 'matches?select=id,status&order=created_at.desc');
    $error = '';
} catch (Throwable $exception) { $profiles=[];$payments=[];$matches=[];$error=$exception->getMessage(); }
$pending = count(array_filter($profiles, fn($p) => ($p['status'] ?? '') === 'pending'));
$pendingPayments = count(array_filter($payments, fn($p) => ($p['status'] ?? '') === 'pending'));
$content = '<div class="welcome rounded-4 p-4 mb-4 d-flex justify-content-between align-items-center"><div><h2 class="h4">Good afternoon, Fidelis</h2><p class="text-muted mb-0">Monitor registrations, approvals, payments and platform activity.</p></div><a class="btn btn-primary" href="users.php">Review registrations →</a></div>';
if ($error) $content .= '<div class="alert alert-warning">'.e($error).'</div>';
$content .= '<div class="row g-3 mb-4">';
foreach ([['Total accounts',count($profiles),'text-primary'],['Awaiting approval',$pending,'text-warning'],['Receipts to verify',$pendingPayments,'text-success'],['Matches',count($matches),'text-info']] as $metric) $content .= '<div class="col-sm-6 col-xl-3"><div class="card rounded-4 p-4"><span class="text-muted">'.$metric[0].'</span><div class="metric '.$metric[2].'">'.$metric[1].'</div></div></div>';
$content .= '</div><div class="card rounded-4 p-4"><div class="d-flex justify-content-between"><h2 class="h5">Recent registrations</h2><a href="users.php">View all</a></div><div class="table-responsive"><table class="table align-middle mb-0"><thead><tr><th>Name</th><th>ART ID</th><th>Access</th><th>Status</th></tr></thead><tbody>';
foreach (array_slice($profiles,0,5) as $profile) $content .= '<tr><td>'.e($profile['full_name']).'<small class="d-block text-muted">'.e($profile['email']).'</small></td><td>'.e($profile['art_id']).'</td><td>'.(($profile['payment_exempt']??false)?'<span class="badge text-bg-success">First 20 · Free</span>':'Standard').'</td><td>'.e($profile['status']).'</td></tr>';
$content .= '</tbody></table></div></div>';
$title='Overview'; page($title,$content);
