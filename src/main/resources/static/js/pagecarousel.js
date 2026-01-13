const carousel = document.getElementById("pageCarousel");
const pages = document.querySelectorAll(".page");
let currentIndex = 0;
(() => {
    let startX = 0;
    let startY = 0;
    let deltaX = 0;
    let isSwiping = false;
    let isDragging = false;

    const threshold = 60;

    function start(x, y) {
        startX = x;
        startY = y;
        deltaX = 0;
        isSwiping = false;
        isDragging = true;
    }

    function move(x, y, preventDefault) {
        if (!isDragging) return;

        deltaX = x - startX;
        const deltaY = y - startY;

        if (!isSwiping) {
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                isSwiping = true;
            } else {
                return;
            }
        }

        if (preventDefault) preventDefault();

        const offset = (-currentIndex * window.innerWidth) + deltaX;
        carousel.style.transition = "none";
        carousel.style.transform = `translateX(${offset}px)`;
    }

    function end() {
        if (!isDragging) return;
        isDragging = false;

        if (!isSwiping) return;

        if (deltaX > threshold && currentIndex > 0) {
            currentIndex--;
        } else if (deltaX < -threshold && currentIndex < pages.length - 1) {
            currentIndex++;
        }
        goToPage(currentIndex);
    }

    // 🔹 TOUCH EVENTS
    carousel.addEventListener("touchstart", e => {
        const t = e.touches[0];
        start(t.clientX, t.clientY);
    }, { passive: true });

    carousel.addEventListener("touchmove", e => {
        const t = e.touches[0];
        move(t.clientX, t.clientY, () => e.preventDefault());
    }, { passive: false });

    carousel.addEventListener("touchend", end);

    // 🔹 MOUSE EVENTS (DESKTOP)
    carousel.addEventListener("mousedown", e => {
        start(e.clientX, e.clientY);
    });

    window.addEventListener("mousemove", e => {
        move(e.clientX, e.clientY);
    });

    window.addEventListener("mouseup", end);
    window.addEventListener("mouseleave", end);

    window.addEventListener("resize", () => {
        carousel.style.transition = "none";
        carousel.style.transform = `translateX(-${currentIndex * window.innerWidth}px)`;
    });
})();

function goToPage(i) {
    if (i == null) i = 0;
    closeSidebar();
    currentIndex = i;
    carousel.style.transform = `translateX(-${i * 100}%)`;
    carousel.style.transition = "transform 0.3s ease";
    document.querySelectorAll(".bottom-nav .nav-item").forEach(item => {
        item.classList.remove("active");
    });
    document.querySelectorAll(".bottom-nav .nav-item")[currentIndex].classList.add("active");
}
goToPage(new URLSearchParams(window.location.search).get('page'))
