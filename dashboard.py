from sqlalchemy import func
from datetime import timedelta, datetime
from flask import render_template, Blueprint
from flask_login import login_required, current_user
from models import db, Vehicle, Service, FuelLog

# Create the Blueprint for the dashboard
dashboard_bp = Blueprint('dashboard', __name__)

@dashboard_bp.route('/dashboard/<int:vehicle_id>')
@login_required
def dashboard(vehicle_id):
    vehicle = Vehicle.query.get_or_404(vehicle_id)
    if vehicle.user_id != current_user.id:
        return "Unauthorized", 403
    
    # 1. Total KMs Driven (latest odometer value)
    latest_fuel_log = FuelLog.query.filter_by(vehicle_id=vehicle_id).order_by(FuelLog.odometer.desc()).first()
    total_kms = round(latest_fuel_log.odometer, 2) if latest_fuel_log and latest_fuel_log.odometer is not None else 0

    # 2. Last Service Date
    last_service = Service.query.filter_by(vehicle_id=vehicle_id).order_by(Service.service_date.desc()).first()

    # 3. Current Mileage (Last entry)
    current_mileage = round(latest_fuel_log.mileage, 2) if latest_fuel_log and latest_fuel_log.mileage is not None else 0

    # 4. Data for the chart (past 3 months)
    three_months_ago = datetime.now() - timedelta(days=90)
    chart_data = db.session.query(FuelLog.log_date, FuelLog.mileage, FuelLog.fuel_price * FuelLog.fuel_used).filter(
        FuelLog.vehicle_id == vehicle_id,
        FuelLog.log_date >= three_months_ago.date()
    ).order_by(FuelLog.log_date).all()

    labels = [d.strftime('%Y-%m-%d') for d, _, _ in chart_data]
    mileage_data = [float(m) for _, m, _ in chart_data]
    cost_data = [float(c) for _, _, c in chart_data]

    return render_template(
        'dashboard.html',
        vehicle=vehicle,
        total_kms=total_kms,
        last_service_date=last_service.service_date if last_service else None,
        current_mileage=current_mileage,
        labels=labels,
        mileage_data=mileage_data,
        cost_data=cost_data
    )