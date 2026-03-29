import React, { useState, useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { parkingService } from '../services/api';

const Sidebar = ({ isOpen, onClose }) => {
  const [locationStats, setLocationStats] = useState({});
  const location = useLocation();

  const locations = [
    { id: 'nav-mall', code: 'mall', name: 'Premium Mall', icon: '🛍️', route: '/map/mall' },
    { id: 'nav-hospital', code: 'hospital', name: 'City Hospital', icon: '🏥', route: '/map/hospital' },
    { id: 'nav-office', code: 'office', name: 'Tech Park', icon: '🏢', route: '/map/office' },
    { id: 'nav-campus', code: 'campus', name: 'Unilake Campus', icon: '🎓', route: '/map/campus' },
  ];

  useEffect(() => {
    const fetchStats = async () => {
      const statsMap = {};
      for (const loc of locations) {
        try {
          const res = await parkingService.getLocationStats(loc.code);
          statsMap[loc.code] = res.data.available;
        } catch (e) {
          statsMap[loc.code] = 0;
        }
      }
      setLocationStats(statsMap);
    };

    fetchStats();
    const interval = setInterval(fetchStats, 15000);
    return () => clearInterval(interval);
  }, []);

  return (
    <>
      <div 
        className={`sidebar-overlay ${isOpen ? 'active' : ''}`} 
        onClick={onClose}
      ></div>
      
      <nav className={`sidebar ${isOpen ? 'open' : ''}`} aria-label="Main navigation">
        <div className="sidebar-brand">
          <div className="brand-icon">🅿️</div>
          <div className="brand-text">
            <span class="brand-name">SmartPark</span>
            <span class="brand-tagline">Parking System</span>
          </div>
        </div>

        <div className="sidebar-nav">
          <div className="nav-section-label">Overview</div>

          <NavLink 
            to="/" 
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
            onClick={onClose}
          >
            <span className="nav-icon">📊</span>
            Dashboard
          </NavLink>

          <div className="nav-section-label">Locations</div>

          {locations.map((loc) => (
            <NavLink 
              key={loc.code}
              to={loc.route} 
              className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              onClick={onClose}
            >
              <span className="nav-icon">{loc.icon}</span>
              {loc.name}
              <span className="nav-badge">{locationStats[loc.code] || 0}</span>
            </NavLink>
          ))}

          <div className="nav-section-label">Management</div>

          <NavLink 
            to="/admin" 
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
            onClick={onClose}
          >
            <span className="nav-icon">⚙️</span>
            Admin Panel
          </NavLink>
        </div>

        <div className="sidebar-footer">
          <div className="status-indicator">
            <div className="pulse-dot"></div>
            <span>System Active</span>
          </div>
        </div>
      </nav>
    </>
  );
};

export default Sidebar;
迫
