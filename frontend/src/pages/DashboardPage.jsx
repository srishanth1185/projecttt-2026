import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  PieChart, Pie, Cell, ResponsiveContainer, Tooltip 
} from 'recharts';
import { parkingService } from '../services/api';

const DashboardPage = () => {
  const navigate = useNavigate();
  const [globalStats, setGlobalStats] = useState(null);
  const [locations, setLocations] = useState([]);
  const [locStats, setLocStats] = useState({});
  const [activity, setActivity] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, locsRes, actRes] = await Promise.all([
          parkingService.getGlobalStats(),
          parkingService.getLocations(),
          parkingService.getActivity()
        ]);

        setGlobalStats(statsRes.data);
        setLocations(locsRes.data);
        setActivity(actRes.data.slice(0, 50));

        // Fetch individual stats for each location
        const statsMap = {};
        for (const loc of locsRes.data) {
          const sRes = await parkingService.getLocationStats(loc.code);
          statsMap[loc.code] = sRes.data;
        }
        setLocStats(statsMap);
      } catch (err) {
        console.error("Dashboard data fetch failed", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    const interval = setInterval(fetchData, 10000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return (
      <div className="loader-container">
        <div className="loader"></div>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  const occupancyRate = globalStats 
    ? Math.round(((globalStats.occupied + globalStats.reserved) / globalStats.total) * 100)
    : 0;

  const chartData = globalStats ? [
    { name: 'Available', value: globalStats.available, color: 'hsl(152, 69%, 45%)' },
    { name: 'Occupied', value: globalStats.occupied, color: 'hsl(0, 72%, 55%)' },
    { name: 'Reserved', value: globalStats.reserved, color: 'hsl(40, 95%, 55%)' },
  ] : [];

  return (
    <div className="dashboard-page page-enter">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p className="page-subtitle">Real-time parking overview across all locations</p>
        </div>
      </div>

      {/* Stats Row */}
      <div className="stats-grid stagger-children">
        <StatCard 
          label="Total Slots" 
          value={globalStats?.total} 
          icon="🅿️" 
          iconClass="stat-icon-total"
          subtitle="All locations combined"
        />
        <StatCard 
          label="Available" 
          value={globalStats?.available} 
          icon="✅" 
          iconClass="stat-icon-available"
          subtitle="Ready for parking"
        />
        <StatCard 
          label="Occupied" 
          value={globalStats?.occupied} 
          icon="🚗" 
          iconClass="stat-icon-occupied"
          subtitle="Currently in use"
        />
        <StatCard 
          label="Occupancy Rate" 
          value={`${occupancyRate}%`} 
          icon="📊" 
          iconClass="stat-icon-rate"
          subtitle={getOccupancyLabel(occupancyRate)}
        />
      </div>

      {/* Location Cards */}
      <div className="locations-section">
        <div className="section-header">
          <h3 className="section-title">Parking Locations</h3>
        </div>
        <div className="locations-grid stagger-children">
          {locations.map(loc => (
            <LocationCard 
              key={loc.code} 
              loc={loc} 
              stats={locStats[loc.code]} 
              onClick={() => navigate(`/map/${loc.code}`)} 
            />
          ))}
        </div>
      </div>

      {/* Bottom Row */}
      <div className="dashboard-bottom">
        <div className="chart-card">
          <div className="chart-card-header">
            <span className="chart-card-title">Occupancy Distribution</span>
          </div>
          <div className="chart-canvas-container" style={{ height: '220px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={chartData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                  animationBegin={0}
                  animationDuration={800}
                >
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
            <div className="chart-center-text">
              <div style={{ fontSize: '1.5rem', fontWeight: '700', color: 'var(--text-primary)' }}>{occupancyRate}%</div>
              <div style={{ fontSize: '0.7rem', color: 'var(--text-tertiary)' }}>Occupied</div>
            </div>
          </div>
        </div>

        <div className="chart-card">
          <div className="chart-card-header">
            <span className="chart-card-title">Recent Activity</span>
          </div>
          <div className="activity-list">
            {activity.map((a, idx) => (
              <ActivityItem key={idx} activity={a} />
            ))}
            {activity.length === 0 && <div className="empty-state"><p>No recent activity</p></div>}
          </div>
        </div>
      </div>
    </div>
  );
};

const StatCard = ({ label, value, icon, iconClass, subtitle }) => (
  <div className="stat-card">
    <div className={`stat-icon ${iconClass}`}>{icon}</div>
    <div className="stat-info">
      <span className="stat-label">{label}</span>
      <span className="stat-value">{value}</span>
      <span className="stat-change" style={{ color: 'var(--text-tertiary)', fontSize: '0.7rem' }}>{subtitle}</span>
    </div>
  </div>
);

const LocationCard = ({ loc, stats, onClick }) => {
  if (!stats) return null;
  const rate = Math.round(((stats.occupied + stats.reserved) / stats.total) * 100);
  const barClass = rate >= 90 ? 'critical' : rate >= 70 ? 'high' : '';
  const statusBadge = rate >= 90
    ? <span className="badge badge-occupied">Full</span>
    : rate >= 70
      ? <span className="badge badge-reserved">Busy</span>
      : <span className="badge badge-available">Open</span>;

  return (
    <div className="location-card" onClick={onClick}>
      <div className="location-card-header">
        <div className="location-card-info">
          <div className="location-card-icon" style={{ background: loc.color }}>{loc.icon}</div>
          <div>
            <div className="location-card-name">{loc.name}</div>
            <div className="location-card-address">{loc.address}</div>
          </div>
        </div>
        <div className="location-card-status">{statusBadge}</div>
      </div>
      <div className="location-card-stats">
        <div className="location-stat">
          <div className="stat-num">{stats.total}</div>
          <div className="stat-label">Total</div>
        </div>
        <div className="location-stat">
          <div className="stat-num" style={{ color: 'var(--color-available)' }}>{stats.available}</div>
          <div className="stat-label">Available</div>
        </div>
        <div className="location-stat">
          <div className="stat-num" style={{ color: 'var(--color-occupied)' }}>{stats.occupied}</div>
          <div className="stat-label">Occupied</div>
        </div>
      </div>
      <div className="location-card-bar">
        <div className="bar-label">
          <span>Occupancy</span>
          <span>{rate}%</span>
        </div>
        <div className="occupancy-bar">
          <div className={`occupancy-fill ${barClass}`} style={{ width: `${rate}%` }}></div>
        </div>
      </div>
    </div>
  );
};

const ActivityItem = ({ activity }) => (
  <div className="activity-item">
    <div className={`activity-dot ${activity.type}`}></div>
    <span className="activity-text">{activity.message}</span>
    <span className="activity-time">{timeAgo(activity.timestamp)}</span>
  </div>
);

const getOccupancyLabel = (rate) => {
  if (rate >= 90) return '🔴 Critical';
  if (rate >= 70) return '🟡 High';
  if (rate >= 40) return '🟢 Moderate';
  return '🟢 Low';
};

const timeAgo = (dateStr) => {
  const date = new Date(dateStr);
  const seconds = Math.floor((new Date() - date) / 1000);
  if (seconds < 60) return 'just now';
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  return date.toLocaleDateString();
};

export default DashboardPage;
迫
