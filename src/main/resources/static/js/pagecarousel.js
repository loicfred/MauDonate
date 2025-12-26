const pageWrap = document.getElementById('pageWrap');
const pageCarousel = document.getElementById('pageCarousel');
const pages = pageCarousel.querySelectorAll('.page');
let pageIndex = 0;

let startX = 0, isDown = false, isDragging = false;


pageWrap.addEventListener('pointerdown', e => {
    if (e.target.closest('button')) return;
    startX = e.clientX;
    isDown = true;
    isDragging = false;
});

pageWrap.addEventListener('pointermove', e => {
    if (!isDown) return;
    const dx = e.clientX - startX;
    if (Math.abs(dx) > 10 && !isDragging) {
        isDragging = true;
        pageWrap.setPointerCapture(e.pointerId);
    }
    if (!isDragging) return;
    pageCarousel.style.transition = 'none';
    pageCarousel.style.transform = `translateX(calc(-${pageIndex * 100}% + ${dx}px))`;
});

pageWrap.addEventListener('pointerup', e => {
    if (!isDown) return;
    isDown = false;
    if (isDragging) pageWrap.releasePointerCapture(e.pointerId);

    pageCarousel.style.transition = '';
    if (!isDragging) return;
    const dx = e.clientX - startX;
    if (Math.abs(dx) > 80) {
        if (dx < 0 && pageIndex < pages.length - 1) pageIndex++;
        if (dx > 0 && pageIndex > 0) pageIndex--;
    }
    pageCarousel.style.transform = `translateX(-${pageIndex * 100}%)`;
});

function goToPage(i) {
    if (i == null) return;
    closeSidebar();
    pageIndex = i;
    pageCarousel.style.transform = `translateX(-${i * 100}%)`;
}
goToPage(new URLSearchParams(window.location.search).get('page'))