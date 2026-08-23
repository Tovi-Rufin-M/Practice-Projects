document.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // 1. Dark / Light Theme Switcher
    // ==========================================
    const themeToggleBtn = document.getElementById('theme-toggle');
    const htmlElement = document.documentElement;

    // Check saved theme from localStorage or default to system preference
    const savedTheme = localStorage.getItem('portfolio-theme');
    if (savedTheme) {
        htmlElement.setAttribute('data-theme', savedTheme);
    } else {
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        htmlElement.setAttribute('data-theme', prefersDark ? 'dark' : 'light');
    }

    themeToggleBtn.addEventListener('click', () => {
        const currentTheme = htmlElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        
        htmlElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('portfolio-theme', newTheme);
    });

    // ==========================================
    // 2. Mobile Navigation Drawer Toggle
    // ==========================================
    const mobileToggleBtn = document.getElementById('mobile-toggle');
    const navMenu = document.getElementById('nav-menu');
    const navLinks = document.querySelectorAll('.nav-link');

    mobileToggleBtn.addEventListener('click', () => {
        navMenu.classList.toggle('active');
        const isOpen = navMenu.classList.contains('active');
        
        const openIcon = mobileToggleBtn.querySelector('.open-icon');
        const closeIcon = mobileToggleBtn.querySelector('.close-icon');
        
        if (isOpen) {
            openIcon.style.display = 'none';
            closeIcon.style.display = 'block';
        } else {
            openIcon.style.display = 'block';
            closeIcon.style.display = 'none';
        }
    });

    // Close mobile menu when a nav link is clicked
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (navMenu.classList.contains('active')) {
                navMenu.classList.remove('active');
                const openIcon = mobileToggleBtn.querySelector('.open-icon');
                const closeIcon = mobileToggleBtn.querySelector('.close-icon');
                openIcon.style.display = 'block';
                closeIcon.style.display = 'none';
            }
        });
    });

    // ==========================================
    // 3. Dynamic Typewriter Effect for Hero
    // ==========================================
    const typewriterElement = document.getElementById('typewriter');
    if (typewriterElement) {
        const phrases = [
            'digital experiences',
            'modern web apps',
            'scalable solutions',
            'interactive UI'
        ];
        
        let phraseIndex = 0;
        let charIndex = 0;
        let isDeleting = false;
        let typeSpeed = 100;

        function typeEffect() {
            const currentPhrase = phrases[phraseIndex];

            if (isDeleting) {
                typewriterElement.textContent = currentPhrase.substring(0, charIndex - 1);
                charIndex--;
                typeSpeed = 50;
            } else {
                typewriterElement.textContent = currentPhrase.substring(0, charIndex + 1);
                charIndex++;
                typeSpeed = 100;
            }

            if (!isDeleting && charIndex === currentPhrase.length) {
                isDeleting = true;
                typeSpeed = 2000; // Pause at end of phrase
            } else if (isDeleting && charIndex === 0) {
                isDeleting = false;
                phraseIndex = (phraseIndex + 1) % phrases.length;
                typeSpeed = 500; // Pause before starting new word
            }

            setTimeout(typeEffect, typeSpeed);
        }

        // Start typewriter effect after small initial delay
        setTimeout(typeEffect, 1000);
    }

    // ==========================================
    // 4. Header Shadow on Scroll
    // ==========================================
    const navbar = document.getElementById('navbar');
    window.addEventListener('scroll', () => {
        if (window.scrollY > 20) {
            navbar.style.boxShadow = '0 10px 30px -10px rgba(0, 0, 0, 0.3)';
        } else {
            navbar.style.boxShadow = 'none';
        }
    });
});
