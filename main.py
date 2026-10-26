import argparse
import sys

import pygame

from config import FPS, SCREEN_HEIGHT, SCREEN_WIDTH, TITLE
from ui.menu import MenuScreen


class GameApp:
    def __init__(self, headless=False):
        self.headless = headless
        pygame.init()
        pygame.mixer.init()
        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
        pygame.display.set_caption(TITLE)
        self.clock = pygame.time.Clock()
        self.running = True
        self.menu = MenuScreen(self.screen)

    def run(self):
        while self.running:
            dt = self.clock.tick(FPS) / 1000.0
            self.handle_events()
            self.menu.update(dt)
            self.menu.draw()
            pygame.display.flip()

        pygame.quit()
        sys.exit()

    def handle_events(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                self.running = False
            self.menu.handle_event(event)


def parse_args():
    parser = argparse.ArgumentParser(description="SUPER NEXUS ADVENTURE")
    parser.add_argument("--headless", action="store_true")
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()
    app = GameApp(headless=args.headless)
    app.run()
