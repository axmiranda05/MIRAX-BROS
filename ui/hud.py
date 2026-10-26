import pygame

from config import SCREEN_WIDTH


def draw_hud(surface, world, font=None):
    font = font or pygame.font.SysFont(None, 28)
    player = world.player
    bar_w = 260
    bar_h = 18
    health_ratio = max(0, player.health / 100)
    energy_ratio = max(0, player.energy / 100)

    shadow = pygame.Rect(20, 20, bar_w + 10, bar_h + 10)
    pygame.draw.rect(surface, (0, 0, 0, 80), shadow, border_radius=10)

    health = pygame.Rect(30, 30, int(bar_w * health_ratio), bar_h)
    bg_health = pygame.Rect(30, 30, bar_w, bar_h)
    pygame.draw.rect(surface, (24, 30, 40), bg_health, border_radius=8)
    pygame.draw.rect(surface, (80, 245, 160), health, border_radius=8)

    energy = pygame.Rect(30, 60, int(bar_w * energy_ratio), bar_h)
    bg_energy = pygame.Rect(30, 60, bar_w, bar_h)
    pygame.draw.rect(surface, (24, 30, 40), bg_energy, border_radius=8)
    pygame.draw.rect(surface, (110, 190, 255), energy, border_radius=8)

    surface.blit(font.render(f"VIDA {int(player.health)}/100", True, (255, 255, 255)), (30, 6))
    surface.blit(font.render(f"ENERGÍA {int(player.energy)}/100", True, (255, 255, 255)), (30, 36))
    surface.blit(font.render(f"MONEDAS {player.coins}", True, (255, 213, 99)), (1020, 22))
    surface.blit(font.render(f"PUNTOS {player.score}", True, (255, 255, 255)), (1020, 52))
    surface.blit(font.render(f"VIDAS {player.lives}", True, (255, 120, 120)), (1020, 82))
    surface.blit(font.render(f"MUNDO {world.level_data['world']['name']}", True, (160, 220, 255)), (780, 22))

    if world.message:
        msg = font.render(world.message, True, (255, 255, 255))
        surface.blit(msg, (SCREEN_WIDTH // 2 - msg.get_width() // 2, 12))
