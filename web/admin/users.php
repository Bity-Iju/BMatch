<?php
require __DIR__ . '/bootstrap.php'; require_login();
$message=''; $error='';
if ($_SERVER['REQUEST_METHOD']==='POST' && isset($_POST['approve_id'])) { try { supabase('PATCH','profiles?id=eq.'.rawurlencode($_POST['approve_id']),['status'=>'active']); $message='Account approved.'; } catch(Throwable $e){$error=$e->getMessage();} }
try {$users=supabase('GET','profile_access?select=*&order=created_at.desc');}catch(Throwable $e){$users=[];$error=$e->getMessage();}
$content='<div class="d-flex justify-content-between align-items-center mb-4"><div><h2 class="h4">User accounts</h2><p class="text-muted">Review registrations and manage approvals.</p></div></div>';
if($message)$content.='<div class="alert alert-success">'.e($message).'</div>';if($error)$content.='<div class="alert alert-danger">'.e($error).'</div>';
$content.='<div class="card rounded-4 p-3 table-responsive"><table class="table align-middle"><thead><tr><th>User</th><th>ART ID</th><th>Location</th><th>Access</th><th>Status</th><th></th></tr></thead><tbody>';
foreach($users as $u){$free=($u['payment_exempt']??false);$content.='<tr><td><strong>'.e($u['full_name']).'</strong><small class="d-block text-muted">'.e($u['email']).'</small></td><td>'.e($u['art_id']).'</td><td>'.e(trim(($u['lga']??'').', '.($u['state']??''),', ')).'</td><td>'.($free?'<span class="badge text-bg-success">First 20 · Free</span>':'Standard').'</td><td>'.e($u['status']).'</td><td>'.($u['status']==='pending'?'<form method="post"><input type="hidden" name="approve_id" value="'.e($u['id']).'"><button class="btn btn-sm btn-outline-success">Approve</button></form>':'—').'</td></tr>';}
$content.='</tbody></table></div>'; $title='Users';page($title,$content);
