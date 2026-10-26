from __future__ import annotations

import pygame

from config import ATTACK_DAMAGE, DOUBLE_JUMP_SPEED, GRAVITY, JUMP_SPEED, MAX_ENERGY, MAX_HEALTH, PLAYER_SPEED, RUN_SPEED, SCREEN_HEIGHT, SLIDE_SPEED


class Player:
    def __init__(self, x, y):
        self.rect = pygame.Rect(x, y, 42, 58)
        self.vx = 0.0
        self.vy = 0.0
        self.speed = PLAYER_SPEED
        self.facing = 1
        self.on_ground = False
        self.jump_count = 0
        self.attack_cooldown = 0
        self.slide_timer = 0
        self.invulnerable_timer = 0
        self.health = MAX_HEALTH
        self.energy = MAX_ENERGY
        self.score = 0
        self.coins = 0
        self.lives = 3
        self.shield_timer = 0
        self.was_jump = False

    def update(self, dt, keys, platforms):
        move_left = keys[pygame.K_LEFT] or keys[pygame.K_a]
        move_right = keys[pygame.K_RIGHT] or keys[pygame.K_d]
        running = keys[pygame.K_LSHIFT]
        jump_pressed = keys[pygame.K_SPACE]
        slide_pressed = keys[pygame.K_l]
        attack_pressed = keys[pygame.K_j]
        shield_pressed = keys[pygame.K_k]

        self.vx = 0
        if move_left:
            self.vx = -self.speed
            self.facing = -1
        if move_right:
            self.vx = self.speed
            self.facing = 1
        if running:
            self.speed = RUN_SPEED
        else:
            self.speed = PLAYER_SPEED

        if slide_pressed and self.on_ground:
            self.slide_timer = 0.45
            self.vx = self.facing * SLIDE_SPEED
            self.energy = max(0, self.energy - 2)

        if self.on_ground:
            self.jump_count = 0

        if jump_pressed and not self.was_jump:
            if self.on_ground:
                self.vy = -JUMP_SPEED
                self.on_ground = False
                self.jump_count = 1
            elif self.jump_count < 2 and self.energy >= 25:
                self.vy = -DOUBLE_JUMP_SPEED
                self.jump_count += 1
                self.energy -= 25

        if attack_pressed:
            self.do_attack()

        if shield_pressed and self.energy >= 12:
            self.shield_timer = 2.0
            self.energy -= 12

        self.was_jump = jump_pressed

        if self.attack_cooldown > 0:
            self.attack_cooldown -= dt
        if self.slide_timer > 0:
            self.slide_timer -= dt
        if self.invulnerable_timer > 0:
            self.invulnerable_timer -= dt
        if self.shield_timer > 0:
            self.shield_timer -= dt

        self.rect.x += self.vx * dt
        for platform in platforms:
            if self.rect.colliderect(platform):
                if self.vx > 0:
                    self.rect.right = platform.left
                elif self.vx < 0:
                    self.rect.left = platform.right

        self.vy += GRAVITY * dt
        self.rect.y += self.vy * dt
        self.on_ground = False
        for platform in platforms:
            if self.rect.colliderect(platform):
                if self.vy > 0:
                    self.rect.bottom = platform.top
                    self.on_ground = True
                elif self.vy < 0:
                    self.rect.top = platform.bottom
                self.vy = 0

        self.energy = min(MAX_ENERGY, self.energy + dt * 7)

        if self.rect.y > SCREEN_HEIGHT + 200:
            self.health = 0

    def do_attack(self):
        if self.attack_cooldown <= 0:
            self.attack_cooldown = 0.35
            return True
        return False

    def take_damage(self, amount):
        if self.invulnerable_timer > 0:
            return
        if self.shield_timer > 0:
            amount = max(5, amount // 2)
        self.health -= amount
        self.invulnerable_timer = 1.0
        self.shield_timer = max(0, self.shield_timer - 0.4)

    def draw(self, surface, camera):
        body = pygame.Rect(int(self.rect.x - camera.x), int(self.rect.y - camera.y), self.rect.width, self.rect.height)
        body_color = (110, 220, 255) if self.shield_timer > 0 else (80, 200, 255)
        pygame.draw.rect(surface, body_color, body, border_radius=12)
        eye_offset = 10 if self.facing > 0 else -10
        eye_y = body.y + 16
        pygame.draw.circle(surface, (255, 255, 255), (body.x + 13 + eye_offset, eye_y), 4)
        pygame.draw.circle(surface, (80, 250, 200), (body.x + 13 + eye_offset, eye_y), 2)
        if self.slide_timer > 0:
            pygame.draw.rect(surface, (80, 210, 255), (body.x, body.bottom - 8, body.width, 8), border_radius=4)
