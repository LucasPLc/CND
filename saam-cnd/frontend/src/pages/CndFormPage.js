import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CndForm from '../components/Cnd/CndForm';
import { getCndResultadoById, createCndResultado, updateCndResultado, getClientes } from '../services/apiService';
import { Container, Typography, Paper, CircularProgress, Alert, Box, Button } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

const CndFormPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditMode = Boolean(id);

    const [initialData, setInitialData] = useState({});
    const [clientes, setClientes] = useState([]);
    const [loading, setLoading] = useState(isEditMode);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null); // Para mensagens de sucesso

    useEffect(() => {
        const fetchClientesList = async () => {
            if (!isEditMode || (isEditMode && initialData.fkCliente === undefined)) { // Busca clientes se for novo ou se não tiver cliente no initialData
                try {
                    const data = await getClientes();
                    setClientes(data);
                } catch (err) {
                    console.error("Erro ao buscar lista de clientes:", err);
                    setError(prev => prev ? `${prev}\nFalha ao carregar lista de clientes.` : "Falha ao carregar lista de clientes para seleção.");
                }
            }
        };
        fetchClientesList();
    }, [isEditMode, initialData.fkCliente]); // Adicionado initialData.fkCliente para re-buscar se necessário

    useEffect(() => {
        if (isEditMode && id) {
            setLoading(true);
            setError(null);
            setSuccessMessage(null);
            getCndResultadoById(id)
                .then(data => {
                    // fkCliente já é tratado no CndForm para ser string
                    setInitialData(data);
                    setLoading(false);
                })
                .catch(err => {
                    setError('Falha ao carregar dados da CND para edição: ' + (err.response?.data?.message || err.message));
                    setLoading(false);
                    console.error(err);
                });
        } else {
            // Garante que o formulário esteja limpo e com defaults ao criar um novo
            setInitialData({
                fkCliente: '',
                situacao: '',
                dataEmissao: '',
                dataValidade: '',
                codigoControle: '',
                statusProcessamento: 'MANUAL',
                tipoCertidao: 'Federal',
                orgaoEmissor: '',
                observacoes: '',
            });
        }
    }, [id, isEditMode]);

    const handleSubmit = async (formData) => {
        setError(null);
        setSuccessMessage(null);
        setLoading(true);
        try {
            const payload = { ...formData };
            // fkCliente já é convertido para Int no CndForm

            if (isEditMode) {
                payload.linha = `UPDATE-PEC-4537-FRONTEND`;
                await updateCndResultado(id, payload);
                setSuccessMessage('CND atualizada com sucesso!');
            } else {
                payload.linha = `INSERT-PEC-4536-FRONTEND`;
                await createCndResultado(payload);
                setSuccessMessage('CND cadastrada com sucesso!');
            }
            // Atraso para o usuário ver a mensagem de sucesso antes de redirecionar
            setTimeout(() => {
                navigate('/cnds'); // Redireciona para a lista após sucesso
            }, 1500);
        } catch (err) {
            const errorMsg = err.response?.data?.message || JSON.stringify(err.response?.data?.errors) || err.message;
            setError(`Erro ao salvar CND: ${errorMsg}`);
            console.error("Detalhes do erro ao salvar CND:", err.response?.data);
        } finally {
            setLoading(false);
        }
    };

    const clearMessages = () => {
        setError(null);
        setSuccessMessage(null);
    };

    if (loading && isEditMode) { // Mostra spinner apenas na edição enquanto busca dados
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
            <Paper elevation={3} sx={{ p: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h5" component="h1">
                        {isEditMode ? 'Editar Certidão (Resultado CND)' : 'Cadastrar Nova Certidão (Resultado CND)'}
                    </Typography>
                    <Button
                        variant="outlined"
                        startIcon={<ArrowBackIcon />}
                        onClick={() => navigate('/cnds')}
                    >
                        Voltar para Lista
                    </Button>
                </Box>

                {error && <Alert severity="error" onClose={clearMessages} sx={{ mb: 2 }}>{error}</Alert>}
                {successMessage && <Alert severity="success" onClose={clearMessages} sx={{ mb: 2 }}>{successMessage}</Alert>}
                {loading && <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}><CircularProgress size={24} /></Box>}

                <CndForm
                    initialData={initialData}
                    onSubmit={handleSubmit}
                    clientes={clientes}
                    isEditMode={isEditMode}
                    isLoadingSubmit={loading} // Passa o estado de loading para o botão do formulário
                />
            </Paper>
        </Container>
    );
};

export default CndFormPage;
