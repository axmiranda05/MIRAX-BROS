from config import SCREEN_WIDTH, SCREEN_HEIGHT
from game.levels import WORLD_DEFS, LEVEL_SEQUENCE, get_next_level
from game.save_system import SaveSystem


def test_screen_size():
    assert SCREEN_WIDTH == 1280
    assert SCREEN_HEIGHT == 720


def test_worlds_available():
    assert len(WORLD_DEFS) >= 5
    assert all(world["id"] for world in WORLD_DEFS)


def test_level_progression_reaches_final():
    assert len(LEVEL_SEQUENCE) >= 15
    assert LEVEL_SEQUENCE[0] == (0, 0)
    assert LEVEL_SEQUENCE[-1] == (4, 2)
    assert get_next_level(0, 0) == (0, 1)
    assert get_next_level(4, 2) is None


def test_save_round_trip(tmp_path):
    save = SaveSystem(tmp_path / "save.json")
    save.save_progress({
        "world_index": 2,
        "unlocked_worlds": [1, 2],
        "score": 250,
        "coins": 42,
    })

    data = save.load_progress()
    assert data["world_index"] == 2
    assert data["score"] == 250
    assert data["coins"] == 42
