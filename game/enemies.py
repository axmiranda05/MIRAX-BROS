from __future__ import annotations

import math

import pygame


class Enemy:
    def __init__(self, x, y, enemy_type="patrol"):
        self.type = enemy_type
        self.rect = pygame.Rect(x, y, 36, 36)
        self.speed = 70 if enemy_type == "patrol" else 110
        self.direction = 1
        self.health = 50
        self.attack_timer = 0
        self.base_y = y
        self.float_phase = 0

    def update(self, dt, player, platforms):
        if self.type == "patrol":
            self.rect.x += self.direction * self.speed * dt
            if self.rect.x < 0 or self.rect.x > 2600:
                self.direction *= -1
        elif self.type == "drone":
            self.float_phase += dt * 3
            self.rect.y = self.base_y + math.sin(self.float_phase) * 50
            self.rect.x += self.direction * self.speed * dt * 0.8
            if self.rect.x < 0 or self.rect.x > 2600:
                self.direction *= -1
        self.attack_timer = max(0, self.attack_timer - dt)

    def draw(self, surface, camera):
        pos = (int(self.rect.x - camera.x), int(self.rect.y - camera.y))
        if self.type == "patrol":
            color = (255, 95, 95)
            pygame.draw.rect(surface, color, (pos[0], pos[1], self.rect.width, self.rect.height), border_radius=8)
            pygame.draw.circle(surface, (255, 220, 130), (pos[0] + 12, pos[1] + 12), 4)
        elif self.type == "drone":
            color = (120, 200, 255)
            pygame.draw.circle(surface, color, pos, 20)
            pygame.draw.circle(surface, (255, 255, 255), (pos[0] + 8, pos[1] - 4), 5)
        else:
            color = (255, 170, 40)
            pygame.draw.rect(surface, color, (pos[0], pos[1], self.rect.width, self.rect.height), border_radius=12)

    def take_damage(self, amount):
        self.health -= amount
        return self.health <= 0
