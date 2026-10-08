export const AdminDashboard = () => {
    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-6">Admin Dashboard</h1>
            <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 mb-8">
                <h2 className="text-xl font-semibold mb-4">System Overview</h2>
                <p className="text-gray-500">Welcome to the admin panel. Here you can view system analytics and manage users and URLs.</p>
                <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="bg-gray-50 p-6 rounded-xl border border-gray-100">
                        <h3 className="font-semibold text-gray-700">Users</h3>
                        <p className="text-sm text-gray-500 mt-2">Manage all registered users</p>
                    </div>
                    <div className="bg-gray-50 p-6 rounded-xl border border-gray-100">
                        <h3 className="font-semibold text-gray-700">All URLs</h3>
                        <p className="text-sm text-gray-500 mt-2">View and manage all shortened URLs</p>
                    </div>
                    <div className="bg-gray-50 p-6 rounded-xl border border-gray-100">
                        <h3 className="font-semibold text-gray-700">System Analytics</h3>
                        <p className="text-sm text-gray-500 mt-2">View system-wide traffic and metrics</p>
                    </div>
                </div>
            </div>
        </div>
    );
};
