import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Páginas públicas
import Index             from './pages/Index';
import Login             from './pages/Login';
import Registro          from './pages/Registro';
import RegistroEmpresa   from './pages/RegistroEmpresa';
import RegistroOferente  from './pages/RegistroOferente';
import RegistroPendiente from './pages/RegistroPendiente';
import BuscarPuestos     from './pages/BuscarPuestos';

// Admin
import AdminDashboard      from './pages/admin/Dashboard';
import EmpresasPendientes  from './pages/admin/EmpresasPendientes';
import OferentesPendientes from './pages/admin/OferentesPendientes';
import Caracteristicas     from './pages/admin/Caracteristicas';
import Reportes            from './pages/admin/Reportes';

// Oferente
import OferenteDashboard from './pages/oferente/Dashboard';
import Habilidades       from './pages/oferente/Habilidades';
import CV                from './pages/oferente/CV';
import Postulacion       from './pages/oferente/Postulacion';
import MisPostulaciones  from './pages/oferente/MisPostulaciones';

// Empresa
import EmpresaDashboard  from './pages/empresa/Dashboard';
import MisPuestos        from './pages/empresa/MisPuestos';
import NuevoPuesto       from './pages/empresa/NuevoPuesto';
import Requisitos        from './pages/empresa/Requisitos';
import Candidatos        from './pages/empresa/Candidatos';
import DetalleCandidato  from './pages/empresa/DetalleCandidato';
import Postulaciones     from './pages/empresa/Postulaciones';
import Reporte           from './pages/empresa/Reporte';

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

            {/* Oferente */}
            <Route path="/oferente" element={<ProtectedRoute rol="OFERENTE" />}>
              <Route path="dashboard"     element={<OferenteDashboard />} />
              <Route path="habilidades"   element={<Habilidades />} />
              <Route path="cv"            element={<CV />} />
              <Route path="postulacion"   element={<Postulacion />} />
              <Route path="postulaciones" element={<MisPostulaciones />} />
            </Route>

            {/* Empresa */}
            <Route path="/empresa" element={<ProtectedRoute rol="EMPRESA" />}>
              <Route path="dashboard"                       element={<EmpresaDashboard />} />
              <Route path="puestos"                         element={<MisPuestos />} />
              <Route path="puestos/nuevo"                   element={<NuevoPuesto />} />
              <Route path="puestos/:id/requisitos"          element={<Requisitos />} />
              <Route path="puestos/:id/candidatos"          element={<Candidatos />} />
              <Route path="puestos/:id/postulaciones"       element={<Postulaciones />} />
              <Route path="candidatos/:oferenteId"          element={<DetalleCandidato />} />
              <Route path="reportes"                        element={<Reporte />} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}