import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const AdminRoute = () => {
    const { user, loading } = useAuth();

    if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
    
    // Check if the user is authenticated and has the ADMIN role
    if (!user || user.role !== 'ROLE_ADMIN') {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
};
