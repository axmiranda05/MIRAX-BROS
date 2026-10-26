from __future__ import annotations

from typing import Iterable


def resolve_axis_collisions(entity, solid_objects, axis: str, dt: float):
    if axis == 'x':
        entity.rect.x += entity.vx * dt
        for obj in solid_objects:
            if entity.rect.colliderect(obj):
                if entity.vx > 0:
                    entity.rect.right = obj.left
                elif entity.vx < 0:
                    entity.rect.left = obj.right
                entity.vx = 0
    else:
        entity.rect.y += entity.vy * dt
        for obj in solid_objects:
            if entity.rect.colliderect(obj):
                if entity.vy > 0:
                    entity.rect.bottom = obj.top
                    entity.on_ground = True
                elif entity.vy < 0:
                    entity.rect.top = obj.bottom
                entity.vy = 0


def rects_overlap(a, b):
    return a.x < b.right and a.right > b.x and a.y < b.bottom and a.bottom > b.y


def get_platforms(level_data):
    return [platform["rect"] for platform in level_data.get("platforms", [])]


def clamp(value, minimum, maximum):
    return max(minimum, min(value, maximum))
