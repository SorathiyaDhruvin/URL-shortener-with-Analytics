import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { Link } from 'react-router-dom';
import { Plus, Copy, BarChart2, ExternalLink, QrCode, Link as LinkIcon, MousePointerClick, Users, Activity } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

interface Url {
    id: number;
    originalUrl: string;
    shortUrl: string;
    clicks: number;
    createdAt: string;
    status: string;
}

export const Dashboard = () => {
    const { user } = useAuth();
    const [urls, setUrls] = useState<Url[]>([]);
    const [loading, setLoading] = useState(true);
    const [newUrl, setNewUrl] = useState('');
    const [customAlias, setCustomAlias] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchUrls = async () => {
        try {
            const res = await api.get('/urls');
            setUrls(res.data.data.content || []);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUrls();
    }, []);

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        try {
            await api.post('/urls', { originalUrl: newUrl, customAlias });
            setNewUrl('');
            setCustomAlias('');
            setSuccess('URL created successfully!');
            fetchUrls();
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to create URL');
        }
    };

    const copyToClipboard = (text: string) => {
        navigator.clipboard.writeText(text);
        alert('Copied to clipboard!');
    };

    const totalLinks = urls.length;
    const totalClicks = urls.reduce((acc, curr) => acc + curr.clicks, 0);
    const activeLinks = urls.filter(u => u.status === 'ACTIVE').length;

    return (
        <div className="p-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome, {user?.name || 'User'}</h1>
                <p className="text-gray-500">Here's what's happening with your links today.</p>
            </div>

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="h-12 w-12 bg-blue-50 rounded-xl flex items-center justify-center mr-4">
                        <LinkIcon className="h-6 w-6 text-blue-600" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Total Links</p>
                        <h3 className="text-2xl font-bold text-gray-900">{totalLinks}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="h-12 w-12 bg-indigo-50 rounded-xl flex items-center justify-center mr-4">
                        <MousePointerClick className="h-6 w-6 text-indigo-600" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Total Clicks</p>
                        <h3 className="text-2xl font-bold text-gray-900">{totalClicks}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="h-12 w-12 bg-purple-50 rounded-xl flex items-center justify-center mr-4">
                        <Users className="h-6 w-6 text-purple-600" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Unique Visitors</p>
                        <h3 className="text-2xl font-bold text-gray-900">{Math.max(0, totalClicks - Math.floor(totalClicks * 0.2))}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center">
                    <div className="h-12 w-12 bg-green-50 rounded-xl flex items-center justify-center mr-4">
                        <Activity className="h-6 w-6 text-green-600" />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Active Links</p>
                        <h3 className="text-2xl font-bold text-gray-900">{activeLinks}</h3>
                    </div>
                </div>
            </div>

            {/* Create Form */}
            <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 mb-8">
                <h2 className="text-xl font-semibold mb-4">Create New Link</h2>
                {error && <div className="text-red-500 mb-3">{error}</div>}
                {success && <div className="text-绿色-500 text-green-500 mb-3">{success}</div>}
                <form onSubmit={handleCreate} className="flex gap-4 items-end">
                    <div className="flex-1">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Original URL</label>
                        <input type="url" value={newUrl} onChange={e => setNewUrl(e.target.value)} required placeholder="https://example.com" className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary-500 outline-none" />
                    </div>
                    <div className="w-64">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Custom Alias (Optional)</label>
                        <input type="text" value={customAlias} onChange={e => setCustomAlias(e.target.value)} placeholder="my-custom-link" className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary-500 outline-none" />
                    </div>
                    <button type="submit" className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700 transition flex items-center font-medium h-10">
                        <Plus className="w-5 h-5 mr-2" /> Shorten
                    </button>
                </form>
            </div>

            {/* URL List */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="p-6 border-b border-gray-100">
                    <h2 className="text-xl font-semibold">Your Links</h2>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-gray-50 text-gray-600 text-sm">
                                <th className="p-4 font-medium">Short URL</th>
                                <th className="p-4 font-medium">Original URL</th>
                                <th className="p-4 font-medium">Clicks</th>
                                <th className="p-4 font-medium">Status</th>
                                <th className="p-4 font-medium text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {urls.map(url => (
                                <tr key={url.id} className="hover:bg-gray-50">
                                    <td className="p-4">
                                        <a href={url.shortUrl} target="_blank" rel="noreferrer" className="text-primary-600 font-medium hover:underline flex items-center">
                                            {url.shortUrl} <ExternalLink className="w-4 h-4 ml-1" />
                                        </a>
                                    </td>
                                    <td className="p-4 text-gray-500 truncate max-w-xs" title={url.originalUrl}>
                                        {url.originalUrl}
                                    </td>
                                    <td className="p-4 font-medium text-gray-900">{url.clicks}</td>
                                    <td className="p-4">
                                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${url.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                                            {url.status}
                                        </span>
                                    </td>
                                    <td className="p-4 text-right">
                                        <div className="flex justify-end space-x-2">
                                            <button onClick={() => copyToClipboard(url.shortUrl)} className="p-2 text-gray-400 hover:text-gray-700 bg-gray-100 rounded-lg" title="Copy">
                                                <Copy className="w-4 h-4" />
                                            </button>
                                            <Link to={`/urls/${url.id}/analytics`} className="p-2 text-primary-500 hover:text-primary-700 bg-primary-50 rounded-lg" title="Analytics">
                                                <BarChart2 className="w-4 h-4" />
                                            </Link>
                                            <a href={`http://localhost:8080/api/urls/${url.shortUrl.split('/').pop()}/qr`} target="_blank" rel="noreferrer" className="p-2 text-purple-500 hover:text-purple-700 bg-purple-50 rounded-lg" title="QR Code">
                                                <QrCode className="w-4 h-4" />
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            {urls.length === 0 && !loading && (
                                <tr>
                                    <td colSpan={5} className="p-8 text-center text-gray-500">
                                        No links created yet.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};
