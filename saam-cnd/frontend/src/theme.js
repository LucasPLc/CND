import { createTheme } from '@mui/material/styles';

// Exemplo de um tema básico para começar
// Cores podem ser ajustadas conforme o padrão SAAM (se houver um guia de estilo)
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2', // Um azul padrão do MUI
      // light: '#42a5f5',
      // dark: '#1565c0',
      // contrastText: '#fff',
    },
    secondary: {
      main: '#dc004e', // Um rosa/vermelho padrão do MUI
      // light: '#ff79b0',
      // dark: '#c51162',
      // contrastText: '#fff',
    },
    background: {
      default: '#f4f6f8', // Um cinza claro para o fundo geral
      paper: '#ffffff',   // Branco para componentes Paper (cards, tabelas)
    },
    // Você pode adicionar mais customizações aqui: text, error, warning, info, success
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
      fontSize: '1.75rem',
    },
    h5: {
      fontWeight: 600,
      fontSize: '1.5rem',
    },
    h6: {
      fontWeight: 600,
      fontSize: '1.25rem',
    },
    // Customizar outros variantes se necessário
  },
  components: {
    // Exemplo de customização global para Botões
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none', // Padrão SAAM pode preferir botões não capitalizados
          // borderRadius: 8,
        },
        containedPrimary: {
          // color: '#fff', // Se o contraste do main não for suficiente
        }
      }
    },
    MuiTableHead: {
        styleOverrides: {
            root: {
                backgroundColor: '#e0e0e0', // Um cinza um pouco mais escuro para cabeçalhos de tabela
                '& .MuiTableCell-head': {
                    fontWeight: 'bold',
                }
            }
        }
    },
    MuiPaper: {
        styleOverrides: {
            root: {
                // borderRadius: 8, // Se quiser bordas mais arredondadas
            }
        }
    }
    // Adicionar customizações para outros componentes (MuiTextField, MuiTable, etc.)
  }
});

export default theme;
