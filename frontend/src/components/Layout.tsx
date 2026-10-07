import { Outlet, Navigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, Link as LinkIcon, LogOut } from 'lucide-react';

export const ProtectedLayout = () => {
    const { user, loading, logout } = useAuth();

    if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
    if (!user) return <Navigate to="/login" />;

    return (
        <div className="flex h-screen bg-gray-50">
            {/* Sidebar */}
            <aside className="w-64 bg-white border-r border-gray-200">
                <div className="p-6">
                    <h1 className="text-2xl font-bold text-primary-600">Shorty</h1>
                </div>
                <nav className="mt-6 px-4 space-y-2">
                    <Link to="/dashboard" className="flex items-center px-4 py-2 text-gray-700 bg-gray-100 rounded-lg">
                        <LayoutDashboard className="w-5 h-5 mr-3" /> Dashboard
                    </Link>
                    <Link to="/urls" className="flex items-center px-4 py-2 text-gray-700 hover:bg-gray-100 rounded-lg">
                        <LinkIcon className="w-5 h-5 mr-3" /> My Links
                    </Link>
                </nav>
                <div className="absolute bottom-0 w-64 p-4 border-t border-gray-200">
                    <button onClick={logout} className="flex items-center w-full px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg">
                        <LogOut className="w-5 h-5 mr-3" /> Logout
                    </button>
                </div>
            </aside>
            {/* Main Content */}
            <main className="flex-1 overflow-y-auto">
                <Outlet />
            </main>
        </div>
    );
};
