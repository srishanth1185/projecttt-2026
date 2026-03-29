import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

/* ── Custom Premium Styles ── */
import './styles/variables.css';
import './styles/base.css';
import './styles/animations.css';
import './styles/components.css';
import './styles/dashboard.css';
import './styles/slot-map.css';
import './styles/admin.css';

import App from './App.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
