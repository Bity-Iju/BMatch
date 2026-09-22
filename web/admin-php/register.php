<?php
require __DIR__ . '/bootstrap.php';

$error = '';
$success = '';
$closed = false;

try {
    if (!configured()) {
        throw new RuntimeException('Configure Supabase in config.php first.');
    }
    $open = supabase('POST', 'rpc/admin_registration_open', []);
    $closed = !$open;
} catch (Throwable $exception) {
    $error = $exception->getMessage();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && !$closed && $error === '') {
    try {
        $email = trim((string) ($_POST['email'] ?? ''));
        $password = (string) ($_POST['password'] ?? '');
        $confirmation = (string) ($_POST['password_confirmation'] ?? '');
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            throw new RuntimeException('Enter a valid email address.');
        }
        if (strlen($password) < 8 || $password !== $confirmation) {
            throw new RuntimeException('Passwords must match and contain at least 8 characters.');
        }

        $auth = auth_signup($email, $password);
        if (empty($auth['access_token'])) {
            $success = 'Account created. Confirm the email, then sign in and run registration again to claim the administrator slot.';
        } else {
            $_SESSION['access_token'] = $auth['access_token'];
            $_SESSION['user_id'] = $auth['user']['id'];
            $claim = supabase('POST', 'rpc/claim_admin_slot', []);
            if (($claim[0] ?? $claim) === true) {
                header('Location: index.php');
                exit;
            }
            session_destroy();
            $closed = true;
            $success = 'The two administrator slots have already been claimed.';
        }
    } catch (Throwable $exception) {
        $error = $exception->getMessage();
    }
}
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Admin registration · BMatch</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="assets/admin.css">
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100">
  <form method="post" class="card p-4 shadow-sm" style="width:min(430px,calc(100% - 32px))">
    <div class="brand-mark d-inline-block mb-3">B</div>
    <h1 class="h3">Register administrator</h1>
    <p class="text-muted">Only two administrator accounts can be created for this BMatch project.</p>
    <?php if ($closed): ?><div class="alert alert-secondary">Administrator registration is closed. The two available slots have been used.</div><?php endif; ?>
    <?php if ($error): ?><div class="alert alert-danger"><?= e($error) ?></div><?php endif; ?>
    <?php if ($success): ?><div class="alert alert-success"><?= e($success) ?></div><?php endif; ?>
    <?php if (!$closed && !$success): ?>
      <label class="form-label">Email<input class="form-control" name="email" type="email" required autocomplete="email"></label>
      <label class="form-label">Password<input class="form-control" name="password" type="password" minlength="8" required autocomplete="new-password"></label>
      <label class="form-label">Confirm password<input class="form-control" name="password_confirmation" type="password" minlength="8" required autocomplete="new-password"></label>
      <button class="btn btn-primary w-100 mt-2">Create administrator account</button>
    <?php endif; ?>
    <a class="d-block text-center mt-3" href="login.php">Back to sign in</a>
  </form>
</body>
</html>
