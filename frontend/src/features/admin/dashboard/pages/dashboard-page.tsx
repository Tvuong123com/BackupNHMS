import { useState } from "react";
import {
  Users,
  Building2,
  ShieldAlert,
  ListChecks,
  Clock,
  TrendingUp,
  Activity,
  ArrowUpRight,
  UserCheck,
  Bed,
  CheckCircle2,
  AlertTriangle,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Link } from "react-router";
import { AiRiskAlertWidget } from "@/features/admin/ai/components/ai-risk-alert-widget";

const DashboardPage = () => {
  const [timeRange] = useState("Today");

  // Sample analytical metrics
  const stats = [
    {
      title: "Total Residents",
      value: "148",
      change: "+4 this week",
      trend: "up",
      icon: Users,
      color: "bg-blue-500/10 text-blue-600",
      link: "/admin/residents",
    },
    {
      title: "Occupied / Total Beds",
      value: "148 / 180",
      change: "82.2% Occupancy",
      trend: "up",
      icon: Bed,
      color: "bg-emerald-500/10 text-emerald-600",
      link: "/admin/facilities",
    },
    {
      title: "Active Incidents",
      value: "3",
      change: "1 Urgent SLA",
      trend: "down",
      icon: ShieldAlert,
      color: "bg-amber-500/10 text-amber-600",
      link: "/admin/incidents",
    },
    {
      title: "Care Tasks Today",
      value: "216",
      change: "89% Completed",
      trend: "up",
      icon: ListChecks,
      color: "bg-purple-500/10 text-purple-600",
      link: "/admin/care-tasks",
    },
  ];

  const recentIncidents = [
    { id: 1, title: "Resident Fall - Room 204", time: "10 mins ago", severity: "HIGH", status: "OPEN" },
    { id: 2, title: "Medication Delay - Wing B", time: "45 mins ago", severity: "MEDIUM", status: "IN_PROGRESS" },
    { id: 3, title: "Equipment Alert - Oxygen Tank #12", time: "2 hours ago", severity: "LOW", status: "RESOLVED" },
  ];

  const pendingApprovals = [
    { id: "CP-1024", resident: "Eleanor Vance", type: "Care Plan Initial Review", date: "2026-07-31" },
    { id: "PRE-884", resident: "Robert Paulson", type: "Pre-Admission Screening", date: "2026-07-31" },
    { id: "LOC-302", resident: "Margaret Thatcher", type: "Care Level Transition", date: "2026-07-30" },
  ];

  return (
    <div className="space-y-8 pb-10">
      {/* Header Banner */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between rounded-2xl bg-gradient-to-r from-blue-900 via-indigo-900 to-slate-900 p-8 text-white shadow-xl">
        <div className="space-y-2">
          <div className="inline-flex items-center gap-2 rounded-full bg-blue-500/20 px-3 py-1 text-xs font-semibold text-blue-200 backdrop-blur-md">
            <Activity className="h-3.5 w-3.5" /> ElderCare Management Suite
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
            Welcome back, System Administrator
          </h1>
          <p className="text-blue-200/80 text-sm max-w-xl">
            Here is what is happening across your facilities today. All critical telemetry systems are functioning normally.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/admin/residents">
            <Button variant="secondary" className="font-semibold shadow-md">
              <UserCheck className="mr-2 h-4 w-4" /> View Residents
            </Button>
          </Link>
          <Link to="/admin/incidents">
            <Button className="bg-blue-600 hover:bg-blue-500 text-white font-semibold shadow-md">
              <ShieldAlert className="mr-2 h-4 w-4" /> Incident Center
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat, i) => {
          const Icon = stat.icon;
          return (
            <Card key={i} className="hover:shadow-md transition-shadow border-gray-100">
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-gray-500">
                  {stat.title}
                </CardTitle>
                <div className={`p-2.5 rounded-xl ${stat.color}`}>
                  <Icon className="h-5 w-5" />
                </div>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900">{stat.value}</div>
                <div className="flex items-center justify-between mt-2">
                  <span className="text-xs font-medium text-emerald-600 flex items-center">
                    <TrendingUp className="mr-1 h-3 w-3" /> {stat.change}
                  </span>
                  <Link to={stat.link} className="text-xs text-blue-600 hover:underline flex items-center">
                    Details <ArrowUpRight className="ml-0.5 h-3 w-3" />
                  </Link>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* AI Predictive Risk Alerts */}
      <AiRiskAlertWidget />

      {/* Main Content Grid */}
      <div className="grid gap-8 lg:grid-cols-3">
        {/* Left Column: Recent Incidents & Approvals (2 cols) */}
        <div className="lg:col-span-2 space-y-8">
          {/* Active Incidents Feed */}
          <Card className="border-gray-100 shadow-sm">
            <CardHeader className="flex flex-row items-center justify-between">
              <div>
                <CardTitle className="text-lg font-bold text-gray-900">Recent Incidents & SLAs</CardTitle>
                <p className="text-xs text-gray-500 mt-1">Real-time alerts requiring staff response</p>
              </div>
              <Link to="/admin/incidents">
                <Button variant="outline" size="sm">View All</Button>
              </Link>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentIncidents.map((item) => (
                  <div key={item.id} className="flex items-center justify-between p-4 rounded-xl bg-gray-50 border border-gray-100 hover:bg-blue-50/50 transition-colors">
                    <div className="flex items-center gap-4">
                      <div className={`p-2 rounded-lg ${item.severity === 'HIGH' ? 'bg-red-100 text-red-700' : item.severity === 'MEDIUM' ? 'bg-amber-100 text-amber-700' : 'bg-blue-100 text-blue-700'}`}>
                        <AlertTriangle className="h-5 w-5" />
                      </div>
                      <div>
                        <h4 className="font-semibold text-gray-900 text-sm">{item.title}</h4>
                        <span className="text-xs text-gray-500 flex items-center gap-1 mt-0.5">
                          <Clock className="h-3 w-3" /> {item.time}
                        </span>
                      </div>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${item.status === 'OPEN' ? 'bg-red-100 text-red-800' : item.status === 'IN_PROGRESS' ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'}`}>
                      {item.status}
                    </span>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Pending Reviews & Approvals */}
          <Card className="border-gray-100 shadow-sm">
            <CardHeader className="flex flex-row items-center justify-between">
              <div>
                <CardTitle className="text-lg font-bold text-gray-900">Pending Reviews & Approvals</CardTitle>
                <p className="text-xs text-gray-500 mt-1">Intake screenings, care plan approvals, and care level transitions</p>
              </div>
              <Link to="/admin/care-plans">
                <Button variant="outline" size="sm">Care Plans</Button>
              </Link>
            </CardHeader>
            <CardContent>
              <div className="divide-y divide-gray-100">
                {pendingApprovals.map((item) => (
                  <div key={item.id} className="py-3.5 flex items-center justify-between">
                    <div>
                      <span className="text-xs font-mono font-semibold text-blue-600 bg-blue-50 px-2 py-0.5 rounded mr-2">{item.id}</span>
                      <span className="font-medium text-gray-900 text-sm">{item.resident}</span>
                      <span className="text-xs text-gray-500 ml-2">({item.type})</span>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-xs text-gray-400">{item.date}</span>
                      <Button size="sm" variant="ghost" className="text-blue-600 hover:text-blue-700">Review</Button>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column: Quick Links & Facility Capacity */}
        <div className="space-y-8">
          {/* Facility Operations Card */}
          <Card className="border-gray-100 shadow-sm bg-gradient-to-br from-white to-gray-50/50">
            <CardHeader>
              <CardTitle className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <Building2 className="h-5 w-5 text-blue-600" /> Facility Capacity
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <div className="flex justify-between text-sm font-medium">
                  <span className="text-gray-600">Assisted Living Wing A</span>
                  <span className="text-gray-900 font-bold">42 / 45 Beds</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div className="bg-blue-600 h-2.5 rounded-full" style={{ width: "93%" }}></div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between text-sm font-medium">
                  <span className="text-gray-600">Memory Care Wing B</span>
                  <span className="text-gray-900 font-bold">36 / 40 Beds</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div className="bg-emerald-600 h-2.5 rounded-full" style={{ width: "90%" }}></div>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between text-sm font-medium">
                  <span className="text-gray-600">Skilled Nursing Wing C</span>
                  <span className="text-gray-900 font-bold">70 / 95 Beds</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div className="bg-indigo-600 h-2.5 rounded-full" style={{ width: "73%" }}></div>
                </div>
              </div>

              <div className="pt-4 border-t border-gray-100 flex justify-between items-center text-xs text-gray-500">
                <span>Total Available Beds: <strong className="text-emerald-600">32 Beds</strong></span>
                <Link to="/admin/facilities" className="text-blue-600 font-semibold hover:underline">Manage Beds</Link>
              </div>
            </CardContent>
          </Card>

          {/* Quick Shortcuts */}
          <Card className="border-gray-100 shadow-sm">
            <CardHeader>
              <CardTitle className="text-lg font-bold text-gray-900">Admin Shortcuts</CardTitle>
            </CardHeader>
            <CardContent className="grid grid-cols-2 gap-3">
              <Link to="/admin/pre-admission" className="p-3 rounded-xl border border-gray-100 bg-gray-50/50 hover:bg-blue-50/50 hover:border-blue-200 transition-all text-center group">
                <span className="text-xs font-semibold text-gray-700 group-hover:text-blue-700 block">Pre-Admission</span>
              </Link>
              <Link to="/admin/assessment" className="p-3 rounded-xl border border-gray-100 bg-gray-50/50 hover:bg-blue-50/50 hover:border-blue-200 transition-all text-center group">
                <span className="text-xs font-semibold text-gray-700 group-hover:text-blue-700 block">Assessments</span>
              </Link>
              <Link to="/admin/staffing-ratios" className="p-3 rounded-xl border border-gray-100 bg-gray-50/50 hover:bg-blue-50/50 hover:border-blue-200 transition-all text-center group">
                <span className="text-xs font-semibold text-gray-700 group-hover:text-blue-700 block">Staffing Ratios</span>
              </Link>
              <Link to="/admin/audit-logs" className="p-3 rounded-xl border border-gray-100 bg-gray-50/50 hover:bg-blue-50/50 hover:border-blue-200 transition-all text-center group">
                <span className="text-xs font-semibold text-gray-700 group-hover:text-blue-700 block">Audit & PHI Logs</span>
              </Link>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;