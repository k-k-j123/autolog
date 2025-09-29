from datetime import date, datetime
from flask import render_template, redirect, url_for, flash, request, Blueprint
from flask_login import login_required, current_user
from models import db, Vehicle

# Create the Blueprint for vehicle-related routes
vehicles_bp = Blueprint('vehicles', __name__)

@vehicles_bp.route('/vehicles')
@login_required
def vehicles():
    # Only fetch vehicles belonging to the current user
    user_vehicles = Vehicle.query.filter_by(user_id=current_user.id).all()
    return render_template('vehicles.html', vehicles=user_vehicles)

@vehicles_bp.route('/add_vehicle', methods=['GET', 'POST'])
@login_required
def add_vehicle():
    if request.method == 'POST':
        name = request.form.get('name')
        vehicle_number = request.form.get('vehicle_number')
        # The date from the form comes as a string, convert it to a date object
        purchase_date = datetime.strptime(request.form.get('purchase_date'), '%Y-%m-%d').date()
        
        new_vehicle = Vehicle(
            user_id=current_user.id,
            name=name,
            vehicle_number=vehicle_number,
            purchase_date=purchase_date
        )
        db.session.add(new_vehicle)
        db.session.commit()
        flash('Vehicle added successfully!', 'success')
        return redirect(url_for('vehicles.vehicles'))
    return render_template('add_vehicle.html')

@vehicles_bp.route('/delete_vehicle/<int:vehicle_id>', methods=['POST'])
@login_required
def delete_vehicle(vehicle_id):
    # Find the vehicle and ensure it belongs to the current user
    vehicle = Vehicle.query.filter_by(id=vehicle_id, user_id=current_user.id).first_or_404()
    
    # Delete the vehicle and commit the change
    db.session.delete(vehicle)
    db.session.commit()
    flash('Vehicle deleted successfully!', 'success')
    return redirect(url_for('vehicles.vehicles'))