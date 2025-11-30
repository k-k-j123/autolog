from flask import Flask
from models import db, User
from flask_login import LoginManager
# Import all of your blueprints
from auth import auth_bp
from vehicle import vehicles_bp
from fuel import fuel_bp
from service import service_bp
from dashboard import dashboard_bp

import os
db_url = os.getenv("DATABASE_URL")

app = Flask(__name__)
app.config['SECRET_KEY'] = 'sk_7f3b9d1e6a2c4f8d0e5a7b2c1d9e6f3a'
app.config['SQLALCHEMY_DATABASE_URI'] = db_url
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db.init_app(app)

login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'auth.login'
login_manager.login_message_category = 'info'

@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

# Register all the blueprints
app.register_blueprint(auth_bp)
app.register_blueprint(vehicles_bp)
app.register_blueprint(fuel_bp)
app.register_blueprint(service_bp)
app.register_blueprint(dashboard_bp)

# Default route: redirect based on authentication
from flask import redirect, url_for
from flask_login import current_user

@app.route('/')
def index():
    if current_user.is_authenticated:
        # Redirect to dashboard for the first vehicle, or to vehicles page if none
        from models import Vehicle
        vehicle = Vehicle.query.filter_by(user_id=current_user.id).first()
        if vehicle:
            return redirect(url_for('dashboard.dashboard', vehicle_id=vehicle.id))
        else:
            return redirect(url_for('vehicles.vehicles'))
    else:
        return redirect(url_for('auth.login'))

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True)