import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Páginas públicas
import Index            from './pages/Index';
import Login            from './pages/Login';
import Registro         from './pages/Registro';
import RegistroEmpresa  from './pages/RegistroEmpresa';
import RegistroOferente from './pages/RegistroOferente';
import RegistroPendiente from './pages/RegistroPendiente';
import BuscarPuestos    from './pages/BuscarPuestos';

// Admin
import AdminDashboard      from './pages/admin/Dashboard';
import EmpresasPendientes  from './pages/admin/EmpresasPendientes';
import OferentesPendientes from './pages/admin/OferentesPendientes';
import Caracteristicas     from './pages/admin/Caracteristicas';
import Reportes            from './pages/admin/Reportes';

export default function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Públicas */}
            <Route path="/"                   element={<Index />} />
            <Route path="/login"              element={<Login />} />
            <Route path="/registro"           element={<Registro />} />
            <Route path="/registro/empresa"   element={<RegistroEmpresa />} />
            <Route path="/registro/oferente"  element={<RegistroOferente />} />
            <Route path="/registro-pendiente" element={<RegistroPendiente />} />
            <Route path="/buscar-puestos"     element={<BuscarPuestos />} />

            {/* Admin */}
            <Route path="/admin" element={<ProtectedRoute rol="ADMIN" />}>
              <Route path="dashboard"            element={<AdminDashboard />} />
              <Route path="empresas/pendientes"  element={<EmpresasPendientes />} />
              <Route path="oferentes/pendientes" element={<OferentesPendientes />} />
              <Route path="caracteristicas"      element={<Caracteristicas />} />
              <Route path="reportes"             element={<Reportes />} />
            </Route>

            {/* Empresa y Oferente — se completan en Fase 6 y 7 */}
            <Route path="/empresa/*"  element={<ProtectedRoute rol="EMPRESA" />} />
            <Route path="/oferente/*" element={<ProtectedRoute rol="OFERENTE" />} />

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}