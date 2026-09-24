const $ = (selector) => document.querySelector(selector);
const config = window.BMATCH_SUPABASE;
const supabase = config && window.supabase
  ? window.supabase.createClient(config.url, config.anonKey)
  : null;
const hasSupabaseConfig = Boolean(
  supabase &&
  config.url &&
  !config.url.endsWith("/rest/v1/") &&
  !config.anonKey.includes("PASTE_") &&
  config.anonKey !== "******"
);

let users = [
  { name: "Sarah Johnson", email: "sarah@example.com", art: "ART-008", location: "Ikeja, Lagos", date: "Today", status: "Pending" },
  { name: "Dr. Amina Bello", email: "amina@example.com", art: "ART-007", location: "Abuja Municipal, FCT", date: "Yesterday", status: "Active" },
  { name: "John Peter", email: "john@example.com", art: "ART-006", location: "Lagos Island, Lagos", date: "Sep 20, 2026", status: "Pending" },
  { name: "Eunice Taiwo", email: "eunice@example.com", art: "ART-005", location: "Ibadan North, Oyo", date: "Sep 18, 2026", status: "Active" }
];
let payments = [];
let matches = [];
let conversations = [];
let messages = [];

function userRow(user) {
  const access = user.paymentExempt
    ? '<span class="pill green">First 20 · Free</span>'
    : `<span class="pill ${user.status === "Active" ? "green" : "orange"}">${user.status}</span>`;
  return `<tr><td><strong>${user.name}</strong><small class="muted">${user.email}</small></td><td>${user.art}</td><td>${user.location}</td><td>${user.date}</td><td>${access}</td><td>${user.status === "Pending" ? `<button class="action approve" data-id="${user.id || ""}" data-art="${user.art}">Approve</button>` : "—"}</td></tr>`;
}

function render() {
  $("#recent-users").innerHTML = users.slice(0, 3).map((user) => `<div class="recent-row"><span class="mini-avatar">${user.name.split(" ").map((part) => part[0]).join("")}</span><span><strong>${user.name}</strong><small>${user.art} • ${user.location}</small></span><time>${user.date}</time></div>`).join("");
  $("#users-table").innerHTML = users.map(userRow).join("");
  $("#payments-table").innerHTML = payments.length
    ? payments.map((payment) => `<tr><td><strong>${payment.name}</strong><small class="muted">${payment.art}</small></td><td>₦${Number(payment.amount).toLocaleString()}</td><td><button class="action receipt" data-path="${payment.receipt || ""}">${payment.receipt || "No receipt"}</button></td><td>${new Date(payment.date).toLocaleDateString()}</td><td><span class="pill ${payment.status === "Verified" ? "green" : "orange"}">${payment.status}</span></td><td>${payment.status === "Pending" ? `<button class="action verify" data-id="${payment.id}">Verify</button>` : "—"}</td></tr>`).join("")
    : `<tr><td colspan="6" class="muted">No payment records found.</td></tr>`;
  $("#matches-table").innerHTML = matches.length
    ? matches.map((match) => {
      const first = users.find((user) => user.id === match.first_user_id);
      const second = users.find((user) => user.id === match.second_user_id);
      const status = match.status === "active" ? "Active" : "Pending review";
      return `<tr><td><strong>#${match.id.slice(0, 8)}</strong></td><td>${first?.name || match.first_user_id} + ${second?.name || match.second_user_id}</td><td>${new Date(match.created_at).toLocaleString()}</td><td><span class="pill ${status === "Active" ? "green" : "orange"}">${status}</span></td></tr>`;
    }).join("")
    : `<tr><td colspan="4" class="muted">No matches found.</td></tr>`;
  $("#conversation-count").textContent = conversations.length;
  $("#message-count").textContent = messages.length;
}

function showPage(id) {
  const page = document.getElementById(id) ? id : "overview";
  document.querySelectorAll(".page").forEach((item) => item.classList.toggle("active", item.id === page));
  document.querySelectorAll(".nav-link").forEach((button) => button.classList.toggle("active", button.dataset.page === page));
  $("#page-title").textContent = page === "payments" ? "Payments & receipts" : page[0].toUpperCase() + page.slice(1);
  history.replaceState(null, "", `#${page}`);
}

function toast(message) {
  $("#toast").textContent = message;
  $("#toast").style.display = "block";
  setTimeout(() => { $("#toast").style.display = "none"; }, 2200);
}

function setConnectionStatus(message, connected = false) {
  const banner = $("#connection-banner");
  banner.textContent = message;
  banner.classList.add("visible");
  banner.classList.toggle("connected", connected);
}

function exportUsers() {
  const headers = ["Name", "Email", "ART ID", "Location", "Registered", "Status"];
  const rows = users.map((user) => [user.name, user.email, user.art, user.location, user.date, user.status]);
  const csv = [headers, ...rows].map((row) => row.map((value) => `"${String(value ?? "").replaceAll('"', '""')}"`).join(",")).join("\n");
  const link = document.createElement("a");
  link.href = URL.createObjectURL(new Blob([csv], { type: "text/csv;charset=utf-8" }));
  link.download = "bmatch-users.csv";
  link.click();
  URL.revokeObjectURL(link.href);
  toast("User list exported");
}

async function saveSettings() {
  if (!supabase) {
    toast("Connect Supabase before saving settings");
    return;
  }
  const { error } = await supabase.from("admin_settings").upsert({
    id: true,
    currency: "NGN",
    payment_initiation_threshold: 5000,
    auto_approve_users: $("#approval-policy").value === "automatic",
  });
  if (error) {
    toast(error.message);
    return;
  }
  toast("Settings saved");
}

