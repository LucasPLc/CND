import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link as RouterLink, useLocation } from 'react-router-dom';
import {
    AppBar, Toolbar, Typography, Drawer, List, ListItem, ListItemIcon,
    ListItemText, CssBaseline, Box, IconButton, Tooltip, Divider
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import HomeIcon from '@mui/icons-material/Home';
import PeopleIcon from '@mui/icons-material/People';
import DescriptionIcon from '@mui/icons-material/Description'; // Para CNDs
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { styled, useTheme } from '@mui/material/styles';

// Import das páginas
import HomePage from './pages/HomePage';
import ClientePage from './pages/ClientePage';
import CndDashboardPage from './pages/CndDashboardPage';
import CndDetailPage from './pages/CndDetailPage';
import CndFormPage from './pages/CndFormPage';

const drawerWidth = 240;

const Main = styled('main', { shouldForwardProp: (prop) => prop !== 'open' })(
  ({ theme, open }) => ({
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: `-${drawerWidth}px`,
    ...(open && {
      transition: theme.transitions.create('margin', {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
      marginLeft: 0,
    }),
  }),
);

const StyledAppBar = styled(AppBar, { shouldForwardProp: (prop) => prop !== 'open' })(
  ({ theme, open }) => ({
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: `${drawerWidth}px`,
      transition: theme.transitions.create(['margin', 'width'], {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
    }),
  }),
);

const DrawerHeader = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  padding: theme.spacing(0, 1),
  ...theme.mixins.toolbar,
  justifyContent: 'flex-end',
}));

function App() {
  const theme = useTheme();
  const [open, setOpen] = useState(true); // Drawer começa aberto

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  const NavItem = ({ to, icon, primary, open }) => {
    const location = useLocation();
    const isActive = location.pathname === to || (location.pathname.startsWith(to) && to !== "/");


    return (
      <ListItem
        button
        component={RouterLink}
        to={to}
        selected={isActive}
        sx={{
            ...(isActive && {
                backgroundColor: theme.palette.action.selected,
                '&:hover': {
                    backgroundColor: theme.palette.action.hover,
                },
            }),
             my: 0.5,
             mx: open ? 1 : 'auto', // Centraliza ícone quando drawer está fechado
             borderRadius: open ? theme.shape.borderRadius : '50%',
             width: open ? 'auto' : 48, // Largura do ícone quando fechado
             height: open ? 'auto' : 48, // Altura do ícone quando fechado
             justifyContent: open ? 'initial' : 'center',
             px: open ? 2.5 : 'initial',
        }}
      >
        <ListItemIcon sx={{
            minWidth: 0,
            mr: open ? 3 : 'auto',
            justifyContent: 'center',
            color: isActive ? theme.palette.primary.main : 'inherit'
            }}>
            {icon}
        </ListItemIcon>
        <ListItemText primary={primary} sx={{ opacity: open ? 1 : 0, color: isActive ? theme.palette.primary.main : 'inherit' }} />
      </ListItem>
    );
  };

  const menuItems = [
    { text: 'Home', icon: <HomeIcon />, path: '/' },
    { text: 'Clientes', icon: <PeopleIcon />, path: '/clientes' },
    { text: 'CND Dashboard', icon: <DescriptionIcon />, path: '/cnds' },
  ];

  return (
    <Router>
      <Box sx={{ display: 'flex' }}>
        <CssBaseline />
        <StyledAppBar position="fixed" open={open}>
          <Toolbar>
            <IconButton
              color="inherit"
              aria-label="open drawer"
              onClick={handleDrawerOpen}
              edge="start"
              sx={{ mr: 2, ...(open && { display: 'none' }) }}
            >
              <MenuIcon />
            </IconButton>
            <Typography variant="h6" noWrap component="div">
              SAAM-CND
            </Typography>
          </Toolbar>
        </StyledAppBar>
        <Drawer
          sx={{
            width: drawerWidth,
            flexShrink: 0,
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              boxSizing: 'border-box',
            },
          }}
          variant="persistent"
          anchor="left"
          open={open}
        >
          <DrawerHeader>
            <Typography variant="h6" sx={{ flexGrow: 1, pl: 2 }}>Menu</Typography>
            <IconButton onClick={handleDrawerClose}>
              {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
            </IconButton>
          </DrawerHeader>
          <Divider />
          <List>
            {menuItems.map((item) => (
                <NavItem key={item.text} to={item.path} icon={item.icon} primary={item.text} open={open} />
            ))}
          </List>
        </Drawer>
        <Main open={open}>
          <DrawerHeader /> {/* Para dar espaço abaixo do AppBar */}
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/clientes" element={<ClientePage />} />
            <Route path="/cnds" element={<CndDashboardPage />} />
            <Route path="/cnds/novo" element={<CndFormPage />} />
            <Route path="/cnds/editar/:id" element={<CndFormPage />} />
            <Route path="/cnds/detalhes/:id" element={<CndDetailPage />} />
            {/* Adicionar outras rotas aqui conforme necessário */}
          </Routes>
           <Box component="footer" sx={{ bgcolor: 'background.paper', p: 2, mt: 'auto', textAlign: 'center' }}>
                <Typography variant="body2" color="text.secondary">
                    &copy; {new Date().getFullYear()} SAAM-CND. Todos os direitos reservados.
                </Typography>
            </Box>
        </Main>
      </Box>
    </Router>
  );
}

export default App;
