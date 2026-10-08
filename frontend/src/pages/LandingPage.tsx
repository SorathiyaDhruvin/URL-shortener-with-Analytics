import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LinkIcon } from 'lucide-react';

export const LandingPage = () => {
    const { user, loading } = useAuth();

    if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
    
    if (user) {
        return <Navigate to="/dashboard" replace />;
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navigation */}
            <nav className="bg-white shadow-sm border-b border-gray-100">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex items-center">
                            <div className="h-8 w-8 bg-primary-600 rounded-lg flex items-center justify-center shadow-sm">
                                <LinkIcon className="h-5 w-5 text-white" />
                            </div>
                            <span className="ml-2 text-xl font-bold text-gray-900">Shorty</span>
                        </div>
                        <div className="flex items-center space-x-4">
                            <Link to="/login" className="text-gray-600 hover:text-primary-600 font-medium transition-colors">
                                Sign In
                            </Link>
                            <Link to="/register" className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 font-medium transition-colors shadow-sm">
                                Get Started
                            </Link>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Hero Section */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center">
                <h1 className="text-5xl font-extrabold text-gray-900 tracking-tight mb-6">
                    Shorten Your Links, <span className="text-primary-600">Expand Your Reach</span>
                </h1>
                <p className="text-xl text-gray-500 mb-10 max-w-2xl mx-auto">
                    Create short, memorable links and track their performance with our powerful analytics platform.
                </p>
                <div className="flex justify-center space-x-4">
                    <Link to="/register" className="bg-primary-600 text-white px-8 py-3 rounded-xl hover:bg-primary-700 font-semibold text-lg transition-colors shadow-lg shadow-primary-500/30">
                        Get Started
                    </Link>
                    <Link to="/login" className="bg-white text-gray-700 border border-gray-200 px-8 py-3 rounded-xl hover:bg-gray-50 font-semibold text-lg transition-colors shadow-sm">
                        Sign In
                    </Link>
                </div>
            </main>
        </div>
    );
};
