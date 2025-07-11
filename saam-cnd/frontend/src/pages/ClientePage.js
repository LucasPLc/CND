import React, { useState, useEffect, useCallback } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { getClientes, deleteCliente, createCliente } from '../services/apiService';
import {
    Container, Typography, Button, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, Paper, CircularProgress, Alert,
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    TextField, Box, IconButton, Tooltip,
    FormControlLabel, Checkbox, TablePagination // Adicionados
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';

const ClientePage = () => {
    const [clientes, setClientes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [formData, setFormData] = useState({
        nome: '',
        cnpj: '',
        periodicidade: 30, // Valor padrão
        statusCliente: 'ATIVO', // Valor padrão
        nacional: true, // Valor padrão
        municipal: true, // Valor padrão
        estadual: true, // Valor padrão
        fkEmpresa: 1, // TODO: Tornar dinâmico ou selecionável
    });
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [clienteToDelete, setClienteToDelete] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5); // Ou 10, como preferir

    const fetchClientes = useCallback(async () => {
        setLoading(true);
        setError(null);
        // setSuccessMessage(null); // Limpa mensagem de sucesso ao recarregar
        try {
            const data = await getClientes();
            setClientes(data);
        } catch (err) {
            setError('Falha ao carregar clientes: ' + (err.response?.data?.message || err.message));
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchClientes();
    }, [fetchClientes]);

    const handleOpenDeleteDialog = (cliente) => {
        setClienteToDelete(cliente);
        setDeleteDialogOpen(true);
    };

    const handleCloseDeleteDialog = () => {
        setClienteToDelete(null);
        setDeleteDialogOpen(false);
    };

    const handleDeleteCliente = async () => {
        if (!clienteToDelete) return;
        try {
            await deleteCliente(clienteToDelete.id);
            setSuccessMessage(`Cliente ${clienteToDelete.nome} (ID: ${clienteToDelete.id}) excluído com sucesso.`);
            fetchClientes(); // Recarrega a lista
        } catch (err) {
            setError('Falha ao excluir cliente: ' + (err.response?.data?.message || err.message));
            console.error(err);
        } finally {
            handleCloseDeleteDialog();
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage(null);
        try {
            // Adicionando os campos que faltavam no DTO de request
            const clienteDataToSubmit = {
                ...formData,
                // fkEmpresa é obrigatório no DTO, mas não estava no form inicial. Usando um valor padrão por agora.
                // Idealmente, este campo viria de uma seleção ou contexto do usuário.
                fkEmpresa: formData.fkEmpresa || 1, // Garante que fkEmpresa seja enviado
            };
            await createCliente(clienteDataToSubmit);
            setSuccessMessage(`Cliente ${formData.nome} criado com sucesso!`);
            setShowForm(false);
            setFormData({ nome: '', cnpj: '', periodicidade: 30, statusCliente: 'ATIVO', nacional: true, municipal: true, estadual: true, fkEmpresa: 1 });
            fetchClientes(); // Recarregar a lista
        } catch (err) {
            setError('Erro ao criar cliente: ' + (err.response?.data?.message || err.message));
            console.error("Detalhes do erro ao criar cliente:", err.response?.data);
        }
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const clearMessages = () => {
        setError(null);
        setSuccessMessage(null);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0); // Volta para a primeira página ao mudar o número de linhas
    };

    // Calcula os clientes para a página atual
    const emptyRows = page > 0 ? Math.max(0, (1 + page) * rowsPerPage - clientes.length) : 0;
    const currentClientes = clientes.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);


    if (loading && clientes.length === 0) { // Mostra o spinner apenas no carregamento inicial
        return (
            <Container style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Gerenciamento de Clientes
            </Typography>

            {error && <Alert severity="error" onClose={clearMessages} sx={{ mb: 2 }}>{error}</Alert>}
            {successMessage && <Alert severity="success" onClose={clearMessages} sx={{ mb: 2 }}>{successMessage}</Alert>}

            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between' }}>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => { setShowForm(!showForm); clearMessages(); }}
                >
                    {showForm ? 'Cancelar Cadastro' : 'Novo Cliente'}
                </Button>
                <Tooltip title="Atualizar Lista">
                    <IconButton onClick={() => { fetchClientes(); clearMessages(); }} color="primary">
                        <RefreshIcon />
                    </IconButton>
                </Tooltip>
            </Box>

            {showForm && (
                <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>Formulário de Cliente</Typography>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="nome"
                            label="Nome do Cliente"
                            name="nome"
                            autoComplete="name"
                            autoFocus
                            value={formData.nome}
                            onChange={handleInputChange}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="cnpj"
                            label="CNPJ"
                            name="cnpj"
                            autoComplete="off"
                            value={formData.cnpj}
                            onChange={handleInputChange}
                            // TODO: Adicionar máscara e validação de CNPJ
                        />
                        {/* Adicionando campos faltantes conforme CndClienteRequestDTO */}
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="periodicidade"
                            label="Periodicidade (dias)"
                            name="periodicidade"
                            type="number"
                            value={formData.periodicidade}
                            onChange={handleInputChange}
                        />
                         <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="statusCliente"
                            label="Status do Cliente"
                            name="statusCliente"
                            value={formData.statusCliente}
                            onChange={handleInputChange}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="fkEmpresa"
                            label="ID da Empresa (fk_empresa)"
                            name="fkEmpresa"
                            type="number"
                            value={formData.fkEmpresa}
                            onChange={handleInputChange}
                            // TODO: Idealmente, isso seria um select buscando empresas
                        />
                        {/* Campos Booleanos - Poderiam ser Checkboxes */}
                        <FormControlLabel control={<Checkbox checked={formData.nacional} onChange={handleInputChange} name="nacional" />} label="Nacional" />
                        <FormControlLabel control={<Checkbox checked={formData.municipal} onChange={handleInputChange} name="municipal" />} label="Municipal" />
                        <FormControlLabel control={<Checkbox checked={formData.estadual} onChange={handleInputChange} name="estadual" />} label="Estadual" />


                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            Salvar Cliente
                        </Button>
                    </Box>
                </Paper>
            )}

            {loading && <CircularProgress sx={{ display: 'block', margin: '20px auto' }} />}

            {!loading && clientes.length === 0 && !error && (
                <Alert severity="info" sx={{ mt: 2 }}>Nenhum cliente encontrado.</Alert>
            )}

            {clientes.length > 0 && (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 650 }} aria-label="tabela de clientes">
                        <TableHead>
                            <TableRow sx={{ backgroundColor: (theme) => theme.palette.grey[200] }}>
                                <TableCell>ID</TableCell>
                                <TableCell>Nome</TableCell>
                                <TableCell>CNPJ</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Empresa ID</TableCell>
                                <TableCell align="right">Ações</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {currentClientes.map((cliente) => (
                                <TableRow
                                    key={cliente.id}
                                    hover
                                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                >
                                    <TableCell component="th" scope="row">
                                        {cliente.id}
                                    </TableCell>
                                    <TableCell>{cliente.nome}</TableCell>
                                    <TableCell>{cliente.cnpj}</TableCell>
                                    <TableCell>{cliente.statusCliente}</TableCell>
                                    <TableCell>{cliente.fkEmpresa} ({cliente.nomeEmpresa})</TableCell>
                                    <TableCell align="right">
                                        <Tooltip title="Editar">
                                            <IconButton
                                                component={RouterLink}
                                                to={`/clientes/editar/${cliente.id}`} // TODO: Criar página de edição
                                                color="primary"
                                                size="small"
                                                sx={{ mr: 1 }}
                                            >
                                                <EditIcon />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Excluir">
                                            <IconButton
                                                onClick={() => handleOpenDeleteDialog(cliente)}
                                                color="error"
                                                size="small"
                                            >
                                                <DeleteIcon />
                                            </IconButton>
                                        </Tooltip>
                                    </TableCell>
                                </TableRow>
                            ))}
                            {emptyRows > 0 && (
                                <TableRow style={{ height: 53 * emptyRows }}>
                                    <TableCell colSpan={6} />
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                    <TablePagination
                        rowsPerPageOptions={[5, 10, 25]}
                        component="div"
                        count={clientes.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                        labelRowsPerPage="Linhas por página:"
                    />
                </TableContainer>
            )}

            {/* Delete Confirmation Dialog */}
            <Dialog
                open={deleteDialogOpen}
                onClose={handleCloseDeleteDialog}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"Confirmar Exclusão"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Tem certeza de que deseja excluir o cliente "{clienteToDelete?.nome}" (ID: {clienteToDelete?.id})? Esta ação não pode ser desfeita.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteDialog} color="primary">
                        Cancelar
                    </Button>
                    <Button onClick={handleDeleteCliente} color="error" autoFocus>
                        Excluir
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default ClientePage;