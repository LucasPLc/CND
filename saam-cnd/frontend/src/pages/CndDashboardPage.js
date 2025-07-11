import React, { useState, useEffect, useCallback } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { getCndResultados, deleteCndResultado, consultarCndFederalApi, sincronizarCndMgApi, downloadCndPdf, getClientes } from '../services/apiService'; // getClientes adicionado
import {
    Container, Typography, Button, Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper, CircularProgress, Alert, Dialog, DialogActions,
    DialogContent, DialogContentText, DialogTitle, TextField, Box, IconButton, Tooltip,
    Grid, Accordion, AccordionSummary, AccordionDetails, TablePagination,
    Select, MenuItem, FormControl, InputLabel, Autocomplete // Adicionados para o novo formulário
} from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import SearchIcon from '@mui/icons-material/Search';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import VisibilityIcon from '@mui/icons-material/Visibility';
import SyncIcon from '@mui/icons-material/Sync'; // Para Sinc MG
import AccountBalanceIcon from '@mui/icons-material/AccountBalance'; // Para Federal

const CndDashboardPage = () => {
    const [resultados, setResultados] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [filters, setFilters] = useState({
        cnpjCliente: '',
        situacao: '',
        statusProcessamento: '',
        // TODO: Adicionar filtros de data (dataEmissaoInicio, dataEmissaoFim) com DatePickers
    });
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [resultadoToDelete, setResultadoToDelete] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10); // Default para 10 linhas

    // Estados para o novo formulário de consulta
    const [clientes, setClientes] = useState([]);
    const [selectedCliente, setSelectedCliente] = useState(null); // Para Autocomplete
    const [cnpjAvulso, setCnpjAvulso] = useState(''); // Não usado ainda, mas para a ideia de CNPJ avulso
    const [tipoNovaConsulta, setTipoNovaConsulta] = useState(''); // Ex: 'federal', 'sefaz-mg'
    const [loadingClientes, setLoadingClientes] = useState(false);
    const [loadingNovaConsulta, setLoadingNovaConsulta] = useState(false);


    const fetchResultados = useCallback(async () => {
        setLoading(true);
        setError(null);
        // setSuccessMessage(null);
        try {
            const activeFilters = Object.fromEntries(
                Object.entries(filters).filter(([_, value]) => value !== '')
            );
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
        // Buscar clientes para o formulário de nova consulta
        const fetchClientesParaForm = async () => {
            setLoadingClientes(true);
            try {
                const data = await getClientes();
                setClientes(data || []);
            } catch (err) {
                console.error("Erro ao buscar clientes para formulário:", err);
                // Não definir setError global aqui para não interferir com erros da tabela principal
            } finally {
                setLoadingClientes(false);
            }
        };
        fetchClientesParaForm();
    }, [fetchResultados]); // fetchResultados é dependência para recarregar a tabela principal

    const clearMessages = () => {
        setError(null);
        setSuccessMessage(null);
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prevFilters => ({
            ...prevFilters,
            [name]: value,
        }));
    };

    const handleApplyFilters = () => {
        clearMessages();
        fetchResultados();
    };

    const handleOpenDeleteDialog = (resultado) => {
        setResultadoToDelete(resultado);
        setDeleteDialogOpen(true);
    };

    const handleCloseDeleteDialog = () => {
        setResultadoToDelete(null);
        setDeleteDialogOpen(false);
    };

    const handleDeleteResultado = async () => {
        if (!resultadoToDelete) return;
        clearMessages();
        try {
            await deleteCndResultado(resultadoToDelete.id);
            setSuccessMessage(`Resultado CND ID ${resultadoToDelete.id} excluído com sucesso.`);
            fetchResultados();
        } catch (err) {
            setError('Falha ao excluir resultado CND: ' + (err.response?.data?.message || err.message));
            console.error(err);
        } finally {
            handleCloseDeleteDialog();
        }
    };

    const handleConsultarFederal = async (clienteId) => {
        clearMessages();
        setLoading(true); // Poderia ter um loading específico para a ação
        try {
            const novoResultado = await consultarCndFederalApi(clienteId);
            setSuccessMessage(`Consulta Federal para cliente ID ${clienteId} disparada com sucesso. Novo Resultado ID: ${novoResultado.id}, Situação: ${novoResultado.situacao || 'N/A'}`);
            fetchResultados(); // Recarrega para ver o novo resultado
        } catch (consultaError) {
            setError('Falha ao consultar CND Federal: ' + (consultaError.response?.data?.message || consultaError.message));
        } finally {
            setLoading(false);
        }
    };

    const handleSincronizarMg = async (clienteId) => {
        clearMessages();
        setLoading(true); // Poderia ter um loading específico para a ação
        try {
            const novoResultado = await sincronizarCndMgApi(clienteId);
            setSuccessMessage(`Sincronização MG para cliente ID ${clienteId} disparada com sucesso. Novo Resultado ID: ${novoResultado.id}, Situação: ${novoResultado.situacao || 'N/A'}`);
            fetchResultados();
        } catch (sincError) {
            setError('Falha ao sincronizar CND MG: ' + (sincError.response?.data?.message || sincError.message));
        } finally {
            setLoading(false);
        }
    };

    const handleDownloadPdf = async (resultadoId, nomeArquivoSugerido) => {
        clearMessages();
        try {
            await downloadCndPdf(resultadoId); // O nome do arquivo é tratado no apiService
            setSuccessMessage(`Download do PDF ${nomeArquivoSugerido} iniciado.`);
        } catch (downloadError) {
            setError('Falha ao baixar PDF: ' + (downloadError.response?.data?.message || downloadError.message));
        }
    };

    const handleNovaConsultaSubmit = async () => {
        if (!selectedCliente || !tipoNovaConsulta) {
            setError("Por favor, selecione um cliente e um tipo de consulta.");
            return;
        }
        clearMessages();
        setLoadingNovaConsulta(true);
        try {
            let novoResultado;
            if (tipoNovaConsulta === 'federal') {
                novoResultado = await consultarCndFederalApi(selectedCliente.id);
                setSuccessMessage(`Consulta Federal para cliente ${selectedCliente.nome} (ID: ${selectedCliente.id}) disparada. Situação: ${novoResultado.situacao || 'N/A'}`);
            } else if (tipoNovaConsulta === 'sefaz-mg') {
                novoResultado = await sincronizarCndMgApi(selectedCliente.id);
                setSuccessMessage(`Sincronização MG para cliente ${selectedCliente.nome} (ID: ${selectedCliente.id}) disparada. Situação: ${novoResultado.situacao || 'N/A'}`);
            }
            fetchResultados(); // Recarrega a lista de resultados
            setSelectedCliente(null); // Limpa seleção
            setTipoNovaConsulta(''); // Limpa seleção
        } catch (err) {
            setError(`Falha ao iniciar nova consulta (${tipoNovaConsulta}): ` + (err.response?.data?.message || err.message));
        } finally {
            setLoadingNovaConsulta(false);
        }
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    // Calcula os resultados para a página atual
    const currentResultados = resultados.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
    const emptyRows = page > 0 ? Math.max(0, (1 + page) * rowsPerPage - resultados.length) : 0;


    // if (loading && resultados.length === 0) {
    //     return (
    //         <Container style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
    //             <CircularProgress />
    //         </Container>
    //     );
    // }

    return (
        <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Monitoramento de Certidões Negativas de Débitos (TESTE SIMPLIFICADO)
            </Typography>
            <p>Se esta mensagem aparecer, o componente CndDashboardPage está renderizando o básico.</p>
            <p>Loading: {loading.toString()}</p>
            <p>Error: {error}</p>
            <p>Success: {successMessage}</p>
            <p>Resultados Count: {resultados.length}</p>
            <p>Clientes Count (para form): {clientes.length}</p>


            {error && <Alert severity="error" onClose={clearMessages} sx={{ mb: 2 }}>{error}</Alert>}
            {successMessage && <Alert severity="success" onClose={clearMessages} sx={{ mb: 2 }}>{successMessage}</Alert>}

            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Button
                    variant="contained"
                    startIcon={<AddCircleOutlineIcon />}
                    component={RouterLink}
                    to="/cnds/novo" // Assumindo que CndFormPage será em /cnds/novo
                    onClick={clearMessages}
                >
                    Nova CND (Manual)
                </Button>
                <Tooltip title="Atualizar Lista">
                    <IconButton onClick={() => { clearMessages(); fetchResultados(); }} color="primary">
                        <RefreshIcon />
                    </IconButton>
                </Tooltip>
            </Box>

            <Accordion sx={{ mb: 2 }}>
                <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls="filters-panel-content"
                    id="filters-panel-header"
                >
                    <Typography>Filtros de Pesquisa</Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={4}>
                            <TextField
                                fullWidth
                                label="CNPJ do Cliente"
                                name="cnpjCliente"
                                value={filters.cnpjCliente}
                                onChange={handleFilterChange}
                                variant="outlined"
                                size="small"
                            />
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <TextField
                                fullWidth
                                label="Situação da Certidão"
                                name="situacao"
                                value={filters.situacao}
                                onChange={handleFilterChange}
                                variant="outlined"
                                size="small"
                            />
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <TextField
                                fullWidth
                                label="Status do Processamento"
                                name="statusProcessamento"
                                value={filters.statusProcessamento}
                                onChange={handleFilterChange}
                                variant="outlined"
                                size="small"
                            />
                        </Grid>
                        <Grid item xs={12} container justifyContent="flex-end">
                            <Button
                                variant="contained"
                                startIcon={<SearchIcon />}
                                onClick={handleApplyFilters}
                            >
                                Aplicar Filtros
                            </Button>
                        </Grid>
                    </Grid>
                </AccordionDetails>
            </Accordion>

            <Accordion sx={{ mb: 2 }}>
                <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls="nova-consulta-panel-content"
                    id="nova-consulta-panel-header"
                >
                    <Typography>Nova Consulta de CND</Typography>
                </AccordionSummary>
                <AccordionDetails>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={6}>
                            <Autocomplete
                                options={clientes}
                                getOptionLabel={(option) => `${option.nome} (${option.cnpj})` || ''}
                                value={selectedCliente}
                                onChange={(event, newValue) => {
                                    setSelectedCliente(newValue);
                                }}
                                loading={loadingClientes}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        label="Selecione um Cliente"
                                        variant="outlined"
                                        size="small"
                                        InputProps={{
                                            ...params.InputProps,
                                            endAdornment: (
                                                <>
                                                    {loadingClientes ? <CircularProgress color="inherit" size={20} /> : null}
                                                    {params.InputProps.endAdornment}
                                                </>
                                            ),
                                        }}
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12} sm={4}>
                            <FormControl fullWidth size="small">
                                <InputLabel id="tipo-nova-consulta-label">Tipo de Consulta</InputLabel>
                                <Select
                                    labelId="tipo-nova-consulta-label"
                                    value={tipoNovaConsulta}
                                    label="Tipo de Consulta"
                                    onChange={(e) => setTipoNovaConsulta(e.target.value)}
                                >
                                    <MenuItem value=""><em>Selecione</em></MenuItem>
                                    <MenuItem value="federal">Receita Federal (CND PJ)</MenuItem>
                                    <MenuItem value="sefaz-mg">SEFAZ-MG (CDT)</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={2} container justifyContent="flex-end">
                            <Button
                                variant="contained"
                                color="secondary"
                                onClick={handleNovaConsultaSubmit}
                                disabled={!selectedCliente || !tipoNovaConsulta || loadingNovaConsulta}
                                startIcon={loadingNovaConsulta ? <CircularProgress size={20} color="inherit" /> : <SearchIcon />}
                            >
                                Consultar
                            </Button>
                        </Grid>
                    </Grid>
                </AccordionDetails>
            </Accordion>


            {loading && <Box sx={{ display: 'flex', justifyContent: 'center', my:2 }}><CircularProgress /></Box>}

            {!loading && resultados.length === 0 && !error && (
                <Alert severity="info" sx={{ mt: 2 }}>Nenhum resultado de CND encontrado para os filtros aplicados.</Alert>
            )}

            {resultados.length > 0 && (
                <TableContainer component={Paper} sx={{ mt: 2 }}>
                    <Table sx={{ minWidth: 1024 }} aria-label="tabela de resultados cnd">
                        <TableHead>
                            <TableRow sx={{ backgroundColor: (theme) => theme.palette.grey[200] }}>
                                <TableCell>ID</TableCell>
                                <TableCell>Cliente (CNPJ)</TableCell>
                                <TableCell>Data Processamento</TableCell>
                                <TableCell>Situação</TableCell>
                                <TableCell>Data Emissão</TableCell>
                                <TableCell>Data Validade</TableCell>
                                <TableCell>Status Processo</TableCell>
                                <TableCell align="center">Ações</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {currentResultados.map((res) => (
                                <TableRow
                                    key={res.id}
                                    hover
                                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                >
                                    <TableCell>{res.id}</TableCell>
                                    <TableCell>{res.cliente?.cnpj || res.fkCliente || 'N/A'}</TableCell>
                                    <TableCell>{res.dataProcessamento ? new Date(res.dataProcessamento).toLocaleString() : '-'}</TableCell>
                                    <TableCell>{res.situacao || '-'}</TableCell>
                                    <TableCell>{res.dataEmissao ? new Date(res.dataEmissao).toLocaleDateString() : '-'}</TableCell>
                                    <TableCell>{res.dataValidade ? new Date(res.dataValidade).toLocaleDateString() : '-'}</TableCell>
                                    <TableCell>{res.statusProcessamento || '-'}</TableCell>
                                    <TableCell align="center">
                                        <Tooltip title="Ver Detalhes">
                                            <IconButton component={RouterLink} to={`/cnds/detalhes/${res.id}`} size="small" sx={{ mr: 0.5 }}>
                                                <VisibilityIcon />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Editar CND (Manual)">
                                            <IconButton component={RouterLink} to={`/cnds/editar/${res.id}`} size="small" sx={{ mr: 0.5 }}>
                                                <EditIcon />
                                            </IconButton>
                                        </Tooltip>
                                        {res.arquivo && (
                                        <Tooltip title={`Baixar PDF ${res.nomeArquivo || ''}`}>
                                            <IconButton onClick={() => handleDownloadPdf(res.id, res.nomeArquivo)} size="small" sx={{ mr: 0.5 }} color="secondary" disabled={!res.arquivo}>
                                                <CloudDownloadIcon />
                                            </IconButton>
                                        </Tooltip>
                                        )}
                                        {res.fkCliente && (
                                            <>
                                            <Tooltip title={`Consultar CND Federal (Cliente ID: ${res.fkCliente})`}>
                                                <IconButton onClick={() => handleConsultarFederal(res.fkCliente)} size="small" sx={{ mr: 0.5 }} color="primary">
                                                    <AccountBalanceIcon />
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title={`Sincronizar CND MG (Cliente ID: ${res.fkCliente})`}>
                                                <IconButton onClick={() => handleSincronizarMg(res.fkCliente)} size="small" sx={{ mr: 0.5 }} color="primary">
                                                    <SyncIcon />
                                                </IconButton>
                                            </Tooltip>
                                            </>
                                        )}
                                        <Tooltip title="Excluir Resultado">
                                            <IconButton onClick={() => handleOpenDeleteDialog(res)} size="small" color="error">
                                                <DeleteIcon />
                                            </IconButton>
                                        </Tooltip>
                                    </TableCell>
                                </TableRow>
                            ))}
                             {emptyRows > 0 && (
                                <TableRow style={{ height: 53 * emptyRows }}>
                                    <TableCell colSpan={8} />
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                     <TablePagination
                        rowsPerPageOptions={[5, 10, 25, 50]}
                        component="div"
                        count={resultados.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                        labelRowsPerPage="Resultados por página:"
                    />
                </TableContainer>
            )}

            <Dialog open={deleteDialogOpen} onClose={handleCloseDeleteDialog}>
                <DialogTitle>Confirmar Exclusão</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Tem certeza de que deseja excluir o Resultado CND ID {resultadoToDelete?.id}?
                        {resultadoToDelete?.cliente?.cnpj && ` (Cliente: ${resultadoToDelete.cliente.cnpj})`}
                        Esta ação não pode ser desfeita.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteDialog}>Cancelar</Button>
                    <Button onClick={handleDeleteResultado} color="error" autoFocus>Excluir</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default CndDashboardPage;
