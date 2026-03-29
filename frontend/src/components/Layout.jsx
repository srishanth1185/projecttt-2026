import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

const Layout = () => {
  const [isSidebarOpen, setSidebarOpen] = useState(false);

  const toggleSidebar = () => setSidebarOpen(!isSidebarOpen);
  const closeSidebar = () => setSidebarOpen(false);

  return (
    <div className="app-container">
      {/* ── Mobile Header ── */}
      <header className="mobile-header">
        <button 
          className="hamburger" 
          id="mobile-hamburger" 
          aria-label="Toggle menu"
          onClick={toggleSidebar}
        >
          ☰
        </button>
        <span style={{ fontWeight: 700, fontSize: '1rem' }}>🅿️ SmartPark</span>
        <div style={{ width: '40px' }}></div>
      </header>

      <Sidebar isOpen={isSidebarOpen} onClose={closeSidebar} />

      <main className="main-content" id="main-content">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
