import math

import pygame

from config import SCREEN_HEIGHT, SCREEN_WIDTH
from game.levels import WORLD_DEFS, build_level, get_next_level
from game.world import LevelWorld
from ui.hud import draw_hud
from ui.settings import SettingsPanel


class Button:
    def __init__(self, x, y, w, h, text, color, hover_color):
        self.rect = pygame.Rect(x, y, w, h)
        self.text = text
        self.color = color
        self.hover_color = hover_color
        self.hover = False

    def draw(self, surface):
        color = self.hover_color if self.hover else self.color
        pygame.draw.rect(surface, color, self.rect, border_radius=12)
        label = pygame.font.SysFont(None, 28).render(self.text, True, (255, 255, 255))
        surface.blit(label, (self.rect.centerx - label.get_width() // 2, self.rect.centery - label.get_height() // 2))


class MenuScreen:
    def __init__(self, screen):
        self.screen = screen
        self.font = pygame.font.SysFont(None, 42)
        self.title_font = pygame.font.SysFont(None, 76)
        self.buttons = [
            Button(80, 180, 260, 58, "NUEVA PARTIDA", (33, 149, 255), (35, 195, 255)),
            Button(80, 250, 260, 58, "CONTINUAR", (40, 109, 174), (74, 152, 255)),
            Button(80, 320, 260, 58, "SELECCIONAR NIVEL", (9, 76, 120), (30, 120, 180)),
            Button(80, 390, 260, 58, "CONFIGURACIÓN", (23, 96, 70), (20, 146, 106)),
            Button(80, 460, 260, 58, "CONTROLES", (90, 92, 105), (145, 145, 175)),
            Button(80, 530, 260, 58, "SALIR", (120, 42, 56), (220, 90, 90)),
        ]
        self.selected = 0
        self.pulse = 0
        self.game_world = None
        self.settings = SettingsPanel(screen)
        self.active = "menu"
        self.keys = {key: False for key in range(512)}
        self.camera = None
        self.victory = False

    def handle_event(self, event):
        if event.type == pygame.MOUSEMOTION:
            for idx, button in enumerate(self.buttons):
                button.hover = button.rect.collidepoint(event.pos)
                if button.hover:
                    self.selected = idx
        elif event.type == pygame.MOUSEBUTTONDOWN and event.button == 1:
            if self.game_world is not None:
                return
            self.activate_button(self.selected)
        elif event.type == pygame.KEYDOWN:
            self.keys[event.key] = True
            if self.victory:
                if event.key in (pygame.K_RETURN, pygame.K_SPACE, pygame.K_ESCAPE):
                    self.victory = False
                    self.active = "menu"
                    self.game_world = None
                return
            if self.game_world is None:
                if event.key in (pygame.K_UP, pygame.K_w):
                    self.selected = (self.selected - 1) % len(self.buttons)
                elif event.key in (pygame.K_DOWN, pygame.K_s):
                    self.selected = (self.selected + 1) % len(self.buttons)
                elif event.key in (pygame.K_RETURN, pygame.K_SPACE):
                    self.activate_button(self.selected)
                elif event.key == pygame.K_ESCAPE:
                    self.active = "menu"
                    self.game_world = None
            else:
                if event.key == pygame.K_ESCAPE:
                    self.game_world = None
                    self.active = "menu"
                elif event.key == pygame.K_r:
                    self.start_new_game()
        elif event.type == pygame.KEYUP:
            self.keys[event.key] = False

    def activate_button(self, index):
        action = self.buttons[index].text
        if action == "NUEVA PARTIDA":
            self.start_new_game()
        elif action == "CONTINUAR":
            self.start_new_game()
        elif action == "SELECCIONAR NIVEL":
            self.start_new_game(world_index=4, level_index=2)
        elif action == "CONFIGURACIÓN":
            self.settings.toggle()
        elif action == "CONTROLES":
            self.active = "controls"
        elif action == "SALIR":
            pygame.quit()
            raise SystemExit

    def start_new_game(self, world_index=0, level_index=0):
        level_data = build_level(world_index, level_index)
        self.game_world = LevelWorld(level_data)
        self.game_world.camera.update(self.game_world.player.rect, self.game_world.width, self.game_world.height)
        self.active = "game"

    def update(self, dt):
        self.pulse += dt
        if self.game_world is not None:
            self.keys = {key: bool(self.keys.get(key, False)) for key in range(512)}
            self.game_world.update(dt, self.keys)
            if self.game_world.win:
                self.game_world.message = "Portal abierto. Nivel terminado."
                next_stage = get_next_level(self.game_world.world_index, self.game_world.level_index)
                if next_stage is None:
                    self.victory = True
                    self.game_world = None
                    self.active = "victory"
                else:
                    self.start_new_game(*next_stage)
            if not self.game_world.player.health and self.game_world.player.lives <= 0:
                self.game_world = None
            return

    def draw(self):
        if self.game_world is not None:
            self.screen.fill((8, 12, 20))
            world_color = self.game_world.level_data["world"]["palette"][0]
            self.screen.fill(world_color)
            for i in range(5):
                y = 100 + i * 120 + int(math.sin(self.pulse * 1.5 + i) * 18)
                pygame.draw.rect(self.screen, (255, 255, 255, 60), (0, y, SCREEN_WIDTH, 2), 1)
            camera = self.game_world.camera if hasattr(self.game_world, 'camera') else type('Camera', (), {'x': 0, 'y': 0})()
            if not hasattr(self.game_world, 'camera'):
                camera.x = 0
                camera.y = 0
            self.game_world.draw(self.screen, camera)
            draw_hud(self.screen, self.game_world)
            self.settings.draw()
            return

        if self.victory:
            self.screen.fill((7, 10, 25))
            title = self.title_font.render("VICTORIA", True, (92, 255, 183))
            self.screen.blit(title, (430, 180))
            subtitle = self.font.render("MIRAX BROS HA COMPLETADO EL FINAL", True, (255, 255, 255))
            self.screen.blit(subtitle, (320, 290))
            hint = self.font.render("Pulsa ENTER para volver al menú", True, (180, 210, 255))
            self.screen.blit(hint, (380, 360))
            return

        self.screen.fill((7, 9, 25))
        for i in range(20):
            x = i * 80 + int(self.pulse * 50) % 120
            y = 100 + (i % 4) * 100 + int(math.sin(self.pulse + i) * 30)
            pygame.draw.circle(self.screen, (80, 160, 255), (x, y), 2)
        title = self.title_font.render("MIRAX", True, (150, 230, 255))
        self.screen.blit(title, (90, 70))
        subtitle = self.font.render("BROS", True, (104, 255, 195))
        self.screen.blit(subtitle, (90, 150))
        for idx, button in enumerate(self.buttons):
            if idx == self.selected:
                button.hover = True
            else:
                button.hover = False
            button.draw(self.screen)

        if self.active == "controls":
            help_surface = pygame.Surface((400, 380), pygame.SRCALPHA)
            pygame.draw.rect(help_surface, (16, 22, 40, 220), (0, 0, 400, 380), border_radius=18)
            self.screen.blit(help_surface, (480, 120))
            controls = [
                "A / ← : Izquierda",
                "D / → : Derecha",
                "ESPACIO: Saltar",
                "ESPACIO x2: Doble salto",
                "SHIFT: Correr",
                "J: Atacar",
                "K: Habilidad",
                "L: Deslizamiento",
                "ESC: Menu",
                "R: Reiniciar",
            ]
            for i, line in enumerate(controls):
                txt = self.font.render(line, True, (220, 230, 255))
                self.screen.blit(txt, (510, 160 + i * 28))

        self.settings.draw()
