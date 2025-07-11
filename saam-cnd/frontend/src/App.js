import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import './App.css';

import HomePage from './pages/HomePage';
import CndDashboardPage from './pages/CndDashboardPage';

import CndDetailPage from './pages/CndDetailPage';

// Placeholders - serão substituídos por componentes reais
const ClientePagePlaceholder = () => <h2>Gerenciamento de Clientes (Em Desenvolvimento)</h2>;
import CndFormPage from './pages/CndFormPage';

// const CndDashboardPagePlaceholder = () => <h2>Monitoramento de CNDs (Em Desenvolvimento)</h2>; // Substituído
// const CndDetailPagePlaceholder = () => <h2>Detalhes da CND (Em Desenvolvimento)</h2>; // Substituído
// const CndFormPagePlaceholder = () => <h2>Formulário de CND (Em Desenvolvimento)</h2>; // Substituído


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

            {/* Rotas de Cliente (PEC-4924 no backend, front-end básico por enquanto) */}
            <Route path="/clientes" element={<ClientePagePlaceholder />} />
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
