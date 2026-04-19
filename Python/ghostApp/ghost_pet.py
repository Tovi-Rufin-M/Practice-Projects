import tkinter as tk
from PIL import Image, ImageTk, ImageSequence
import random
import sys
import os

import sys, os
BASE = getattr(sys, '_MEIPASS', os.path.dirname(os.path.abspath(__file__)))
GIF_PATH = os.path.join(BASE, "ghost.gif")# place the GIF in the same folder as this script
SPEED = 3
FPS = 60

class GhostPet:
    def __init__(self):
        self.root = tk.Tk()
        self.root.overrideredirect(True)
        self.root.wm_attributes("-topmost", True)

        # Load GIF frames
        gif = Image.open(GIF_PATH)
        self.delays = []
        self.frames_right = []
        self.frames_left  = []

        SCALE = 0.1  # 👈 change this: 1.0 = original, 0.5 = half, 2.0 = double

        for frame in ImageSequence.Iterator(gif):
            f = frame.convert("RGBA")
            new_size = (int(f.width * SCALE), int(f.height * SCALE))
            f = f.resize(new_size, Image.NEAREST)  # NEAREST keeps the pixel-art crisp
            # Make white/near-white background transparent
            data = f.getdata()
            new_data = []
            for r, g, b, a in data:
                if r < 230 and g > 230 and b > 230:
                    new_data.append((r, g, b, 0))
                else:
                    new_data.append((r, g, b, a))
            f.putdata(new_data)

            self.frames_right.append(f.transpose(Image.FLIP_LEFT_RIGHT))
            self.frames_left.append(f)

            try:
                delay = frame.info.get("duration", 100)
            except Exception:
                delay = 100
            self.delays.append(max(delay, 50))

        self.ghost_w, self.ghost_h = self.frames_right[0].size
        self.frame_idx = 0
        self.direction = "right"

        # Transparent window
        self.root.config(bg="black")
        self.root.wm_attributes("-transparentcolor", "black")

        self.canvas = tk.Canvas(
            self.root,
            width=self.ghost_w,
            height=self.ghost_h,
            bg="black",
            highlightthickness=0,
        )
        self.canvas.pack()

        self.tk_frames_right = [ImageTk.PhotoImage(f) for f in self.frames_right]
        self.tk_frames_left  = [ImageTk.PhotoImage(f) for f in self.frames_left]

        sw = self.root.winfo_screenwidth()
        sh = self.root.winfo_screenheight()
        self.sw, self.sh = sw, sh

        self.x = random.randint(0, sw - self.ghost_w)
        self.y = random.randint(0, sh - self.ghost_h)
        self.vx = SPEED * random.choice([-1, 1])
        self.vy = SPEED * random.choice([-1, 1])
        self.tick = 0

        self.img_id = self.canvas.create_image(0, 0, anchor="nw",
                                               image=self.tk_frames_right[0])

        self.canvas.bind("<Button-3>", lambda e: self.root.destroy())
        self.canvas.bind("<Button-1>", self._spook)

        self._move()
        self._animate()
        self.root.mainloop()

    def _spook(self, event):
        self.vx = random.choice([-1, 1]) * (SPEED + 3)
        self.vy = random.choice([-1, 1]) * (SPEED + 3)

    def _animate(self):
        frames = self.tk_frames_right if self.direction == "right" else self.tk_frames_left
        self.canvas.itemconfig(self.img_id, image=frames[self.frame_idx])
        delay = self.delays[self.frame_idx]
        self.frame_idx = (self.frame_idx + 1) % len(frames)
        self.root.after(delay, self._animate)

    def _move(self):
        self.tick += 1

        # Random direction change every ~4 seconds
        if self.tick % (FPS * 4) == 0:
            self.vx = SPEED * random.choice([-1, 1]) * random.uniform(0.8, 1.5)
            self.vy = SPEED * random.choice([-1, 1]) * random.uniform(0.8, 1.5)

        self.x += self.vx
        self.y += self.vy

        # Bounce off screen edges
        if self.x <= 0:
            self.x = 0
            self.vx = abs(self.vx)
        elif self.x >= self.sw - self.ghost_w:
            self.x = self.sw - self.ghost_w
            self.vx = -abs(self.vx)

        if self.y <= 0:
            self.y = 0
            self.vy = abs(self.vy)
        elif self.y >= self.sh - self.ghost_h:
            self.y = self.sh - self.ghost_h
            self.vy = -abs(self.vy)

        self.direction = "right" if self.vx > 0 else "left"
        self.root.geometry(f"+{int(self.x)}+{int(self.y)}")
        self.root.after(1000 // FPS, self._move)


if __name__ == "__main__":
    if not os.path.exists(GIF_PATH):
        print(f"GIF not found: {GIF_PATH}")
        print("Place your ghost GIF in the same folder and name it: download__1_.gif")
        sys.exit(1)
    print("Ghost is haunting your screen!")
    print("Left-click  -> spook it away")
    print("Right-click -> banish it (quit)")
    GhostPet()