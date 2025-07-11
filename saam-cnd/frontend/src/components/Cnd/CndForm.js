import React, { useState, useEffect } from 'react';
import {
    TextField, Button, Grid, Select, MenuItem, InputLabel, FormControl, Box, Typography
} from '@mui/material';

const CndForm = ({ initialData = {}, onSubmit, clientes = [], isEditMode = false }) => {
    const [formData, setFormData] = useState({
        fkCliente: '',
        situacao: '',
        dataEmissao: '',
        dataValidade: '',
        codigoControle: '',
        // arquivo: '', // Omitido por enquanto, upload de arquivo requer tratamento especial
        statusProcessamento: 'MANUAL',
        tipoCertidao: 'Federal',
        orgaoEmissor: '',
        observacoes: '',
        ...initialData // Sobrescreve com initialData, se houver
    });

    useEffect(() => {
        const formattedData = {
            fkCliente: '',
            situacao: '',
            dataEmissao: '',
            dataValidade: '',
            codigoControle: '',
            statusProcessamento: 'MANUAL',
            tipoCertidao: 'Federal',
            orgaoEmissor: '',
            observacoes: '',
            ...initialData };

        if (initialData.dataEmissao) {
            formattedData.dataEmissao = initialData.dataEmissao.split('T')[0];
        }
        if (initialData.dataValidade) {
            formattedData.dataValidade = initialData.dataValidade.split('T')[0];
        }
        // Assegura que fkCliente seja uma string para o Select
        formattedData.fkCliente = initialData.fkCliente ? String(initialData.fkCliente) : '';

        setFormData(formattedData);
    }, [initialData]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    // Adaptação para o Select do MUI que pode passar o evento ou diretamente o valor
    const handleSelectChange = (event) => {
        const { name, value } = event.target;
         setFormData(prevData => ({
            ...prevData,
            [name]: value,
        }));
    };


    const handleSubmit = (e) => {
        e.preventDefault();
        const dataToSubmit = { ...formData };
        if (dataToSubmit.fkCliente) { // Garante que fkCliente seja número se preenchido
            dataToSubmit.fkCliente = parseInt(dataToSubmit.fkCliente, 10);
        } else {
            delete dataToSubmit.fkCliente; // Remove se estiver vazio para não enviar como 0 ou NaN
        }
        onSubmit(dataToSubmit);
    };

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <Grid container spacing={2}>
                {!isEditMode && (
                    <Grid item xs={12}>
                        <FormControl fullWidth required={!isEditMode}>
                            <InputLabel id="fkCliente-label">Cliente Associado</InputLabel>
                            <Select
                                labelId="fkCliente-label"
                                id="fkCliente"
                                name="fkCliente"
                                value={formData.fkCliente}
                                label="Cliente Associado"
                                onChange={handleSelectChange} // Usar handleSelectChange aqui
                            >
                                <MenuItem value="">
                                    <em>Selecione um Cliente</em>
                                </MenuItem>
                                {clientes && clientes.map(cliente => (
                                    <MenuItem key={cliente.id} value={String(cliente.id)}>
                                        {cliente.nome || cliente.cnpj} (ID: {cliente.id})
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                )}
                {isEditMode && formData.fkCliente && (
                    <Grid item xs={12}>
                        <Typography variant="subtitle1">
                            Editando CND para Cliente: {
                                clientes.find(c => String(c.id) === String(formData.fkCliente))?.nome ||
                                clientes.find(c => String(c.id) === String(formData.fkCliente))?.cnpj ||
                                `ID ${formData.fkCliente}`
                            }
                        </Typography>
                    </Grid>
                )}

                <Grid item xs={12} sm={6}>
                    <FormControl fullWidth>
                        <InputLabel id="tipoCertidao-label">Tipo de Certidão</InputLabel>
                        <Select
                            labelId="tipoCertidao-label"
                            id="tipoCertidao"
                            name="tipoCertidao"
                            value={formData.tipoCertidao}
                            label="Tipo de Certidão"
                            onChange={handleSelectChange}
                        >
                            <MenuItem value="Federal">Federal</MenuItem>
                            <MenuItem value="Estadual">Estadual</MenuItem>
                            <MenuItem value="Municipal">Municipal</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>

                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        id="orgaoEmissor"
                        name="orgaoEmissor"
                        label="Órgão Emissor"
                        value={formData.orgaoEmissor}
                        onChange={handleChange}
                        placeholder="Ex: Receita Federal, SEFAZ-SP"
                    />
                </Grid>

                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        id="situacao"
                        name="situacao"
                        label="Situação da Certidão"
                        value={formData.situacao}
                        onChange={handleChange}
                        placeholder="Ex: Negativa, Positiva com Efeito de Negativa"
                    />
                </Grid>

                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        id="dataEmissao"
                        name="dataEmissao"
                        label="Data de Emissão"
                        type="date"
                        value={formData.dataEmissao}
                        onChange={handleChange}
                        InputLabelProps={{ shrink: true }}
                    />
                </Grid>

                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        id="dataValidade"
                        name="dataValidade"
                        label="Data de Validade"
                        type="date"
                        value={formData.dataValidade}
                        onChange={handleChange}
                        InputLabelProps={{ shrink: true }}
                    />
                </Grid>

                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        id="codigoControle"
                        name="codigoControle"
                        label="Código de Controle"
                        value={formData.codigoControle}
                        onChange={handleChange}
                    />
                </Grid>

                {isEditMode && ( // Mostrar statusProcessamento apenas em edição
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            id="statusProcessamento"
                            name="statusProcessamento"
                            label="Status do Processamento Interno"
                            value={formData.statusProcessamento}
                            onChange={handleChange}
                            // disabled // Poderia ser desabilitado se for apenas informativo
                        />
                    </Grid>
                )}

                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        id="observacoes"
                        name="observacoes"
                        label="Observações"
                        value={formData.observacoes}
                        onChange={handleChange}
                        multiline
                        rows={3}
                    />
                </Grid>
            </Grid>

            <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
            >
                {isEditMode ? 'Salvar Alterações na CND' : 'Cadastrar Nova CND'}
            </Button>
        </Box>
    );
};

export default CndForm;
