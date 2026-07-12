import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import pytest
from src.app import create_app
from src.core.piece import Piece


@pytest.fixture
def client():
    app = create_app()
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client


def setup_function():
    Piece.total_fill = 0


def test_get_solve_5_12_returns_200(client):
    Piece.total_fill = 0
    response = client.get("/solve/5_12")
    assert response.status_code == 200
    assert response.json == 1


def test_sequential_valid_invalid_valid(client):
    Piece.total_fill = 0
    response = client.get("/solve/5_12")
    assert response.status_code == 200
    assert response.json == 1

    Piece.total_fill = 0
    response = client.get("/solve/5_13")
    assert response.status_code == 200
    assert response.json == 0

    Piece.total_fill = 0
    response = client.get("/solve/5_12")
    assert response.status_code == 200
    assert response.json == 1
