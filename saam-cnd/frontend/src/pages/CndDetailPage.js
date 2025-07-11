import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import { getCndResultadoById, getClienteById, downloadCndPdf } from '../services/apiService';
import {
    Container, Typography, Paper, CircularProgress, Alert, Box, Button, Grid,
    List, ListItem, ListItemText, Divider
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';

const DetailItem = ({ label, value }) => (
    <ListItem disablePadding>
        <ListItemText primary={value || '-'} secondary={label} />
    </ListItem>
);

const CndDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [resultadoCnd, setResultadoCnd] = useState(null);
    const [cliente, setCliente] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);


    const fetchDetalhes = useCallback(async () => {
        setLoading(true);
        setError(null);
        setSuccessMessage(null);
        try {
            const resCnd = await getCndResultadoById(id);
            setResultadoCnd(resCnd);

            if (resCnd && resCnd.fkCliente) {
                try {
                    const resCliente = await getClienteById(resCnd.fkCliente);
                    setCliente(resCliente);
                } catch (errCliente) {
                    console.error("Erro ao buscar cliente:", errCliente);
                    setError(prevError => prevError ? `${prevError}\nFalha ao carregar dados do cliente.` : "Falha ao carregar dados do cliente.");
                }
            } else if (resCnd && !resCnd.fkCliente) {
                setCliente(null); // Garante que não haja dados de cliente antigos se a CND não tiver fkCliente
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

    const clearMessages = () => {
        setError(null);
        setSuccessMessage(null);
    };

    const handleDownloadPdf = async () => {
        if (!resultadoCnd || !resultadoCnd.id) return;
        clearMessages();
        try {
            await downloadCndPdf(resultadoCnd.id);
            setSuccessMessage(`Download do PDF da CND ID ${resultadoCnd.id} iniciado.`);
        } catch (err) {
            setError('Falha ao baixar PDF: ' + (err.response?.data?.message || err.message));
            console.error("Erro no download:", err);
        }
    };

    if (loading) {
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Container>
        );
    }

    if (error && !resultadoCnd) { // Erro fatal ao carregar CND principal
        return (
            <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
                 <Button
                    variant="outlined"
                    startIcon={<ArrowBackIcon />}
                    onClick={() => navigate('/cnds')}
                    sx={{ mb: 2 }}
                >
                    Voltar para Lista
                </Button>
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h4" component="h1">
                    Detalhes da Certidão (CND)
                </Typography>
                <Button
                    variant="outlined"
                    startIcon={<ArrowBackIcon />}
                    onClick={() => navigate('/cnds')}
                >
                    Voltar para Lista
                </Button>
            </Box>

            {error && <Alert severity="warning" onClose={clearMessages} sx={{ mb: 2 }}>{error}</Alert>}
            {successMessage && <Alert severity="success" onClose={clearMessages} sx={{ mb: 2 }}>{successMessage}</Alert>}

            {!resultadoCnd && !loading && (
                <Alert severity="info">Nenhum detalhe de CND encontrado para este ID.</Alert>
            )}

            {resultadoCnd && (
                <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Resultado da Consulta (ID: {resultadoCnd.id})
                    </Typography>
                    <List dense>
                        <DetailItem label="Data do Processamento" value={resultadoCnd.dataProcessamento ? new Date(resultadoCnd.dataProcessamento).toLocaleString() : undefined} />
                        <DetailItem label="Situação da Certidão" value={resultadoCnd.situacao} />
                        <DetailItem label="Data de Emissão" value={resultadoCnd.dataEmissao ? new Date(resultadoCnd.dataEmissao).toLocaleDateString() : undefined} />
                        <DetailItem label="Data de Validade" value={resultadoCnd.dataValidade ? new Date(resultadoCnd.dataValidade).toLocaleDateString() : undefined} />
                        <DetailItem label="Código de Controle" value={resultadoCnd.codigoControle} />
                        <DetailItem label="Status do Processamento Interno" value={resultadoCnd.statusProcessamento} />
                        {resultadoCnd.mensagemErroProcessamento && <DetailItem label="Mensagem de Erro (Processamento)" value={resultadoCnd.mensagemErroProcessamento} />}
                        <DetailItem label="Tipo de Certidão" value={resultadoCnd.tipoCertidao} />
                        <DetailItem label="Órgão Emissor" value={resultadoCnd.orgaoEmissor} />
                        <DetailItem label="Observações" value={resultadoCnd.observacoes} />
                        <DetailItem label="Linha de Auditoria" value={resultadoCnd.linha} />
                    </List>
                    {resultadoCnd.arquivo && (
                        <Button
                            variant="contained"
                            startIcon={<CloudDownloadIcon />}
                            onClick={handleDownloadPdf}
                            sx={{ mt: 2 }}
                            color="secondary"
                        >
                            Baixar PDF da Certidão
                        </Button>
                    )}
                     {!resultadoCnd.arquivo && <Typography variant="caption" display="block" sx={{mt:1}}><em>PDF da certidão não disponível.</em></Typography>}
                </Paper>
            )}

            {cliente && (
                <Paper elevation={3} sx={{ p: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Informações do Cliente Vinculado (ID: {cliente.id})
                    </Typography>
                    <List dense>
                        <DetailItem label="Nome do Cliente" value={cliente.nome} />
                        <DetailItem label="CNPJ do Cliente" value={cliente.cnpj} />
                        <DetailItem label="Nome Fantasia/Empresa" value={cliente.nomeEmpresa} />
                        <DetailItem label="Periodicidade de Consulta" value={`${cliente.periodicidade || '-'} dias`} />
                        <DetailItem label="Status do Cliente" value={cliente.statusCliente} />
                        <DetailItem label="Consulta Nacional" value={cliente.nacional ? 'Sim' : 'Não'} />
                        <DetailItem label="Consulta Estadual" value={cliente.estadual ? 'Sim' : 'Não'} />
                        <DetailItem label="Consulta Municipal" value={cliente.municipal ? 'Sim' : 'Não'} />
                        <DetailItem label="Data de Cadastro do Cliente" value={cliente.dataCadastro ? new Date(cliente.dataCadastro).toLocaleDateString() : undefined} />
                        <DetailItem label="Linha de Auditoria (Cliente)" value={cliente.linha} />
                    </List>
                </Paper>
            )}
             {!cliente && resultadoCnd && resultadoCnd.fkCliente && !loading && (
                <Alert severity="info" sx={{mt: 2}}>Cliente ID {resultadoCnd.fkCliente} associado, mas não foi possível carregar seus dados ou não existe.</Alert>
            )}
            {!cliente && resultadoCnd && !resultadoCnd.fkCliente && !loading && (
                <Alert severity="info" sx={{mt: 2}}>Este resultado de CND não está vinculado a um cliente específico no sistema.</Alert>
            )}
        </Container>
    );
};

export default CndDetailPage;
