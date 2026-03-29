import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { parkingService } from '../services/api';
import BookingModal from '../components/BookingModal';

const SlotMapPage = () => {
  const { code } = useParams();
  const [location, setLocation] = useState(null);
  const [currentFloor, setCurrentFloor] = useState(null);
  const [currentZone, setCurrentZone] = useState(null);
  const [slots, setSlots] = useState([]);
  const [locStats, setLocStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [bookingSlot, setBookingSlot] = useState(null);

  const fetchLocationData = useCallback(async () => {
    try {
      const locsRes = await parkingService.getLocations();
      const currentLoc = locsRes.data.find(l => l.code === code);
      if (currentLoc) {
        setLocation(currentLoc);
        // Default to first floor/zone if none selected
        if (!currentFloor) {
          const firstFloor = currentLoc.floors[0];
          setCurrentFloor(firstFloor);
          setCurrentZone(firstFloor.zones[0]);
        }
      }
      
      const statsRes = await parkingService.getLocationStats(code);
      setLocStats(statsRes.data);
    } catch (err) {
      console.error("Failed to fetch location data", err);
    }
  }, [code, currentFloor]);

  const fetchSlots = useCallback(async () => {
    if (!currentZone || !location || !currentFloor) return;
    try {
      const res = await parkingService.getSlotsForZone(location.code, currentFloor.code, currentZone.code);
      setSlots(res.data);
    } catch (err) {
      console.error("Failed to fetch slots", err);
    }
  }, [location, currentFloor, currentZone]);

  useEffect(() => {
    setLoading(true);
    fetchLocationData().then(() => setLoading(false));
  }, [code]);

  useEffect(() => {
    fetchSlots();
    const interval = setInterval(fetchSlots, 10000);
    return () => clearInterval(interval);
  }, [currentZone, fetchSlots]);

  if (loading || !location) {
    return <div className="loader-container"><div className="loader"></div><p>Loading map...</p></div>;
  }

  const handleSlotClick = (slot) => {
    if (slot.status === 'available') {
      setBookingSlot(slot);
    } else if (slot.status === 'reserved') {
      if (window.confirm(`Release reservation for slot ${slot.slotLabel}?`)) {
        parkingService.releaseSlot(slot.id).then(() => fetchSlots());
      }
    }
  };

  return (
    <div className="slot-map-page page-enter">
      {/* Top Bar */}
      <div className="slot-map-topbar">
        <div className="slot-map-location-info">
          <div className="slot-map-location-icon" style={{ background: location.color }}>{location.icon}</div>
          <div>
            <div className="slot-map-location-name">{location.name}</div>
            <div className="slot-map-location-sub">{location.address}</div>
          </div>
        </div>
        <div className="slot-map-quick-stats">
          <div className="quick-stat">
            <div className="dot available"></div>
            <span className="qs-value">{locStats?.available || 0}</span>
            <span className="qs-label">Available</span>
          </div>
          <div className="quick-stat">
            <div className="dot occupied"></div>
            <span className="qs-value">{locStats?.occupied || 0}</span>
            <span className="qs-label">Occupied</span>
          </div>
          <div className="quick-stat">
            <div className="dot reserved"></div>
            <span className="qs-value">{locStats?.reserved || 0}</span>
            <span className="qs-label">Reserved</span>
          </div>
        </div>
      </div>

      {/* Floor / Zone Tabs */}
      <div className="floor-zone-nav">
        <div className="floor-tabs">
          {location.floors.map(f => (
            <button 
              key={f.code}
              className={`tab ${f.code === currentFloor?.code ? 'active' : ''}`}
              onClick={() => {
                setCurrentFloor(f);
                setCurrentZone(f.zones[0]);
              }}
            >
              {f.name}
            </button>
          ))}
        </div>
        <div className="zone-tabs">
          {currentFloor?.zones.map(z => (
            <button 
              key={z.code}
              className={`tab ${z.code === currentZone?.code ? 'active' : ''}`}
              onClick={() => setCurrentZone(z)}
            >
              {z.name}
            </button>
          ))}
        </div>
      </div>

      {/* Legend */}
      <div className="slot-legend">
        <div className="legend-item"><div className="legend-swatch available"></div> Available</div>
        <div className="legend-item"><div className="legend-swatch occupied"></div> Occupied</div>
        <div className="legend-item"><div className="legend-swatch reserved"></div> Reserved</div>
        <div className="legend-item"><div className="legend-swatch handicapped"></div> Handicapped</div>
      </div>

      {/* Parking Grid */}
      <div className="parking-grid-wrapper">
        <div className="parking-grid-label">
          {currentFloor?.name} — {currentZone?.name}
        </div>
        <div className="parking-grid-container">
          {renderGrid(slots, currentZone, handleSlotClick)}
        </div>
      </div>

      {bookingSlot && (
        <BookingModal 
          slot={bookingSlot} 
          onClose={() => setBookingSlot(null)}
          onSuccess={() => {
            setBookingSlot(null);
            fetchSlots();
          }}
        />
      )}
    </div>
  );
};

const renderGrid = (slots, zone, onSlotClick) => {
  if (!zone || slots.length === 0) return null;
  
  const { rows, slotsPerRow } = zone;
  const gridRows = [];

  for (let r = 0; r < rows; r++) {
    const aisleSlots = [];
    for (let c = 0; c < slotsPerRow; c++) {
      const slot = slots.find(s => s.row === r && s.col === c);
      if (slot) {
        aisleSlots.push(
          <div 
            key={slot.id}
            className={`parking-slot ${slot.status} ${slot.type === 'handicapped' ? 'handicapped' : ''}`}
            onClick={() => onSlotClick(slot)}
            title={`Slot ${slot.slotLabel} - ${slot.status}`}
          >
            <span className="slot-icon">{getSlotIcon(slot)}</span>
            <span className="slot-id">{slot.slotLabel}</span>
            <div className="slot-tooltip">
              Slot {slot.slotLabel} — {slot.status.toUpperCase()}
              {slot.vehicleNo && <><br/>Vehicle: {slot.vehicleNo}</>}
              {slot.expiresAt && <><br/>Expires: {new Date(slot.expiresAt).toLocaleTimeString()}</>}
            </div>
          </div>
        );
      }
    }

    gridRows.push(
      <div key={`row-${r}`} className="parking-aisle-wrapper">
        <div className="parking-aisle">
          <div className="aisle-label">Row {r + 1}</div>
          <div className="parking-grid">
            {aisleSlots}
          </div>
        </div>
        {r < rows - 1 && <div className="parking-road"></div>}
      </div>
    );
  }

  return gridRows;
};

const getSlotIcon = (slot) => {
  if (slot.status === 'occupied') return '🚗';
  if (slot.status === 'reserved') return '🔒';
  if (slot.type === 'handicapped') return '♿';
  return '○';
};

export default SlotMapPage;
迫
