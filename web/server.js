const http = require("node:http");
const fs = require("node:fs");
const path = require("node:path");
const { URL } = require("node:url");

const root = path.resolve(__dirname, "admin");
const port = Number.parseInt(process.env.PORT || "10000", 10);
const supabaseUrl = process.env.SUPABASE_URL || "";
const supabaseAnonKey = process.env.SUPABASE_ANON_KEY || "";

fs.writeFileSync(
  path.join(root, "supabase-config.js"),
  `window.BMATCH_SUPABASE = ${JSON.stringify({
    url: supabaseUrl,
    anonKey: supabaseAnonKey,
  })};\n`,
  "utf8",
);

const contentTypes = {
  ".css": "text/css; charset=utf-8",
  ".html": "text/html; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".svg": "image/svg+xml",
};
const pageRoutes = new Map([
  ["/admin", "overview"],
  ["/overview", "overview"],
  ["/users", "users"],
  ["/payments", "payments"],
  ["/matches", "matches"],
  ["/chats", "chats"],
  ["/settings", "settings"],
]);

const server = http.createServer((request, response) => {
  const requestUrl = new URL(request.url || "/", "http://localhost");
  const page = pageRoutes.get(requestUrl.pathname);
  if (page) {
    response.writeHead(302, { Location: `/#${page}` });
    response.end();
    return;
  }
  const requestedPath = requestUrl.pathname === "/" ? "/index.html" : requestUrl.pathname;
  const filePath = path.resolve(root, `.${requestedPath}`);

  if (!filePath.startsWith(`${root}${path.sep}`)) {
    response.writeHead(400);
    response.end("Invalid path");
    return;
  }

  fs.readFile(filePath, (error, content) => {
    if (error) {
      response.writeHead(error.code === "ENOENT" ? 404 : 500, {
        "Content-Type": "text/plain; charset=utf-8",
      });
      response.end(error.code === "ENOENT" ? "Not found" : "Server error");
      return;
    }

    response.writeHead(200, {
      "Content-Type": contentTypes[path.extname(filePath).toLowerCase()] || "application/octet-stream",
      "Cache-Control": "no-store",
    });
    response.end(content);
  });
});

server.listen(port, "0.0.0.0", () => {
  console.log(`BMatch admin web server listening on port ${port}`);
});
