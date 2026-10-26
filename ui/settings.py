import pygame


class SettingsPanel:
    def __init__(self, screen):
        self.screen = screen
        self.visible = False
        self.volume = 0.8

    def draw(self):
        if not self.visible:
            return
        overlay = pygame.Surface(self.screen.get_size(), pygame.SRCALPHA)
        pygame.draw.rect(overlay, (15, 20, 35, 210), (250, 180, 780, 320), border_radius=20)
        self.screen.blit(overlay, (0, 0))
        title = pygame.font.SysFont(None, 40).render("CONFIGURACIÓN", True, (255, 255, 255))
        self.screen.blit(title, (500, 220))
        volume_text = pygame.font.SysFont(None, 32).render(f"VOLUMEN: {self.volume:.1f}", True, (200, 230, 255))
        self.screen.blit(volume_text, (470, 300))

    def toggle(self):
        self.visible = not self.visible
