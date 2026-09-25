<?php
declare(strict_types=1);
session_start();

$config = require __DIR__ . '/config.php';

function configured(): bool {
    global $config;
    return !str_contains($config['supabase_url'], 'YOUR_PROJECT_REF')
        && !str_contains($config['supabase_anon_key'], 'YOUR_SUPABASE');
}

function supabase(string $method, string $path, ?array $body = null): array {
    global $config;
    $token = $_SESSION['access_token'] ?? $config['supabase_anon_key'];
    $headers = [
        'apikey: ' . $config['supabase_anon_key'],
        'Authorization: Bearer ' . $token,
        'Content-Type: application/json',
        'Accept: application/json',
    ];
    $curl = curl_init($config['supabase_url'] . '/rest/v1/' . ltrim($path, '/'));
    curl_setopt_array($curl, [
        CURLOPT_CUSTOMREQUEST => $method,
        CURLOPT_HTTPHEADER => $headers,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 15,
    ]);
    if ($body !== null) {
        curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($body, JSON_THROW_ON_ERROR));
    }
    $response = curl_exec($curl);
    $status = curl_getinfo($curl, CURLINFO_HTTP_CODE);
    $error = curl_error($curl);
    curl_close($curl);
    if ($response === false || $error !== '') {
        throw new RuntimeException($error ?: 'Supabase request failed.');
    }
    if ($status < 200 || $status >= 300) {
        throw new RuntimeException('Supabase returned HTTP ' . $status . ': ' . $response);
    }
    return $response === '' ? [] : (json_decode($response, true, 512, JSON_THROW_ON_ERROR) ?: []);
}

function auth_request(string $email, string $password): array {
    global $config;
    $curl = curl_init($config['supabase_url'] . '/auth/v1/token?grant_type=password');
    curl_setopt_array($curl, [
        CURLOPT_POST => true,
        CURLOPT_HTTPHEADER => ['apikey: ' . $config['supabase_anon_key'], 'Content-Type: application/json'],
        CURLOPT_POSTFIELDS => json_encode(['email' => $email, 'password' => $password], JSON_THROW_ON_ERROR),
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 15,
    ]);
    $response = curl_exec($curl);
    $status = curl_getinfo($curl, CURLINFO_HTTP_CODE);
    curl_close($curl);
    if ($response === false || $status < 200 || $status >= 300) {
        throw new RuntimeException('Invalid administrator login.');
    }

    function auth_signup(string $email, string $password): array {
        global $config;
        $curl = curl_init($config['supabase_url'] . '/auth/v1/signup');
        curl_setopt_array($curl, [
            CURLOPT_POST => true,
            CURLOPT_HTTPHEADER => ['apikey: ' . $config['supabase_anon_key'], 'Content-Type: application/json'],
            CURLOPT_POSTFIELDS => json_encode(['email' => $email, 'password' => $password], JSON_THROW_ON_ERROR),
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_TIMEOUT => 15,
        ]);
        $response = curl_exec($curl);
        $status = curl_getinfo($curl, CURLINFO_HTTP_CODE);
        curl_close($curl);
        if ($response === false || $status < 200 || $status >= 300) {
            throw new RuntimeException('Could not create the administrator account.');
        }
        return json_decode($response, true, 512, JSON_THROW_ON_ERROR);
    }
    return json_decode($response, true, 512, JSON_THROW_ON_ERROR);
}

function require_login(): void {
    if (empty($_SESSION['access_token'])) {
        header('Location: login.php');
        exit;
    }
}

function e(mixed $value): string {
    return htmlspecialchars((string) $value, ENT_QUOTES, 'UTF-8');
}

function page(string $title, string $content): void {
    require __DIR__ . '/layout.php';
}
