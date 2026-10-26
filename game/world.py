from __future__ import annotations

import math

import pygame

from config import SCREEN_HEIGHT, SCREEN_WIDTH
from game.camera import Camera
from game.combat import EnergyBolt, HitSpark
from game.enemies import Enemy
from game.player import Player
from game.physics import resolve_axis_collisions


class LevelWorld:
    def __init__(self, level_data):
        self.level_data = level_data
        self.world_index = level_data["world_index"]
        self.level_index = level_data["level_index"]
        self.width = level_data["level"]["width"]
        self.height = level_data["level"]["height"]
        self.platforms = [pygame.Rect(*item["rect"]) for item in level_data["platforms"]]
        self.start = level_data["start"]
        self.goal = pygame.Rect(*level_data["goal"])
        self.player = Player(*self.start)
        self.camera = Camera(SCREEN_WIDTH, SCREEN_HEIGHT)
        self.enemies = [Enemy(enemy["x"], enemy["y"], enemy["type"]) for enemy in level_data["enemies"]]
        self.coins = []
        for x, y in level_data["coins"]:
            self.coins.append({"rect": pygame.Rect(x, y, 18, 18), "collected": False})
        self.projectiles = []
        self.sparks = []
        self.message = "NEXUS activa el portal"
        self.win = False
        self.completed = False
        self.transition_timer = 0.0

    def update(self, dt, keys):
        self.player.update(dt, keys, self.platforms)
        if keys.get(pygame.K_j):
            self.attack()
        if keys.get(pygame.K_k):
            self.player.shield_timer = max(self.player.shield_timer, 1.1)
            self.player.energy = max(0, self.player.energy - 18 * dt)
        if keys.get(pygame.K_l):
            self.player.slide_timer = max(self.player.slide_timer, 0.25)
        if keys.get(pygame.K_h):
            self.shoot()

        for enemy in self.enemies:
            enemy.update(dt, self.player, self.platforms)
            if enemy.rect.colliderect(self.player.rect):
                self.player.take_damage(12)
                if self.player.rect.centerx < enemy.rect.centerx:
                    self.player.rect.x -= 40
                else:
                    self.player.rect.x += 40

        for coin in self.coins:
            if not coin["collected"] and coin["rect"].colliderect(self.player.rect):
                coin["collected"] = True
                self.player.coins += 1
                self.player.score += 10

        if self.player.rect.colliderect(self.goal):
            self.win = True
            self.completed = True
            self.message = "Nivel completado"
            self.transition_timer = 1.2

        for projectile in list(self.projectiles):
            projectile.update(dt)
            if projectile.x < 0 or projectile.x > self.width:
                self.projectiles.remove(projectile)
            if abs(projectile.x - self.player.rect.centerx) < 20 and abs(projectile.y - self.player.rect.centery) < 20:
                self.player.take_damage(8)
                self.projectiles.remove(projectile)
                self.sparks.append(HitSpark(self.player.rect.centerx, self.player.rect.centery, (255, 100, 150)))

        for spark in list(self.sparks):
            spark.update(dt)
            if spark.life <= 0:
                self.sparks.remove(spark)

        if self.player.health <= 0:
            self.player.lives -= 1
            if self.player.lives <= 0:
                self.player.lives = 3
                self.player.health = 100
                self.player.rect.topleft = self.start
                self.message = "Reintento: vuelve al inicio"
            else:
                self.player.health = 100
                self.player.rect.topleft = self.start
                self.message = "Respawn"

        self.camera.update(self.player.rect, self.width, self.height)

    def attack(self):
        if not self.player.do_attack():
            return
        for enemy in self.enemies:
            attack_rect = pygame.Rect(self.player.rect.centerx + (self.player.facing * 28), self.player.rect.centery - 20, 40, 40)
            if attack_rect.colliderect(enemy.rect):
                if enemy.take_damage(25):
                    self.player.score += 30
                    self.enemies.remove(enemy)
                    self.sparks.append(HitSpark(enemy.rect.centerx, enemy.rect.centery, (150, 255, 200)))
                    break

    def shoot(self):
        if self.player.energy < 10:
            return
        self.player.energy -= 10
        bolt = EnergyBolt(self.player.rect.centerx + self.player.facing * 25, self.player.rect.centery + 8, self.player.facing)
        self.projectiles.append(bolt)

    def draw(self, surface, camera):
        # platforms
        for platform in self.platforms:
            screen_rect = pygame.Rect(platform.x - camera.x, platform.y - camera.y, platform.width, platform.height)
            color = (55, 82, 120) if platform.height > 20 else (90, 180, 255)
            pygame.draw.rect(surface, color, screen_rect, border_radius=8)

        for coin in self.coins:
            if not coin["collected"]:
                rect = coin["rect"]
                pygame.draw.circle(surface, (255, 210, 90), (int(rect.x - camera.x + rect.width // 2), int(rect.y - camera.y + rect.height // 2)), 9)

        for enemy in self.enemies:
            enemy.draw(surface, camera)

        for projectile in self.projectiles:
            projectile.draw(surface, camera)

        for spark in self.sparks:
            spark.draw(surface, camera)

        goal_screen = pygame.Rect(self.goal.x - camera.x, self.goal.y - camera.y, self.goal.width, self.goal.height)
        pygame.draw.rect(surface, (90, 255, 180), goal_screen, border_radius=10)
        self.player.draw(surface, camera)

        hud_text = self.level_data["level"]["name"]
        text = pygame.font.SysFont(None, 26).render(hud_text, True, (255, 255, 255))
        surface.blit(text, (20, 20))
