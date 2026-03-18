import { useEffect, useState } from 'react'
import './Hero.css'

const TAGLINE = "Turning ideas into pixel-perfect interfaces — one component at a time."

export default function Hero() {
  const [displayed, setDisplayed] = useState('')
  const [done, setDone] = useState(false)

  useEffect(() => {
    let i = 0
    const timeout = setTimeout(() => {
      const interval = setInterval(() => {
        i++
        setDisplayed(TAGLINE.slice(0, i))
        if (i >= TAGLINE.length) {
          clearInterval(interval)
          setDone(true)
        }
      }, 28)
      return () => clearInterval(interval)
    }, 900)
    return () => clearTimeout(timeout)
  }, [])

  return (
    <section className="hero">
      <div className="hero-dots" />

      <nav className="hero-nav">
        <span className="nav-logo">YN.</span>
        <ul className="nav-links">
          <li><a href="#work">Work</a></li>
          <li><a href="#about">About</a></li>
          <li><a href="#contact">Contact</a></li>
        </ul>
      </nav>

      <div className="hero-body">
        <div className="eyebrow">Frontend Developer</div>
        <h1 className="hero-name">
          Your<br /><em>Name</em>
        </h1>
        <div className="tagline-wrap">
          <p className="tagline">
            {displayed}
            {!done && <span className="cursor" />}
            {done && <span className="cursor" />}
          </p>
        </div>
      </div>

      <div className="hero-footer">
        <div className="status-badge">
          <span className="status-dot" />
          Available for work
        </div>
        <div className="scroll-hint">
          Scroll
          <div className="scroll-arrow">
            <svg width="10" height="10" viewBox="0 0 10 10" fill="none">
              <path
                d="M5 1v8M2 6l3 3 3-3"
                stroke="currentColor"
                strokeWidth="1.2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </div>
        </div>
      </div>

      <div className="hero-divider" />
    </section>
  )
}
