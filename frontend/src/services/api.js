import axios from 'axios';

const API_BASE_URL = import.meta.env.PROD 
  ? '/api' 
  : 'http://localhost:9999/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const parkingService = {
  // Locations
  getLocations: () => api.get('/locations'),
  getLocationStats: (code) => api.get(`/locations/${code}/stats`),
  getGlobalStats: () => api.get('/stats/global'),

  // Slots
  getSlotsForZone: (locCode, floorCode, zoneCode) => 
    api.get(`/slots/${locCode}/${floorCode}/${zoneCode}`),
  toggleSlotStatus: (slotId) => api.post(`/slots/${slotId}/toggle`),

  // Bookings
  bookSlot: (bookingData) => api.post('/bookings', bookingData),
  getActiveBookings: () => api.get('/bookings/active'),
  releaseSlot: (slotId) => api.post(`/bookings/${slotId}/release`),

  // Activity
  getActivity: () => api.get('/stats/activity'),
};

export default api;
