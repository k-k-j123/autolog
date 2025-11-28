from datetime import datetime
from flask import render_template, redirect, url_for, flash, request, Blueprint
from flask_login import login_required, current_user
from models import db, Service, Vehicle

# Create the Blueprint for service-related routes
service_bp = Blueprint('service', __name__)

@service_bp.route('/add_service/<int:vehicle_id>', methods=['GET', 'POST'])
@login_required
def add_service(vehicle_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        return "Unauthorized", 403

    if request.method == 'POST':
        service_date = datetime.strptime(request.form.get('service_date'), '%Y-%m-%d').date()
        cost = request.form.get('cost')
        service_type = request.form.get('service_type')
        notes = request.form.get('notes')

        new_service = Service(
            vehicle_id=vehicle_id,
            service_date=service_date,
            cost=cost,
            service_type=service_type,
            notes=notes
        )
        db.session.add(new_service)
        db.session.commit()
        flash('Service entry added successfully!', 'success')
        # Assuming you have a dashboard blueprint named 'dashboard'
        return redirect(url_for('dashboard.dashboard', vehicle_id=vehicle_id))

    return render_template('service_form.html', vehicle=vehicle, log=None) # Added log=None

@service_bp.route('/all_service_logs/<int:vehicle_id>')
@login_required
def all_service_logs(vehicle_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        return "Unauthorized", 403
    
    service_logs = Service.query.filter_by(vehicle_id=vehicle_id).order_by(Service.service_date.desc()).all()
    
    return render_template('all_service_logs.html', vehicle=vehicle, service_logs=service_logs)

@service_bp.route('/edit_service_log/<int:vehicle_id>/<int:log_id>', methods=['GET', 'POST'])
@login_required
def edit_service_log(vehicle_id, log_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        return "Unauthorized", 403
    
    log = Service.query.get_or_404(log_id)
    
    if log.vehicle_id != vehicle.id:
        return "Unauthorized", 403

    if request.method == 'POST':
        log.service_date = datetime.strptime(request.form.get('service_date'), '%Y-%m-%d').date()
        log.cost = request.form.get('cost')
        log.service_type = request.form.get('service_type')
        log.notes = request.form.get('notes')
        
        db.session.commit()
        flash('Service log updated successfully!', 'success')
        return redirect(url_for('service.all_service_logs', vehicle_id=vehicle_id))
        
    return render_template('service_form.html', vehicle=vehicle, log=log)