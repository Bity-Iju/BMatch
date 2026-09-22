<?php $pageTitle = $title ?? 'BMatch Admin'; ?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><?= e($pageTitle) ?> · BMatch Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="assets/admin.css">
</head>
<body>
<div class="d-flex min-vh-100">
  <aside class="sidebar p-3 d-flex flex-column">
    <a class="brand text-white text-decoration-none mb-4" href="index.php"><span class="brand-mark">B</span><span>BMatch<small>ADMIN</small></span></a>
    <?php $current = basename($_SERVER['PHP_SELF']); ?>
    <nav class="nav flex-column gap-1">
      <?php foreach (['index.php'=>'Overview','users.php'=>'Users','payments.php'=>'Payments & receipts','matches.php'=>'Matches','chats.php'=>'Chats','settings.php'=>'Settings'] as $file=>$label): ?>
        <a class="nav-link <?= $current === $file ? 'active' : '' ?>" href="<?= $file ?>"><?= $label ?></a>
      <?php endforeach; ?>
    </nav>
    <div class="mt-auto text-white-50 small"><span class="online-dot"></span> System operational<br><span>v1.0 · BMatch platform</span></div>
  </aside>
  <main class="flex-grow-1">
    <header class="topbar d-flex justify-content-between align-items-center px-4 px-lg-5 py-4">
      <div><div class="eyebrow">ADMINISTRATION</div><h1 class="h3 mb-0"><?= e($pageTitle) ?></h1></div>
      <div class="d-flex align-items-center gap-3"><span class="avatar">FA</span><div><strong>Fidelis Admin</strong><small class="d-block text-muted">Administrator</small></div><a href="logout.php" class="btn btn-sm btn-outline-secondary">Sign out</a></div>
    </header>
    <div class="container-fluid px-4 px-lg-5 py-4"><?= $content ?></div>
  </main>
</div>
</body>
</html>
