import React, { useState } from 'react';
import { parkingService } from '../services/api';

const BookingModal = ({ slot, onClose, onSuccess }) => {
  const [vehicleNo, setVehicleNo] = useState('');
  const [duration, setDuration] = useState(30);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!vehicleNo.trim()) {
      alert("Please enter a vehicle number");
      return;
    }

    setLoading(true);
    try {
      await parkingService.bookSlot({
        slotId: slot.id,
        vehicleNo: vehicleNo.toUpperCase(),
        durationMinutes: duration,
      });
      alert(`Slot ${slot.slotLabel} reserved successfully!`);
      onSuccess();
    } catch (err) {
      console.error("Booking failed", err);
      alert("Slot is no longer available.");
      onClose();
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>🅿️ Reserve Parking Slot</h3>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div style={{
              background: 'var(--bg-tertiary)',
              borderRadius: 'var(--radius-md)',
              padding: 'var(--space-4)',
              display: 'flex',
              alignItems: 'center',
              gap: 'var(--space-4)',
              marginBottom: 'var(--space-4)'
            }}>
              <div style={{ fontSize: '2rem' }}>
                {slot.type === 'handicapped' ? '♿' : '🅿️'}
              </div>
              <div>
                <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>
                  Slot {slot.slotLabel}
                </div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-tertiary)' }}>
                  {slot.type === 'handicapped' ? 'Handicapped Accessible' : 'Standard Parking'}
                </div>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="vehicle-number">Vehicle Number</label>
              <input 
                type="text" 
                id="vehicle-number" 
                placeholder="e.g. KA-01-AB-1234" 
                autoComplete="off" 
                maxLength="16"
                value={vehicleNo}
                onChange={(e) => setVehicleNo(e.target.value)}
                autoFocus
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="duration-select">Reservation Duration</label>
              <select 
                id="duration-select"
                value={duration}
                onChange={(e) => setDuration(parseInt(e.target.value, 10))}
              >
                <option value="15">15 Minutes</option>
                <option value="30">30 Minutes</option>
                <option value="60">1 Hour</option>
                <option value="120">2 Hours</option>
                <option value="240">4 Hours</option>
              </select>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-ghost" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Processing...' : 'Confirm Reservation'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BookingModal;
