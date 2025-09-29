from werkzeug.security import generate_password_hash, check_password_hash
from flask import render_template, redirect, url_for, flash, request, Blueprint
from flask_login import login_user, logout_user, login_required, current_user
from models import db, User, Vehicle
from sqlalchemy.exc import IntegrityError

# Correctly define the Blueprint
auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/register', methods=['GET', 'POST'])
def register():
    if current_user.is_authenticated:
        # Check if the user has a vehicle to redirect to the dashboard
        first_vehicle = Vehicle.query.filter_by(user_id=current_user.id).first()
        if first_vehicle:
            return redirect(url_for('dashboard.dashboard', vehicle_id=first_vehicle.id))
        else:
            # If not, redirect to the page to add a vehicle
            return redirect(url_for('vehicles.add_vehicle'))

    if request.method == 'POST':
        name = request.form.get('name')
        email = request.form.get('email')
        password = request.form.get('password')
        hashed_password = generate_password_hash(password)
        new_user = User(name=name, email=email, password=hashed_password)
        try:
            db.session.add(new_user)
            db.session.commit()
            flash('Registration successful! Please log in.', 'success')
            return redirect(url_for('auth.login'))
        except IntegrityError:
            db.session.rollback()
            flash('Email already registered. Please log in.', 'danger')
            return redirect(url_for('auth.register'))
    return render_template('register.html')

@auth_bp.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        # Redirect to the dashboard if a user is already authenticated
        first_vehicle = Vehicle.query.filter_by(user_id=current_user.id).first()
        if first_vehicle:
            return redirect(url_for('dashboard.dashboard', vehicle_id=first_vehicle.id))
        else:
            return redirect(url_for('vehicles.add_vehicle'))
            
    if request.method == 'POST':
        email = request.form.get('email')
        password = request.form.get('password')
        user = User.query.filter_by(email=email).first()
        if user and check_password_hash(user.password, password):
            login_user(user)
            flash('Login successful!', 'success')
            
            # Redirect to the dashboard of the first vehicle or to the add vehicle page
            first_vehicle = Vehicle.query.filter_by(user_id=user.id).first()
            if first_vehicle:
                return redirect(url_for('dashboard.dashboard', vehicle_id=first_vehicle.id))
            else:
                return redirect(url_for('vehicles.add_vehicle'))  # Corrected return statement
        else:
            flash('Invalid email or password. Please try again.', 'danger')
            return redirect(url_for('auth.login'))
    return render_template('login.html')

@auth_bp.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('auth.login'))