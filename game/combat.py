from __future__ import annotations

import math

import pygame


class EnergyBolt:
    def __init__(self, x, y, direction, speed=700):
        self.x = x
        self.y = y
        self.direction = direction
        self.speed = speed
        self.radius = 7
        self.lifetime = 1.4
        self.alive = True

    def update(self, dt):
        self.x += self.direction * self.speed * dt
        self.lifetime -= dt
        self.alive = self.lifetime > 0

    def draw(self, surface, camera):
        if not self.alive:
            return
        rect = pygame.Rect(int(self.x - self.radius), int(self.y - self.radius), self.radius * 2, self.radius * 2)
        pygame.draw.circle(surface, (120, 250, 255), (rect.centerx - camera.x, rect.centery - camera.y), self.radius)
        pygame.draw.circle(surface, (255, 255, 255), (rect.centerx - camera.x, rect.centery - camera.y), self.radius - 3, 2)


class HitSpark:
    def __init__(self, x, y, color):
        self.x = x
        self.y = y
        self.color = color
        self.life = 0.35
        self.particles = []
        for _ in range(8):
            angle = (math.pi * 2) * _ / 8
            self.particles.append({
                "x": x,
                "y": y,
                "vx": math.cos(angle) * (70 + _ * 10),
                "vy": math.sin(angle) * (70 + _ * 10),
            })

    def update(self, dt):
        self.life -= dt
        for p in self.particles:
            p["x"] += p["vx"] * dt
            p["y"] += p["vy"] * dt
            p["vy"] += 180 * dt

    def draw(self, surface, camera):
        for p in self.particles:
            pygame.draw.circle(surface, self.color, (int(p["x"] - camera.x), int(p["y"] - camera.y)), 3)
