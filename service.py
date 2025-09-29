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

    return render_template('service_form.html', vehicle=vehicle)