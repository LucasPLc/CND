import axios from 'axios';

const API_URL = '/api'; // O proxy no package.json cuidará do redirecionamento para http://localhost:8080/api

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'X-ID-CLIENTE': '1', // Header obrigatório para todas as requisições
  },
});

// Interceptor para adicionar token de autenticação (se necessário no futuro)
// apiClient.interceptors.request.use(config => {
//   const token = localStorage.getItem('userToken'); // Exemplo
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// }, error => {
//   return Promise.reject(error);
// });

// Funções de exemplo (serão expandidas conforme as PECs)

// --- Cliente Service ---
export const getClientes = async () => {
  try {
    const response = await apiClient.get('/clientes');
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar clientes:', error);
    throw error;
  }
};

export const getClienteById = async (id) => {
  try {
    const response = await apiClient.get(`/clientes/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Erro ao buscar cliente com ID ${id}:`, error);
    throw error;
  }
};

export const createCliente = async (clienteData) => {
  try {
    const response = await apiClient.post('/clientes', clienteData);
    return response.data;
  } catch (error) {
    console.error('Erro ao criar cliente:', error);
    throw error;
  }
};

export const updateCliente = async (id, clienteData) => {
  try {
    const response = await apiClient.put(`/clientes/${id}`, clienteData);
    return response.data;
  } catch (error) {
    console.error(`Erro ao atualizar cliente com ID ${id}:`, error);
    throw error;
  }
};

export const deleteCliente = async (id) => {
  try {
    await apiClient.delete(`/clientes/${id}`);
  } catch (error) {
    console.error(`Erro ao deletar cliente com ID ${id}:`, error);
    throw error;
  }
};

// --- CNDResultado Service ---
export const deleteCndResultado = async (id) => {
  try {
    await apiClient.delete(`/cnd-resultados/${id}`);
  } catch (error) {
    console.error(`Erro ao deletar resultado CND com ID ${id}:`, error);
    throw error;
  }
};

export const getCndResultadoById = async (id) => {
  try {
    const response = await apiClient.get(`/cnd-resultados/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Erro ao buscar resultado CND com ID ${id}:`, error);
    throw error;
  }
};

export const createCndResultado = async (cndData) => {
  try {
    const response = await apiClient.post('/cnd-resultados', cndData);
    return response.data;
  } catch (error) {
    console.error('Erro ao criar resultado CND:', error);
    throw error;
  }
};

export const updateCndResultado = async (id, cndData) => {
  try {
    const response = await apiClient.put(`/cnd-resultados/${id}`, cndData);
    return response.data;
  } catch (error) {
    console.error(`Erro ao atualizar resultado CND com ID ${id}:`, error);
    throw error;
  }
};


// --- CNDResultado Service ---
export const getCndResultados = async (filters = {}) => { // Já implementada
  try {
    const response = await apiClient.get('/cnd-resultados', { params: filters });
    return response.data;
  } catch (error) {
    console.error('Erro ao buscar resultados CND:', error);
    throw error;
  }
};

export const downloadCndPdf = async (id) => { // Implementação já estava boa
  try {
    const response = await apiClient.get(`/cnd-resultados/${id}/download`, {
      responseType: 'blob',
    });
    const contentDisposition = response.headers['content-disposition'];
    let filename = `CND_resultado_${id}.pdf`;
    if (contentDisposition) {
      const filenameMatch = contentDisposition.match(/filename="?(.+)"?/i);
      if (filenameMatch && filenameMatch.length > 1) {
        filename = filenameMatch[1];
      }
    }

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);

    return { success: true, filename };
  } catch (error) {
    console.error(`Erro ao baixar PDF do resultado CND com ID ${id}:`, error);
    throw error;
  }
};

// --- Consulta CND Federal (PEC-4869) ---
export const consultarCndFederalApi = async (clienteId) => {
  try {
    // Note que o endpoint é POST, mas não envia corpo, apenas o clienteId na URL
    const response = await apiClient.post(`/cnd-resultados/consulta-federal/${clienteId}`);
    return response.data;
  } catch (error) {
    console.error(`Erro ao consultar CND Federal para cliente ID ${clienteId}:`, error);
    throw error;
  }
};

// --- Sincronização CND MG (PEC-4629) ---
export const sincronizarCndMgApi = async (clienteId, tipoConsulta = "ESTADUAL_MG") => {
  try {
    const response = await apiClient.post(`/sincronizacao/minas-gerais/${clienteId}?tipoConsulta=${tipoConsulta}`);
    return response.data;
  } catch (error) {
    console.error(`Erro ao sincronizar CND MG para cliente ID ${clienteId}:`, error);
    throw error;
  }
};


// --- SAAM-CR Validação --- (Exemplo de como poderia ser, se o front-end precisasse chamar diretamente)
// Geralmente, essa validação é feita no backend antes de permitir acesso aos endpoints protegidos.
// Mas, se houver um endpoint de "login" ou "verificação de status" no front, poderia ser algo assim:
export const checkClientStatus = async (idCliente) => {
  try {
    // Este endpoint teria que ser criado no nosso backend para intermediar a chamada ao SAAM-CR
    const response = await apiClient.get(`/auth/status/${idCliente}`);
    return response.data; // Ex: { "autorizado": true }
  } catch (error) {
    console.error(`Erro ao verificar status do cliente ${idCliente}:`, error);
    throw error; // Pode ser um erro 403 específico
  }
};

export default apiClient;
