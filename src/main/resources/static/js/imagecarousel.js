const carousel = document.getElementById('carousel');
const carouselWrap = document.getElementById('carouselWrap');
const slides = carousel.querySelectorAll('.slide');
const dots = document.getElementById('dots');

let current = 0;

// dots
slides.forEach((s, i) => {
    const d = document.createElement('div');
    d.className = 'dot' + (i === 0 ? ' active' : '');
    d.dataset.i = i;
    d.addEventListener('click', () => goTo(i));
    dots.appendChild(d);
});

function updateCarousel() {
    carousel.style.transform = `translateX(-${current * 100}%)`;
    Array.from(dots.children).forEach((d, i) =>
        d.classList.toggle('active', i === current)
    );
}

function goTo(i) {
    current = (i + slides.length) % slides.length;
    updateCarousel();
}

// swipe
let startX = 0, startY = 0;
let isDown = false;
let isDragging = false;

carouselWrap.addEventListener('pointerdown', (e) => {
    isDown = true;
    isDragging = false;
    startX = e.clientX;
    startY = e.clientY;

    // stop image dragging / selection weirdness
    carouselWrap.setPointerCapture(e.pointerId);

    // optional: makes mobile feel snappier
    carousel.style.willChange = 'transform';
});

carouselWrap.addEventListener('pointermove', (e) => {
    if (!isDown) return;

    const dx = e.clientX - startX;
    const dy = e.clientY - startY;

    // if user is mostly scrolling vertically, don't hijack the gesture
    if (!isDragging) {
        if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > 6) {
            // treat as scroll; stop handling swipe
            isDown = false;
            carousel.style.willChange = '';
            return;
        }
        if (Math.abs(dx) > 6) isDragging = true;
    }

    if (!isDragging) return;

    // prevent page from scrolling while swiping horizontally (works with touch-action too)
    e.preventDefault();

    carousel.style.transition = 'none';
    carousel.style.transform = `translateX(calc(-${current * 100}% + ${dx}px))`;
}, { passive: false });

function endSwipe(e) {
    if (!isDown) return;

    isDown = false;

    const dx = e.clientX - startX;
    carousel.style.transition = '';
    carousel.style.willChange = '';

    if (Math.abs(dx) > 60) {
        if (dx < 0) current = Math.min(current + 1, slides.length - 1);
        else current = Math.max(current - 1, 0);
    }
    updateCarousel();
}

carouselWrap.addEventListener('pointerup', endSwipe);
carouselWrap.addEventListener('pointercancel', endSwipe);

// pointerleave is not reliable on touch; keep it only as a mouse fallback
carouselWrap.addEventListener('pointerleave', () => {
    if (isDown) {
        isDown = false;
        carousel.style.transition = '';
        carousel.style.willChange = '';
        updateCarousel();
    }
});