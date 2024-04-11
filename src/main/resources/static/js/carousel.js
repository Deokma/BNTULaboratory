let carousel = document.querySelector('.carousel');
let slides = document.querySelectorAll('.slide');
let currentIndex = 0;

function nextSlide() {
    currentIndex++;
    if (currentIndex >= slides.length) {
        currentIndex = 0;
    }
    updateCarousel();
}

function prevSlide() {
    currentIndex--;
    if (currentIndex < 0) {
        currentIndex = slides.length - 1;
    }
    updateCarousel();
}

function updateCarousel() {
    let offset = -currentIndex * slides[0].offsetWidth;
    carousel.style.transform = `translateX(${offset}px)`;
}

window.onload = function() {
    var element = document.querySelector('.your-element');
    element.classList.remove('hidden');
    element.classList.add('visible');
}
