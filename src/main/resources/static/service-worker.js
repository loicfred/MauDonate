const CACHE_NAME = "maudonate-v1";

const ASSETS_TO_CACHE = [
    "/manifest.json",
    "/css/main.css",
    "/assets/js/app.js",
    "/img/logo.png",
    "/img/logo_transparent.png"
];

// Install event
self.addEventListener("install", (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            console.log("Caching assets...");
            return cache.addAll(ASSETS_TO_CACHE);
        }).catch(err => console.log("Error caching assets:", err))
    );
    self.skipWaiting();
});

// Fetch event
self.addEventListener("fetch", (event) => {
    const req = event.request;
    if (req.mode === "navigate") {
        event.respondWith(
            fetch(req).catch(() => caches.match("/offline.html"))
        );
        return;
    }
    event.respondWith(
        caches.match(req).then(res => res || fetch(req))
    );
});

// Activate event
self.addEventListener("activate", (event) => {
    event.waitUntil(
        caches.keys().then((keys) =>
            Promise.all(
                keys
                    .filter((key) => key !== CACHE_NAME)
                    .map((key) => caches.delete(key))
            )
        )
    );
    self.clients.claim();
});
