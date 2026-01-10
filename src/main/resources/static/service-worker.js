const CACHE_NAME = 'maudonate-assets-v1';

self.addEventListener('install', event => {
    console.log('[SW] Installing');
    self.skipWaiting();
});

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

self.addEventListener('fetch', event => {
    const req = event.request;

    if (req.method !== 'GET') return;

    const url = new URL(req.url);

    if (url.origin === self.location.origin && url.pathname.startsWith('/assets/')) {
        event.respondWith(
            caches.open(CACHE_NAME).then(async cache => {
                const cached = await cache.match(req);
                if (cached) return cached;
                const response = await fetch(req);
                if (response.ok && response.type === 'basic') {
                    cache.put(req, response.clone());
                }
                return response;
            }).catch(err => {
                console.error('[SW] Asset fetch failed:', url.pathname, err);
                return new Response('', { status: 504 });
            })
        );
    }
});
