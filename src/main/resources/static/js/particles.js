/* Lightweight animated background: small soft white balls drifting across the screen. */
(function () {
    function initParticles() {
        var canvas = document.createElement('canvas');
        canvas.id = 'bg-particles';
        document.body.insertBefore(canvas, document.body.firstChild);
        var ctx = canvas.getContext('2d');

        var particles = [];
        var dpr = window.devicePixelRatio || 1;

        function resize() {
            canvas.width = window.innerWidth * dpr;
            canvas.height = window.innerHeight * dpr;
            canvas.style.width = window.innerWidth + 'px';
            canvas.style.height = window.innerHeight + 'px';
            ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
        }
        window.addEventListener('resize', resize);
        resize();

        function rand(min, max) { return Math.random() * (max - min) + min; }

        function seedParticles() {
            var area = window.innerWidth * window.innerHeight;
            var count = Math.max(28, Math.min(90, Math.floor(area / 16000)));
            particles = [];
            for (var i = 0; i < count; i++) {
                particles.push({
                    x: rand(0, window.innerWidth),
                    y: rand(0, window.innerHeight),
                    r: rand(1.2, 3.4),
                    vx: rand(-0.22, 0.22),
                    vy: rand(-0.18, 0.18),
                    o: rand(0.25, 0.8)
                });
            }
        }
        seedParticles();
        window.addEventListener('resize', function () {
            seedParticles();
        });

        function tick() {
            var w = window.innerWidth, h = window.innerHeight;
            ctx.clearRect(0, 0, w, h);
            for (var i = 0; i < particles.length; i++) {
                var p = particles[i];
                p.x += p.vx;
                p.y += p.vy;
                if (p.x < -6) p.x = w + 6;
                if (p.x > w + 6) p.x = -6;
                if (p.y < -6) p.y = h + 6;
                if (p.y > h + 6) p.y = -6;

                ctx.beginPath();
                ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
                ctx.fillStyle = 'rgba(255,255,255,' + p.o + ')';
                ctx.shadowColor = 'rgba(255,255,255,0.9)';
                ctx.shadowBlur = 7;
                ctx.fill();
            }
            requestAnimationFrame(tick);
        }
        requestAnimationFrame(tick);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initParticles);
    } else {
        initParticles();
    }
})();
