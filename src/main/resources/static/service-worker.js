const CACHE_NAME = "maudonate-v1";

const STATIC_ASSETS = [
    '/',
    '/css/main.css',
    '/js/app.js',
    '/manifest.json'
];

/* ---------------- INSTALL ---------------- */
self.addEventListener('install', event => {
    console.log('[SW] Installing');

    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => {
            return cache.addAll(STATIC_ASSETS);
        })
    );

    self.skipWaiting();
});

/* ---------------- ACTIVATE ---------------- */
self.addEventListener('activate', event => {
    console.log('[SW] Activating');

    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys
                    .filter(key => key !== CACHE_NAME)
                    .map(key => caches.delete(key))
            )
        )
    );

    self.clients.claim();
});

/* ---------------- FETCH ---------------- */
self.addEventListener('fetch', event => {
    const req = event.request;
    const url = new URL(req.url);

    // 🚫 Ignore non-http(s) requests (Chrome extensions, devtools, etc.)
    if (url.protocol !== "http:" && url.protocol !== "https:") {
        return;
    }

    // 🚫 Never cache POST requests (Spring Security / CSRF)
    if (req.method !== 'GET') {
        return;
    }

    // 🚫 Skip admin / API calls
    if (url.pathname.startsWith('/admin') ||
        url.pathname.startsWith('/api')) {
        return;
    }

    // 🖼 Static resources → cache first
    if (
        url.pathname.startsWith('/css') ||
        url.pathname.startsWith('/js') ||
        url.pathname.startsWith('/img') ||
        url.pathname.startsWith('/img/icons')
    ) {
        event.respondWith(cacheFirst(req));
        return;
    }

    // 📄 HTML pages → network first
    if (req.headers.get('accept')?.includes('text/html')) {
        event.respondWith(networkFirst(req));

    }
});

/* ---------------- STRATEGIES ---------------- */
async function cacheFirst(request) {
    const cache = await caches.open("app-cache");
    const cached = await cache.match(request);
    if (cached) return cached;

    const response = await fetch(request);
    await cache.put(request, response.clone());
    return response;
}

async function networkFirst(request) {
    const cache = await caches.open(CACHE_NAME);
    try {
        const response = await fetch(request);
        await cache.put(request, response.clone());
        return response;
    } catch {
        return cache.match(request);
    }
}