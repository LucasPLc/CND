import React from 'react';
import { Link } from 'react-router-dom';

function HomePage() {
  return (
    <div>
      <h1>Bem-vindo ao SAAM-CND</h1>
      <p>Sistema de Acompanhamento e Análise de Movimentações - Certidões Negativas de Débitos.</p>
      <nav>
        <ul>
          <li>
            <Link to="/clientes">Gerenciar Clientes</Link>
          </li>
          <li>
            <Link to="/cnds">Monitoramento de CNDs</Link>
          </li>
          {/* Adicionar mais links conforme as telas forem criadas */}
        </ul>
      </nav>
    </div>
  );
}

export default HomePage;
