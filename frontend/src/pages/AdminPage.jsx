import React, { useState, useEffect } from 'react';
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as ReTooltip, ResponsiveContainer,
  PieChart, Pie, Cell
} from 'recharts';
import { parkingService } from '../services/api';

const AdminPage = () => {
  const [locations, setLocations] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [globalStats, setGlobalStats] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [activeSlots, setActiveSlots] = useState([]);
  const [bookingFilter, setBookingFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initData = async () => {
      try {
        const [locsRes, statsRes, bookingsRes] = await Promise.all([
          parkingService.getLocations(),
          parkingService.getGlobalStats(),
          parkingService.getActiveBookings()
        ]);
        
        setLocations(locsRes.data);
        setGlobalStats(statsRes.data);
        setBookings(bookingsRes.data);
        
        if (locsRes.data.length > 0) {
          setSelectedLocation(locsRes.data[0]);
        }
      } catch (err) {
        console.error("Admin init failed", err);
      } finally {
        setLoading(false);
      }
    };
    initData();
  }, []);

  useEffect(() => {
    if (selectedLocation) {
      const fetchSlots = async () => {
        try {
          // Admin view: fetch first floor/zone of selected location for slot control
          const floor = selectedLocation.floors[0];
          const zone = floor.zones[0];
          const res = await parkingService.getSlotsForZone(selectedLocation.code, floor.code, zone.code);
          setActiveSlots(res.data.slice(0, 12)); // Display a sample for control
        } catch (err) {
          console.error("Failed to fetch admin slots", err);
        }
      };
      fetchSlots();
    }
  }, [selectedLocation]);

  const handleToggle = async (slotId) => {
    try {
      await parkingService.toggleSlotStatus(slotId);
      // Refresh sample slots
      const floor = selectedLocation.floors[0];
      const zone = floor.zones[0];
      const res = await parkingService.getSlotsForZone(selectedLocation.code, floor.code, zone.code);
      setActiveSlots(res.data.slice(0, 12));
      
      // Update stats
      const statsRes = await parkingService.getGlobalStats();
      setGlobalStats(statsRes.data);
    } catch (err) {
      console.error("Toggle failed", err);
    }
  };

  const handleRelease = async (slotId) => {
    try {
      await parkingService.releaseSlot(slotId);
      const res = await parkingService.getActiveBookings();
      setBookings(res.data);
      const statsRes = await parkingService.getGlobalStats();
      setGlobalStats(statsRes.data);
    } catch (err) {
      console.error("Release failed", err);
    }
  };

  if (loading || !selectedLocation) {
    return <div className="loader-container"><div className="loader"></div><p>Loading Admin Panel...</p></div>;
  }

  const occupancyRate = globalStats 
    ? Math.round(((globalStats.occupied + globalStats.reserved) / globalStats.total) * 100)
    : 0;

  return (
    <div className="admin-page page-enter">
      <div className="page-header">
        <div>
          <h1>Admin Panel</h1>
          <p className="page-subtitle">Manage slots, view bookings, and analyze usage</p>
        </div>
      </div>

      {/* Admin Stats */}
      <div className="admin-stats stagger-children">
        <div className="stat-card">
          <div className="stat-icon stat-icon-total">📋</div>
          <div className="stat-info">
            <span className="stat-label">Total Slots</span>
            <span className="stat-value">{globalStats?.total}</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon stat-icon-available">🔖</div>
          <div className="stat-info">
            <span className="stat-label">Active Bookings</span>
            <span className="stat-value">{bookings.length}</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon stat-icon-rate">📈</div>
          <div className="stat-info">
            <span className="stat-label">Occupancy Rate</span>
            <span className="stat-value">{occupancyRate}%</span>
          </div>
        </div>
      </div>

      {/* Location Selector */}
      <div className="admin-location-select">
        {locations.map(loc => (
          <button 
            key={loc.code}
            className={`btn ${loc.code === selectedLocation.code ? 'btn-primary' : 'btn-secondary'} btn-sm`}
            onClick={() => setSelectedLocation(loc)}
          >
            {loc.icon} {loc.name}
          </button>
        ))}
      </div>

      {/* Slot Controls */}
      <div className="admin-section">
        <div className="admin-section-header">
          <span className="admin-section-title">Slot Controls</span>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-tertiary)' }}>
            Toggle slots for <strong>{selectedLocation.name}</strong>
          </span>
        </div>
        <div className="admin-section-body">
          <div className="slot-control-grid">
            {activeSlots.map(slot => (
              <div key={slot.id} className="slot-control-item">
                <div className="slot-control-info">
                  <span className="slot-control-id">{slot.slotLabel}</span>
                  <span className="slot-control-status" style={{ 
                    color: slot.status === 'available' ? 'var(--color-available)' : 'var(--color-occupied)' 
                  }}>
                    {slot.status.charAt(0).toUpperCase() + slot.status.slice(1)}
                  </span>
                </div>
                <label className="toggle-switch">
                  <input 
                    type="checkbox" 
                    checked={slot.status === 'available'} 
                    onChange={() => handleToggle(slot.id)}
                  />
                  <span className="toggle-slider"></span>
                </label>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bookings Table */}
      <div className="admin-section">
        <div className="admin-section-header">
          <span className="admin-section-title">Bookings</span>
          <div className="bookings-filters">
            <button 
              className={`filter-btn ${bookingFilter === 'all' ? 'active' : ''}`}
              onClick={() => setBookingFilter('all')}
            >
              All
            </button>
            <button 
              className={`filter-btn ${bookingFilter === 'active' ? 'active' : ''}`}
              onClick={() => setBookingFilter('active')}
            >
              Active
            </button>
          </div>
        </div>
        <div className="admin-section-body" style={{ padding: 0 }}>
          <div className="table-wrapper">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Slot</th>
                  <th>Vehicle</th>
                  <th>Location</th>
                  <th>Booked At</th>
                  <th>Expires</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {bookings.length === 0 ? (
                  <tr><td colSpan="7" style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-tertiary)' }}>No bookings found</td></tr>
                ) : (
                  bookings.map(b => (
                    <tr key={b.id}>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{b.slotLabel}</td>
                      <td>{b.vehicleNo}</td>
                      <td>{b.locationName}</td>
                      <td>{new Date(b.bookedAt).toLocaleString()}</td>
                      <td>{new Date(b.expiresAt).toLocaleString()}</td>
                      <td>
                        <span className={`badge ${b.bookingStatus === 'active' ? 'badge-reserved' : 'badge-available'}`}>
                          {b.bookingStatus === 'active' ? 'Active' : 'Expired'}
                        </span>
                      </td>
                      <td>
                        {b.bookingStatus === 'active' ? (
                          <button className="btn btn-danger btn-sm" onClick={() => handleRelease(b.slotId)}>Release</button>
                        ) : '—'}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPage;
迫
