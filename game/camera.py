from __future__ import annotations

import pygame


class Camera:
    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.x = 0
        self.y = 0
        self.shake = 0.0

    def update(self, target_rect, level_width, level_height):
        self.x = target_rect.centerx - self.width // 2
        self.y = target_rect.centery - self.height // 2
        self.x = max(0, min(self.x, max(0, level_width - self.width)))
        self.y = max(0, min(self.y, max(0, level_height - self.height)))

        if self.shake > 0:
            self.x += (pygame.random.randint(-10, 10) * self.shake)
            self.y += (pygame.random.randint(-10, 10) * self.shake)
            self.shake *= 0.85

    def screen_rect(self):
        return pygame.Rect(self.x, self.y, self.width, self.height)
