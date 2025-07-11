import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import './App.css';

import HomePage from './pages/HomePage';
import ClientePage from './pages/ClientePage';
import CndDashboardPage from './pages/CndDashboardPage';
import CndDetailPage from './pages/CndDetailPage';
import CndFormPage from './pages/CndFormPage';



function App() {
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
            <h1>SAAM-CND</h1>
          </Link>
        </header>
        <nav className="main-nav">
          <ul>
            <li><Link to="/">Home</Link></li>
            <li><Link to="/clientes">Clientes</Link></li>
            <li><Link to="/cnds">CNDs</Link></li>
            {/* Adicionar link para cadastro de CND se for uma página separada */}
            {/* <li><Link to="/cnds/novo">Cadastrar CND</Link></li> */}
          </ul>
        </nav>
        <main>
          <Routes>
            <Route path="/" element={<HomePage />} />

            {/* Rotas de Cliente */}
            <Route path="/clientes" element={<ClientePage />} />
            {/*
            <Route path="/clientes/novo" element={<ClienteFormPage />} />
            <Route path="/clientes/editar/:id" element={<ClienteFormPage />} />
            */}

            {/* Rotas de CND */}
            <Route path="/cnds" element={<CndDashboardPage />} /> {/* Tela principal (PEC-4536, PEC-4961) */}
            <Route path="/cnds/novo" element={<CndFormPage />} /> {/* Cadastro (PEC-4536) */}
            <Route path="/cnds/editar/:id" element={<CndFormPage />} /> {/* Edição (PEC-4537) */}
            <Route path="/cnds/detalhes/:id" element={<CndDetailPage />} /> {/* Visualização (PEC-4961) */}

          </Routes>
        </main>
        <footer>
          <p>&copy; {new Date().getFullYear()} SAAM-CND. Todos os direitos reservados.</p>
        </footer>
      </div>
    </Router>
  );
}

export default App;
