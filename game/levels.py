from __future__ import annotations

from typing import List

from config import SCREEN_HEIGHT, SCREEN_WIDTH

WORLD_DEFS = [
    {
        "id": 1,
        "name": "Bosque Tecnológico",
        "theme": "forest",
        "palette": [(24, 45, 90), (20, 128, 142), (122, 255, 190), (14, 26, 42)],
        "levels": [
            {"id": 1, "name": "Sendero Luminoso", "width": 2400, "height": 720},
            {"id": 2, "name": "Cascadas Digitales", "width": 2600, "height": 720},
            {"id": 3, "name": "Poblado Synthex", "width": 2800, "height": 720},
        ],
    },
    {
        "id": 2,
        "name": "Ciudad Futurista",
        "theme": "city",
        "palette": [(8, 10, 26), (32, 67, 120), (16, 203, 214), (120, 140, 255)],
        "levels": [
            {"id": 1, "name": "Tráfico Aéreo", "width": 2700, "height": 720},
            {"id": 2, "name": "Láseres", "width": 2900, "height": 720},
            {"id": 3, "name": "Torres Elevadas", "width": 3200, "height": 720},
        ],
    },
    {
        "id": 3,
        "name": "Volcán Infernal",
        "theme": "volcano",
        "palette": [(28, 21, 17), (98, 56, 18), (255, 121, 48), (255, 216, 92)],
        "levels": [
            {"id": 1, "name": "Lava Eterna", "width": 2550, "height": 720},
            {"id": 2, "name": "Rocas Caídas", "width": 2850, "height": 720},
            {"id": 3, "name": "Cráter Final", "width": 3100, "height": 720},
        ],
    },
    {
        "id": 4,
        "name": "Dimensión Espacial",
        "theme": "space",
        "palette": [(5, 10, 32), (18, 32, 80), (72, 206, 255), (185, 120, 255)],
        "levels": [
            {"id": 1, "name": "Orbita Oscura", "width": 2800, "height": 720},
            {"id": 2, "name": "Meteoritos", "width": 3000, "height": 720},
            {"id": 3, "name": "Portal Inestable", "width": 3300, "height": 720},
        ],
    },
    {
        "id": 5,
        "name": "Fortaleza Final",
        "theme": "fortress",
        "palette": [(13, 15, 28), (42, 90, 110), (84, 255, 202), (219, 38, 67)],
        "levels": [
            {"id": 1, "name": "Puertas Cerradas", "width": 3000, "height": 720},
            {"id": 2, "name": "Trampas de Miedo", "width": 3300, "height": 720},
            {"id": 3, "name": "Jefe: Aegis", "width": 3600, "height": 720},
        ],
    },
]

LEVEL_SEQUENCE = []
for world_index, world in enumerate(WORLD_DEFS):
    for level_index in range(len(world["levels"])):
        LEVEL_SEQUENCE.append((world_index, level_index))


def get_next_level(world_index, level_index):
    current = (world_index, level_index)
    if current not in LEVEL_SEQUENCE:
        return None
    current_position = LEVEL_SEQUENCE.index(current)
    if current_position + 1 >= len(LEVEL_SEQUENCE):
        return None
    return LEVEL_SEQUENCE[current_position + 1]


def build_platforms_for_level(world, level_idx):
    width = level_idx["width"]
    level_number = level_idx["id"]
    world_id = world["id"]
    y_ground = 640
    platforms = [
        {"rect": (0, y_ground, width, 120), "type": "ground"},
    ]
    base_positions = [
        (200, 560), (420, 520), (700, 470), (950, 540), (1180, 470), (1450, 520), (1750, 560), (2000, 490), (2280, 430)
    ]
    for index, (x, y) in enumerate(base_positions):
        width_platform = 140 + ((index + world_id) % 3) * 40
        platforms.append({"rect": (x, y, width_platform, 24), "type": "platform"})
    if world_id in (2, 3, 5):
        for x in range(500, width, 520):
            platforms.append({"rect": (x, 430 + (x % 140), 120, 18), "type": "moving"})
    return platforms


def build_level(world_index, level_index):
    world = WORLD_DEFS[world_index]
    level_data = world["levels"][level_index]
    return {
        "world_index": world_index,
        "level_index": level_index,
        "world": world,
        "level": level_data,
        "platforms": build_platforms_for_level(world, level_data),
        "goal": (level_data["width"] - 150, 540, 70, 100),
        "start": (60, 500),
        "coins": [
            (250, 510), (520, 470), (760, 420), (1220, 430), (1680, 510), (2050, 430), (2300, 380), (2500, 530)
        ],
        "enemies": [
            {"type": "patrol", "x": 620, "y": 590},
            {"type": "patrol", "x": 1100, "y": 590},
            {"type": "drone", "x": 1900, "y": 300},
            {"type": "turret", "x": 2400, "y": 470},
        ],
    }


def get_worlds_total():
    return len(WORLD_DEFS)


def get_levels_total():
    return sum(len(world["levels"]) for world in WORLD_DEFS)
