const hamburgerBtn = document.getElementById('hamburgerBtn');
const sidebar = document.getElementById('sidebar');
const overlay = document.getElementById('sidebarOverlay');
function openSidebar() {
    sidebar.classList.add('open');
    overlay.classList.add('show');
}
function closeSidebar() {
    sidebar.classList.remove('open');
    overlay.classList.remove('show');
}
hamburgerBtn.addEventListener('click', openSidebar);
overlay.addEventListener('click', closeSidebar);
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeSidebar();
});


/* App Ready Log */
console.log("app.js loaded successfully");

(() => {
    'use strict';

    // PWA requires secure context (localhost is OK)
    if (!('serviceWorker' in navigator)) {
        console.info('[PWA] Service workers not supported');
        return;
    }

    window.addEventListener('load', async () => {
        try {
            // Avoid double registration
            const existing = await navigator.serviceWorker.getRegistration('/');
            if (existing) {
                console.log('[PWA] Service Worker already registered:', existing.scope);
                return;
            }

            const registration = await navigator.serviceWorker.register(
                '/service-worker.js',
                { scope: '/' }
            );

            console.log('[PWA] Service Worker registered:', registration.scope);
        } catch (err) {
            console.error('[PWA] Service Worker registration failed:', err);
        }
    });
})();