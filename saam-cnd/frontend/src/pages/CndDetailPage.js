import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getCndResultadoById, getClienteById, downloadCndPdf } from '../services/apiService'; // Assumindo que getClienteById será criado ou já existe

const CndDetailPage = () => {
    const { id } = useParams(); // ID do CndResultado
    const [resultadoCnd, setResultadoCnd] = useState(null);
    const [cliente, setCliente] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchDetalhes = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const resCnd = await getCndResultadoById(id);
            setResultadoCnd(resCnd);

            if (resCnd && resCnd.fkCliente) {
                try {
                    const resCliente = await getClienteById(resCnd.fkCliente);
                    setCliente(resCliente);
                } catch (errCliente) {
                    console.error("Erro ao buscar cliente:", errCliente);
                    setError(prevError => prevError ? prevError + "; Falha ao carregar dados do cliente." : "Falha ao carregar dados do cliente.");
                }
            }
        } catch (err) {
            setError('Falha ao carregar detalhes da CND: ' + (err.response?.data?.message || err.message));
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        fetchDetalhes();
    }, [fetchDetalhes]);

    const handleDownloadPdf = async () => {
        if (!resultadoCnd || !resultadoCnd.id) return;
        try {
            await downloadCndPdf(resultadoCnd.id);
            // O download é tratado no apiService
        } catch (err) {
            setError('Falha ao baixar PDF: ' + (err.response?.data?.message || err.message));
            console.error("Erro no download:", err);
        }
    };

    if (loading) return <p>Carregando detalhes...</p>;
    if (error && !resultadoCnd) return <p style={{ color: 'red' }}>Erro: {error}</p>; // Se erro fatal ao carregar CND

    return (
        <div>
            <h2>Detalhes da Certidão Negativa de Débitos (CND)</h2>
            {error && <p style={{ color: 'red' }}>Aviso: {error}</p>}

            {resultadoCnd && (
                <div className="cnd-details-container" style={{ border: '1px solid #eee', padding: '15px', margin: '10px' }}>
                    <h3>Resultado da Consulta (ID: {resultadoCnd.id})</h3>
                    <p><strong>Data do Processamento:</strong> {resultadoCnd.dataProcessamento ? new Date(resultadoCnd.dataProcessamento).toLocaleString() : '-'}</p>
                    <p><strong>Situação da Certidão:</strong> {resultadoCnd.situacao || '-'}</p>
                    <p><strong>Data de Emissão da Certidão:</strong> {resultadoCnd.dataEmissao ? new Date(resultadoCnd.dataEmissao).toLocaleDateString() : '-'}</p>
                    <p><strong>Data de Validade da Certidão:</strong> {resultadoCnd.dataValidade ? new Date(resultadoCnd.dataValidade).toLocaleDateString() : '-'}</p>
                    <p><strong>Código de Controle da Certidão:</strong> {resultadoCnd.codigoControle || '-'}</p>
                    <p><strong>Status do Processamento Interno:</strong> {resultadoCnd.statusProcessamento || '-'}</p>
                    {resultadoCnd.mensagemErroProcessamento && <p><strong>Mensagem de Erro (Processamento):</strong> {resultadoCnd.mensagemErroProcessamento}</p>}
                    <p><strong>Linha de Auditoria (Resultado):</strong> {resultadoCnd.linha || '-'}</p>

                    {resultadoCnd.arquivo && (
                        <button onClick={handleDownloadPdf} className="button" style={{marginTop: '10px'}}>
                            Download PDF da Certidão (PEC-4981)
                        </button>
                    )}
                     {!resultadoCnd.arquivo && <p><em>PDF da certidão não disponível.</em></p>}
                </div>
            )}

            {cliente && (
                <div className="cliente-details-container" style={{ border: '1px solid #eee', padding: '15px', margin: '10px' }}>
                    <h3>Informações do Cliente (ID: {cliente.id})</h3>
                    <p><strong>CNPJ do Cliente:</strong> {cliente.cnpj}</p>
                    <p><strong>Nome da Empresa (Cliente):</strong> {cliente.nomeEmpresa || 'Não informado'}</p> {/* Supondo que nomeEmpresa venha do DTO */}
                    <p><strong>Periodicidade de Consulta:</strong> {cliente.periodicidade} dias</p>
                    <p><strong>Status do Cliente:</strong> {cliente.statusCliente}</p>
                    <p><strong>Consulta Nacional:</strong> {cliente.nacional ? 'Sim' : 'Não'}</p>
                    <p><strong>Consulta Estadual:</strong> {cliente.estadual ? 'Sim' : 'Não'}</p>
                    <p><strong>Consulta Municipal:</strong> {cliente.municipal ? 'Sim' : 'Não'}</p>
                    <p><strong>Linha de Auditoria (Cliente):</strong> {cliente.linha || '-'}</p>
                </div>
            )}

            {!resultadoCnd && !loading && <p>Nenhum detalhe de CND encontrado para este ID.</p>}

            <Link to="/cnds" className="button">Voltar para Lista de CNDs</Link>
        </div>
    );
};

export default CndDetailPage;
