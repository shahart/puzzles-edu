import sys
import os
from concurrent.futures import ThreadPoolExecutor
from threading import Barrier

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import pytest
from src.app import create_app


@pytest.fixture
def app():
    app = create_app()
    app.config["TESTING"] = True
    return app


@pytest.fixture
def client(app):
    with app.test_client() as client:
        yield client


def test_get_solve_5_12_returns_200(client):
    response = client.get("/solve/5_12")
    assert response.status_code == 200
    assert response.json == 1


def test_sequential_valid_invalid_valid(client):
    response = client.get("/solve/5_12")
    assert response.status_code == 200
    assert response.json == 1

    response = client.get("/solve/5_13")
    assert response.status_code == 200
    assert response.json == 0

    response = client.get("/solve/5_12")
    assert response.status_code == 200
    assert response.json == 1


def test_handles_solve_requests_concurrently(app):
    problem_ids = ["12_5", "10_6", "15_4", "20_3"]
    start_together = Barrier(len(problem_ids))

    def solve(problem_id):
        with app.test_client() as thread_client:
            start_together.wait()
            response = thread_client.get(f"/solve/{problem_id}")
            return response.status_code, response.json

    with ThreadPoolExecutor(max_workers=len(problem_ids)) as executor:
        responses = list(executor.map(solve, problem_ids))

    assert responses == [(200, 1), (200, 1), (200, 1), (200, 1)]
