import json
from pathlib import Path


class SaveSystem:
    def __init__(self, path="save.json"):
        self.path = Path(path)

    def default_save(self):
        return {
            "world_index": 0,
            "level_index": 0,
            "unlocked_worlds": [0],
            "score": 0,
            "coins": 0,
            "lives": 3,
            "abilities": {
                "double_jump": True,
                "dash": False,
                "slide": True,
                "shield": False,
            },
            "volume": 0.8,
        }

    def load_progress(self):
        if not self.path.exists():
            return self.default_save()
        try:
            with self.path.open("r", encoding="utf-8") as file:
                data = json.load(file)
            defaults = self.default_save()
            defaults.update(data)
            return defaults
        except json.JSONDecodeError:
            return self.default_save()

    def save_progress(self, data):
        self.path.parent.mkdir(parents=True, exist_ok=True)
        with self.path.open("w", encoding="utf-8") as file:
            json.dump(data, file, indent=2)
