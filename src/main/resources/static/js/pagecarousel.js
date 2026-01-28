const pgCarousel = document.getElementById("pageCarousel");
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
        pgCarousel.style.transition = "none";
        pgCarousel.style.transform = `translateX(${offset}px)`;
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
    pgCarousel.addEventListener("touchstart", e => {
        const t = e.touches[0];
        start(t.clientX, t.clientY);
    }, { passive: true });

    pgCarousel.addEventListener("touchmove", e => {
        const t = e.touches[0];
        move(t.clientX, t.clientY, () => e.preventDefault());
    }, { passive: false });

    pgCarousel.addEventListener("touchend", end);

    // 🔹 MOUSE EVENTS (DESKTOP)
    pgCarousel.addEventListener("mousedown", e => {
        if (e.button !== 0) return;
        if (e.target.closest("button")) return;
        if (e.target.closest(".slide")) return;
        start(e.clientX, e.clientY);
    });
    window.addEventListener("mousemove", e => {
        if (e.button !== 0) return;
        if (e.target.closest("button")) return;
        if (e.target.closest(".slide")) return;
        move(e.clientX, e.clientY);
    });

    window.addEventListener("mouseup", e => {
        end();
    });
    window.addEventListener("mouseleave", e => {
        end();
    });

    window.addEventListener("resize", () => {
        pgCarousel.style.transition = "none";
        pgCarousel.style.transform = `translateX(-${currentIndex * window.innerWidth}px)`;
    });
})();

function goToPage(i) {
    if (i == null) i = 0;
    closeSidebar();
    currentIndex = i;
    pgCarousel.style.transform = `translateX(-${i * 100}%)`;
    pgCarousel.style.transition = "transform 0.3s ease";
    document.querySelectorAll(".bottom-nav .nav-item").forEach(item => {
        item.classList.remove("active");
    });
    document.querySelectorAll(".bottom-nav .nav-item")[currentIndex].classList.add("active");
}
goToPage(new URLSearchParams(window.location.search).get('page'))
