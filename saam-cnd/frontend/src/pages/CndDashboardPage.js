import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { getCndResultados, deleteCliente } from '../services/apiService'; // getCndResultados, deleteCndResultado (a ser criado)
// Importar o serviço de exclusão de CNDResultado quando for criado: import { deleteCndResultado } from '../services/apiService';

const CndDashboardPage = () => {
    const [resultados, setResultados] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filters, setFilters] = useState({
        cnpjCliente: '',
        // Adicionar outros filtros conforme PEC-4961: nome, situação, status, datas
        situacao: '',
        statusProcessamento: '',
        // dataEmissaoInicio: '',
        // dataEmissaoFim: '',
    });

    const fetchResultados = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            // Prepara os filtros para não enviar os vazios
            const activeFilters = {};
            for (const key in filters) {
                if (filters[key]) {
                    activeFilters[key] = filters[key];
                }
            }
            const data = await getCndResultados(activeFilters);
            setResultados(data);
        } catch (err) {
            setError('Falha ao carregar resultados CND: ' + (err.response?.data?.message || err.message));
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [filters]);

    useEffect(() => {
        fetchResultados();
    }, [fetchResultados]);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prevFilters => ({
            ...prevFilters,
            [name]: value,
        }));
    };

    const handleApplyFilters = () => {
        fetchResultados();
    };

    const handleDeleteResultado = async (id) => {
        if (window.confirm(`Tem certeza de que deseja excluir o resultado CND ID ${id}?`)) {
            try {
                await deleteCndResultado(id);
                alert(`Resultado CND ID ${id} excluído com sucesso.`);
                fetchResultados(); // Recarregar a lista
            } catch (err) {
                setError('Falha ao excluir resultado CND: ' + (err.response?.data?.message || err.message));
                console.error(err);
            }
        }
    };


    if (loading) return <p>Carregando resultados...</p>;
    // Removido o if (error) para mostrar a tabela e os filtros mesmo com erro. O erro será mostrado acima.

    return (
        <div>
            <h2>Monitoramento de Certidões Negativas de Débitos</h2>

            {error && <p style={{ color: 'red' }}>Erro: {error}</p>}

            {/* TODO: Formulário de Cadastro de CND (PEC-4536) - Será um componente separado ou modal */}
            <Link to="/cnds/novo" className="button">Nova CND (Cadastro Manual)</Link>

            {/* Filtros (PEC-4961) */}
            <div className="filters-container" style={{ margin: '20px 0', padding: '10px', border: '1px solid #ccc' }}>
                <h4>Filtros</h4>
                <input
                    type="text"
                    name="cnpjCliente"
                    placeholder="CNPJ do Cliente"
                    value={filters.cnpjCliente}
                    onChange={handleFilterChange}
                />
                <input
                    type="text"
                    name="situacao"
                    placeholder="Situação da Certidão (ex: Negativa)"
                    value={filters.situacao}
                    onChange={handleFilterChange}
                />
                <input
                    type="text"
                    name="statusProcessamento"
                    placeholder="Status do Processamento (ex: CONCLUIDO)"
                    value={filters.statusProcessamento}
                    onChange={handleFilterChange}
                />
                {/* Adicionar mais inputs de filtro aqui */}
                <button onClick={handleApplyFilters}>Aplicar Filtros</button>
            </div>

            {resultados.length === 0 && !loading && !error && <p>Nenhum resultado de CND encontrado.</p>}

            {resultados.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>ID Resultado</th>
                            <th>CNPJ Cliente</th>
                            <th>Data Processamento</th>
                            <th>Situação Certidão</th>
                            <th>Data Emissão</th>
                            <th>Data Validade</th>
                            <th>Status Processamento</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {resultados.map(res => (
                            <tr key={res.id}>
                                <td>{res.id}</td>
                                <td>{res.cnpjCliente || res.fkCliente}</td> {/* Mostrar CNPJ se disponível */}
                                <td>{res.dataProcessamento ? new Date(res.dataProcessamento).toLocaleString() : '-'}</td>
                                <td>{res.situacao || '-'}</td>
                                <td>{res.dataEmissao ? new Date(res.dataEmissao).toLocaleDateString() : '-'}</td>
                                <td>{res.dataValidade ? new Date(res.dataValidade).toLocaleDateString() : '-'}</td>
                                <td>{res.statusProcessamento || '-'}</td>
                                <td>
                                    <Link to={`/cnds/detalhes/${res.id}`} className="button edit">Ver Detalhes</Link>
                                    {/* Botão de Edição (PEC-4537) */}
                                    <Link to={`/cnds/editar/${res.id}`} className="button edit">Editar</Link>
                                    {/* Botão de Exclusão (PEC-4538) */}
                                    <button onClick={() => handleDeleteResultado(res.id)} className="button delete">Excluir</button>
                                    {/* Botão Consultar CND Federal (PEC-4869) */}
                                    {res.fkCliente && ( // Só mostra se tiver cliente associado ao resultado
                                        <button
                                            onClick={async () => {
                                                try {
                                                    const novoResultado = await consultarCndFederalApi(res.fkCliente);
                                                    alert(`Consulta Federal para cliente ${res.fkCliente} disparada. Resultado ID: ${novoResultado.id}, Situação: ${novoResultado.situacao}`);
                                                    fetchResultados(); // Recarregar para ver o novo resultado (ou atualizado)
                                                } catch (consultaError) {
                                                    setError('Falha ao consultar CND Federal: ' + (consultaError.response?.data?.message || consultaError.message));
                                                }
                                            }}
                                            className="button"
                                            title={`Consultar CND Federal para Cliente ID ${res.fkCliente}`}
                                        >
                                            Consultar Federal (Mock)
                                        </button>
                                    )}
                                     {/* Botão Sincronizar CND MG (PEC-4629) */}
                                     {res.fkCliente && (
                                        <button
                                            onClick={async () => {
                                                try {
                                                    const novoResultado = await sincronizarCndMgApi(res.fkCliente);
                                                    alert(`Sincronização MG para cliente ${res.fkCliente} disparada. Resultado ID: ${novoResultado.id}, Situação: ${novoResultado.situacao}`);
                                                    fetchResultados();
                                                } catch (sincError) {
                                                    setError('Falha ao sincronizar CND MG: ' + (sincError.response?.data?.message || sincError.message));
                                                }
                                            }}
                                            className="button"
                                            title={`Sincronizar CND MG para Cliente ID ${res.fkCliente}`}
                                        >
                                            Sinc MG (Mock)
                                        </button>
                                    )}
                                    {/* Botão de Download (PEC-4981) - será habilitado condicionalmente */}
                                    {res.arquivo && (
                                        <button
                                            onClick={async () => {
                                                try {
                                                    await downloadCndPdf(res.id);
                                                } catch (downloadError) {
                                                    setError('Falha ao baixar PDF: ' + (downloadError.response?.data?.message || downloadError.message));
                                                }
                                            }}
                                            className="button"
                                            disabled={!res.arquivo}
                                        >
                                            Download PDF
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
            {/* TODO: Paginação */}
        </div>
    );
};

export default CndDashboardPage;
