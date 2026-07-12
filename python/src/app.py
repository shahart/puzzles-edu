from flask import Flask

from src.routes.solve import solve_bp


def create_app() -> Flask:
    app = Flask(__name__)
    app.register_blueprint(solve_bp)
    return app


if __name__ == "__main__":
    import os

    app = create_app()
    port = int(os.environ.get("PORT", 8080))
    print(f"Puzzle solver server running on http://localhost:{port}")
    app.run(port=port)
