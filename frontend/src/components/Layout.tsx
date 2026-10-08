import { Outlet, Navigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, Link as LinkIcon, LogOut, PlusCircle, BarChart2, User, Settings } from 'lucide-react';

export const ProtectedLayout = () => {
    const { user, loading, logout } = useAuth();
    const location = useLocation();

    if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
    if (!user) return <Navigate to="/login" />;

    const navItems = [
        { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
        { name: 'My Links', path: '/urls', icon: LinkIcon },
        { name: 'Create Link', path: '/dashboard', icon: PlusCircle }, // Assuming create is on dashboard
        { name: 'Analytics', path: '/dashboard', icon: BarChart2 }, // Assuming general analytics is on dashboard
        { name: 'Profile', path: '/profile', icon: User },
        { name: 'Settings', path: '/settings', icon: Settings },
    ];

    if (user?.role === 'ROLE_ADMIN') {
        navItems.splice(1, 0, { name: 'Admin Dashboard', path: '/admin', icon: LayoutDashboard });
    }

    return (
        <div className="flex h-screen bg-gray-50">
            {/* Sidebar */}
            <aside className="w-64 bg-white border-r border-gray-200 relative flex flex-col">
                <div className="p-6 flex items-center">
                    <div className="h-8 w-8 bg-primary-600 rounded-lg flex items-center justify-center shadow-sm mr-3">
                        <LinkIcon className="h-5 w-5 text-white" />
                    </div>
                    <h1 className="text-2xl font-bold text-gray-900">Shorty</h1>
                </div>
                <nav className="mt-2 px-4 space-y-1 flex-1 overflow-y-auto">
                    {navItems.map((item) => {
                        const isActive = location.pathname === item.path || (item.name === 'Analytics' && location.pathname.includes('/analytics'));
                        return (
                            <Link 
                                key={item.name}
                                to={item.path} 
                                className={`flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${
                                    isActive 
                                        ? 'bg-primary-50 text-primary-700' 
                                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                                }`}
                            >
                                <item.icon className={`w-5 h-5 mr-3 ${isActive ? 'text-primary-600' : 'text-gray-400'}`} /> 
                                {item.name}
                            </Link>
                        );
                    })}
                </nav>
                <div className="p-4 border-t border-gray-200">
                    <button onClick={logout} className="flex items-center w-full px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                        <LogOut className="w-5 h-5 mr-3 text-red-500" /> Logout
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