async function loadLiveData() {
  if (!hasSupabaseConfig) {
    setConnectionStatus("Supabase is not connected. Add the project URL and anon public key to supabase-config.js.");
    render();
    return;
  }
  const { data: { user: currentUser }, error: userError } = await supabase.auth.getUser();
  if (userError) throw userError;
  const { data: adminMembership, error: adminError } = await supabase
    .from("admin_users")
    .select("user_id")
    .eq("user_id", currentUser.id)
    .maybeSingle();
  if (adminError) throw adminError;
  if (!adminMembership) {
    throw new Error("This account is not an admin. Add its user ID to public.admin_users in Supabase.");
  }
  const { data: profileData, error: profileError } = await supabase.from("profile_access").select("*").order("created_at", { ascending: false });
  if (profileError) throw profileError;
  users = (profileData || []).map((profile) => ({
    id: profile.id,
    name: profile.full_name || "Unnamed user",
    email: profile.email,
    art: profile.art_id,
    location: [profile.lga, profile.state].filter(Boolean).join(", ") || "Not provided",
    date: profile.created_at,
    status: profile.status === "active" ? "Active" : profile.status === "suspended" ? "Suspended" : "Pending",
    paymentExempt: profile.payment_exempt,
    accessTier: profile.access_tier
  }));
  const { data: paymentData, error: paymentError } = await supabase.from("payments").select("id, amount, receipt_path, status, submitted_at, profiles(full_name, art_id)").order("submitted_at", { ascending: false });
  if (paymentError) throw paymentError;
  payments = (paymentData || []).map((payment) => ({
    id: payment.id,
    name: payment.profiles?.full_name || "Unknown user",
    art: payment.profiles?.art_id || "—",
    amount: payment.amount,
    receipt: payment.receipt_path,
    date: payment.submitted_at,
    status: payment.status === "verified" ? "Verified" : "Pending"
  }));
  const { data: matchData, error: matchError } = await supabase.from("matches").select("id, first_user_id, second_user_id, status, created_at").order("created_at", { ascending: false });
  if (matchError) throw matchError;
  matches = matchData || [];
  const { data: conversationData, error: conversationError } = await supabase.from("conversations").select("id");
  if (conversationError) throw conversationError;
  conversations = conversationData || [];
  const { data: messageData, error: messageError } = await supabase.from("messages").select("id");
  if (messageError) throw messageError;
  messages = messageData || [];
  setConnectionStatus("Connected to Supabase. Live database data is displayed.", true);
  render();
}

async function requireAdmin() {
  if (!hasSupabaseConfig) {
    $("#auth-screen").hidden = true;
    await loadLiveData();
    return;
  }
  const { data: { session } } = await supabase.auth.getSession();
  if (!session) {
    $("#auth-screen").hidden = false;
    return;
  }
  $("#auth-screen").hidden = true;
  try {
    await loadLiveData();
  } catch (error) {
    setConnectionStatus(`Supabase connection failed: ${error.message || "check the URL, anon key, and database policies"}`);
    toast(error.message || "Could not load Supabase data");
  }
}

$("#login-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  $("#login-error").textContent = "";
  const { error } = await supabase.auth.signInWithPassword({ email: $("#login-email").value, password: $("#login-password").value });
  if (error) {
    $("#login-error").textContent = error.message;
    return;
  }
  await requireAdmin();
});

document.addEventListener("click", async (event) => {
  const navigation = event.target.closest("[data-page],[data-page-link]");
  if (navigation) showPage(navigation.dataset.page || navigation.dataset.pageLink);
  const action = event.target.closest(".approve,.verify,.receipt,.export-csv,.save-settings,.icon-button");
  if (!action) return;
  if (action.matches(".export-csv")) {
    exportUsers();
    return;
  }
  if (action.matches(".save-settings")) {
    await saveSettings();
    return;
  }
  if (action.matches(".icon-button")) {
    toast("No new notifications");
    return;
  }
  if (action.matches(".approve")) {
    if (hasSupabaseConfig) {
      const { error } = await supabase.from("profiles").update({ status: "active" }).eq("id", action.dataset.id);
      if (error) { toast(error.message); return; }
    } else {
      const user = users.find((item) => item.art === action.dataset.art);
      if (user) user.status = "Active";
    }
    render();
    toast("Account approved");
  }
  if (action.matches(".verify") && supabase) {
    const { error } = await supabase.from("payments").update({ status: "verified", reviewed_at: new Date().toISOString() }).eq("id", action.dataset.id);
    if (error) { toast(error.message); return; }
    await loadLiveData();
    toast("Receipt verified");
  }
  if (action.matches(".receipt") && action.dataset.path) {
    if (!supabase) { toast("Connect Supabase to open receipts"); return; }
    const { data, error } = await supabase.storage.from("payment-receipts").createSignedUrl(action.dataset.path, 300);
    if (error) { toast(error.message); return; }
    window.open(data.signedUrl, "_blank", "noopener");
  }
});

$("#user-search").addEventListener("input", (event) => {
  const query = event.target.value.toLowerCase();
  $("#users-table").innerHTML = users.filter((user) => `${user.name} ${user.email} ${user.art}`.toLowerCase().includes(query)).map(userRow).join("");
});

if (hasSupabaseConfig) supabase.auth.onAuthStateChange((_event, session) => { if (session) requireAdmin(); });
render();
showPage(location.hash.slice(1) || "overview");
window.addEventListener("hashchange", () => showPage(location.hash.slice(1) || "overview"));
requireAdmin();
