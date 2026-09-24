<?php
require __DIR__ . '/bootstrap.php';
if (!empty($_SESSION['access_token'])) { header('Location: index.php'); exit; }
$error = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    try {
        if (!configured()) throw new RuntimeException('Configure Supabase in config.php first.');
        $auth = auth_request(trim($_POST['email'] ?? ''), (string) ($_POST['password'] ?? ''));
        $_SESSION['access_token'] = $auth['access_token'];
        $_SESSION['user_id'] = $auth['user']['id'];
        header('Location: index.php'); exit;
    } catch (Throwable $exception) { $error = $exception->getMessage(); }
}
?>
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="theme-color" content="#1b5e20"><title>Sign in · BMatch Admin</title><link rel="icon" type="image/png" href="assets/logo.png"><link rel="shortcut icon" type="image/png" href="assets/logo.png"><link rel="apple-touch-icon" href="assets/logo.png"><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><link rel="stylesheet" href="assets/admin.css"></head>
<body class="d-flex align-items-center justify-content-center min-vh-100">
<form method="post" class="card p-4 shadow-sm" style="width:min(400px,calc(100% - 32px))"><div class="auth-brand mb-3"><img src="assets/logo.png" alt="BMatch logo"></div><h1 class="h3">BMatch Admin</h1><p class="text-muted">Sign in with your Supabase administrator account.</p><?php if ($error): ?><div class="alert alert-danger"><?= e($error) ?></div><?php endif; ?><label class="form-label">Email<input class="form-control" name="email" type="email" required autocomplete="username"></label><label class="form-label">Password<input class="form-control" name="password" type="password" required autocomplete="current-password"></label><button class="btn btn-primary w-100 mt-2">Sign in</button><a class="d-block text-center mt-3" href="register.php">Register administrator</a></form>
</body></html>
