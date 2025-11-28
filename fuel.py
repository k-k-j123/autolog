from flask import render_template, redirect, url_for, flash, request, Blueprint
from flask_login import login_required, current_user
from models import db, Vehicle, FuelLog
from datetime import datetime
from sqlalchemy import func

# Create the Blueprint for fuel log-related routes
fuel_bp = Blueprint('fuel', __name__)

@fuel_bp.route('/add_fuel_log/<int:vehicle_id>', methods=['GET', 'POST'])
@login_required
def add_fuel_log(vehicle_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        # Prevents a user from adding a fuel log to another user's vehicle
        return "Unauthorized", 403
    
    if request.method == 'POST':
        log_date = datetime.strptime(request.form.get('log_date'), '%Y-%m-%d').date()
        odometer = float(request.form.get('odometer'))
        fuel_used = float(request.form.get('fuel_used'))
        fuel_price = float(request.form.get('fuel_price'))

        # Mileage and kilometers calculation
        last_log = FuelLog.query.filter_by(vehicle_id=vehicle_id).order_by(FuelLog.odometer.desc()).first()
        
        kilometers_driven = 0
        mileage = 0
        if last_log:
            kilometers_driven = odometer - last_log.odometer
            if fuel_used > 0:
                mileage = kilometers_driven / fuel_used
        
        new_log = FuelLog(
            vehicle_id=vehicle_id,
            log_date=log_date,
            odometer=odometer,
            fuel_used=fuel_used,
            fuel_price=fuel_price,
            kilometers_driven=kilometers_driven,
            mileage=mileage
        )
        db.session.add(new_log)
        db.session.commit()
        flash('Fuel log added successfully!', 'success')
        return redirect(url_for('dashboard.dashboard', vehicle_id=vehicle_id))
    
    return render_template('fuel_form.html', vehicle=vehicle)

@fuel_bp.route('/all_fuel_logs/<int:vehicle_id>')
@login_required
def all_fuel_logs(vehicle_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        return "Unauthorized", 403
    
    # Fetch all fuel logs, ordered by date descending
    fuel_logs = FuelLog.query.filter_by(vehicle_id=vehicle_id).order_by(FuelLog.log_date.desc()).all()
    
    return render_template('all_fuel_logs.html', vehicle=vehicle, fuel_logs=fuel_logs)

@fuel_bp.route('/edit_fuel_log/<int:vehicle_id>/<int:log_id>', methods=['GET', 'POST'])
@login_required
def edit_fuel_log(vehicle_id, log_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        return "Unauthorized", 403
    
    # Get the specific log
    log = FuelLog.query.get_or_404(log_id)
    
    # Ensure the log belongs to the vehicle
    if log.vehicle_id != vehicle.id:
        return "Unauthorized", 403

    if request.method == 'POST':
        # Update fields
        log.log_date = datetime.strptime(request.form.get('log_date'), '%Y-%m-%d').date()
        log.odometer = float(request.form.get('odometer'))
        log.fuel_used = float(request.form.get('fuel_used'))
        log.fuel_price = float(request.form.get('fuel_price'))
        
        # NOTE: A full implementation for editing an odometer/fuel log would require
        # recalculating the kilometers_driven and mileage for this and all subsequent logs.
        # This basic implementation updates the log but omits the complex cascading recalculation.
        
        db.session.commit()
        flash('Fuel log updated successfully!', 'success')
        return redirect(url_for('fuel.all_fuel_logs', vehicle_id=vehicle_id))