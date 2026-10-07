import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../services/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell } from 'recharts';
import { ArrowLeft, Users, MousePointerClick, Calendar, TrendingUp } from 'lucide-react';

export const Analytics = () => {
    const { id } = useParams();
    const [data, setData] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchAnalytics = async () => {
            try {
                const res = await api.get(`/urls/${id}/analytics`);
                setData(res.data.data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchAnalytics();
    }, [id]);

    if (loading) return <div className="p-8">Loading analytics...</div>;
    if (!data) return <div className="p-8 text-red-500">Failed to load analytics.</div>;

    const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

    const formatChartData = (mapData: any) => {
        return Object.entries(mapData || {}).map(([name, value]) => ({ name, value }));
    };

    const timeData = formatChartData(data.clicksOverTime);
    const browserData = formatChartData(data.browsers);
    const osData = formatChartData(data.operatingSystems);

    return (
        <div className="p-8">
            <Link to="/dashboard" className="flex items-center text-gray-500 hover:text-gray-900 mb-6">
                <ArrowLeft className="w-4 h-4 mr-2" /> Back to Dashboard
            </Link>
            
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900">Analytics Overview</h1>
                <p className="text-gray-500 mt-1">Detailed statistics for your shortened link</p>
            </div>

            {/* Overview Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                    <div className="bg-blue-100 p-3 rounded-lg mr-4"><MousePointerClick className="w-6 h-6 text-blue-600" /></div>
                    <div>
                        <p className="text-sm text-gray-500 font-medium">Total Clicks</p>
                        <h3 className="text-2xl font-bold text-gray-900">{data.overview.totalClicks}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                    <div className="bg-green-100 p-3 rounded-lg mr-4"><Users className="w-6 h-6 text-green-600" /></div>
                    <div>
                        <p className="text-sm text-gray-500 font-medium">Unique Visitors</p>
                        <h3 className="text-2xl font-bold text-gray-900">{data.overview.uniqueVisitors}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                    <div className="bg-orange-100 p-3 rounded-lg mr-4"><Calendar className="w-6 h-6 text-orange-600" /></div>
                    <div>
                        <p className="text-sm text-gray-500 font-medium">Today's Clicks</p>
                        <h3 className="text-2xl font-bold text-gray-900">{data.overview.todayClicks}</h3>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center">
                    <div className="bg-purple-100 p-3 rounded-lg mr-4"><TrendingUp className="w-6 h-6 text-purple-600" /></div>
                    <div>
                        <p className="text-sm text-gray-500 font-medium">Last 7 Days</p>
                        <h3 className="text-2xl font-bold text-gray-900">{data.overview.last7DaysClicks}</h3>
                    </div>
                </div>
            </div>

            {/* Charts */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <h3 className="text-lg font-semibold mb-6">Clicks Over Time (30 Days)</h3>
                    <div className="h-72">
                        <ResponsiveContainer width="100%" height="100%">
                            <LineChart data={timeData}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{fill: '#6b7280'}} />
                                <YAxis axisLine={false} tickLine={false} tick={{fill: '#6b7280'}} />
                                <RechartsTooltip />
                                <Line type="monotone" dataKey="value" stroke="#3b82f6" strokeWidth={3} dot={{r: 4, strokeWidth: 2}} activeDot={{r: 6}} />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <h3 className="text-lg font-semibold mb-6">Browsers</h3>
                    <div className="h-72">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={browserData} layout="vertical">
                                <CartesianGrid strokeDasharray="3 3" horizontal={false} />
                                <XAxis type="number" axisLine={false} tickLine={false} tick={{fill: '#6b7280'}} />
                                <YAxis dataKey="name" type="category" width={100} axisLine={false} tickLine={false} tick={{fill: '#4b5563', fontSize: 14}} />
                                <RechartsTooltip cursor={{fill: '#f3f4f6'}} />
                                <Bar dataKey="value" fill="#10b981" radius={[0, 4, 4, 0]} barSize={24} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>
                
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <h3 className="text-lg font-semibold mb-6">Operating Systems</h3>
                    <div className="h-72 flex justify-center items-center">
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie data={osData} cx="50%" cy="50%" innerRadius={60} outerRadius={100} paddingAngle={5} dataKey="value" label>
                                    {osData.map((_: any, index: number) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                    ))}
                                </Pie>
                                <RechartsTooltip />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>
                
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <h3 className="text-lg font-semibold mb-6">Referrers</h3>
                    <div className="overflow-y-auto max-h-72">
                        <table className="w-full text-left">
                            <tbody>
                                {Object.entries(data.referrers || {}).map(([referrer, count]: any) => (
                                    <tr key={referrer} className="border-b border-gray-50 last:border-0">
                                        <td className="py-3 text-gray-700">{referrer}</td>
                                        <td className="py-3 text-right font-medium text-gray-900">{count}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};
