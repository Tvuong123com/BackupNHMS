import { useState } from "react";
import {
  Bell,
  CheckCircle2,
  AlertTriangle,
  Info,
  ShieldAlert,
  Search,
  Filter,
  CheckCheck,
  Trash2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

interface NotificationItem {
  id: string;
  title: string;
  message: string;
  timestamp: string;
  type: "ALERT" | "WARNING" | "INFO" | "REMINDER";
  read: boolean;
  module: string;
}

const mockNotifications: NotificationItem[] = [
  {
    id: "NOTIF-001",
    title: "Urgent Incident Reported",
    message: "Resident fall in Wing B Room 204. Immediate CNA assistance requested.",
    timestamp: "10 mins ago",
    type: "ALERT",
    read: false,
    module: "Incidents",
  },
  {
    id: "NOTIF-002",
    title: "Care Level Transition Pending Review",
    message: "Resident Eleanor Vance ADL score updated to 28 (Skilled Nursing).",
    timestamp: "1 hour ago",
    type: "WARNING",
    read: false,
    module: "Care Levels",
  },
  {
    id: "NOTIF-003",
    title: "Pre-Admission Screening Completed",
    message: "Pre-admission screening for Robert Paulson approved by Admission Staff.",
    timestamp: "3 hours ago",
    type: "INFO",
    read: true,
    module: "Intake",
  },
  {
    id: "NOTIF-004",
    title: "Staffing Ratio Compliance Reminder",
    message: "Night shift CNA ratio in Wing A is 1 CNA below target threshold.",
    timestamp: "5 hours ago",
    type: "WARNING",
    read: true,
    module: "Staffing",
  },
  {
    id: "NOTIF-005",
    title: "System Audit Log Export Complete",
    message: "HIPAA PHI Access Logs for July 2026 successfully generated.",
    timestamp: "1 day ago",
    type: "INFO",
    read: true,
    module: "Compliance",
  },
];

const NotificationPage = () => {
  const [notifications, setNotifications] = useState<NotificationItem[]>(mockNotifications);
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState<string>("ALL");

  const handleMarkAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const handleToggleRead = (id: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: !n.read } : n))
    );
  };

  const handleDelete = (id: string) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  const filtered = notifications.filter((n) => {
    const matchSearch =
      n.title.toLowerCase().includes(search.toLowerCase()) ||
      n.message.toLowerCase().includes(search.toLowerCase()) ||
      n.module.toLowerCase().includes(search.toLowerCase());
    const matchType = filterType === "ALL" || n.type === filterType;
    return matchSearch && matchType;
  });

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="space-y-6 pb-12">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
              System Notifications
            </h1>
            {unreadCount > 0 && (
              <Badge className="bg-red-500 hover:bg-red-600 text-white font-bold">
                {unreadCount} Unread
              </Badge>
            )}
          </div>
          <p className="mt-1 text-sm text-gray-500">
            Real-time system alerts, care plan updates, and telemetry notifications.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={handleMarkAllRead} disabled={unreadCount === 0}>
            <CheckCheck className="mr-2 h-4 w-4 text-emerald-600" /> Mark All as Read
          </Button>
        </div>
      </div>

      {/* Filter & Search Bar */}
      <Card className="border-gray-100 shadow-sm">
        <CardContent className="p-4 flex flex-col sm:flex-row gap-4 justify-between items-center">
          <div className="relative w-full sm:w-80">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <Input
              placeholder="Search notifications..."
              className="pl-9 text-sm"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="flex items-center gap-2 w-full sm:w-auto">
            <Filter className="h-4 w-4 text-gray-400" />
            <span className="text-xs font-semibold text-gray-500">Filter:</span>
            {["ALL", "ALERT", "WARNING", "INFO"].map((type) => (
              <Button
                key={type}
                variant={filterType === type ? "default" : "ghost"}
                size="sm"
                className="text-xs font-medium"
                onClick={() => setFilterType(type)}
              >
                {type}
              </Button>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Notifications List */}
      <Card className="border-gray-100 shadow-sm overflow-hidden">
        <CardContent className="p-0 divide-y divide-gray-100">
          {filtered.length === 0 ? (
            <div className="p-12 text-center text-gray-500 text-sm">
              <Bell className="mx-auto h-8 w-8 text-gray-300 mb-2" />
              No notifications matching your criteria.
            </div>
          ) : (
            filtered.map((n) => (
              <div
                key={n.id}
                className={`p-5 flex items-start justify-between gap-4 transition-colors ${
                  !n.read ? "bg-blue-50/40 font-medium" : "hover:bg-gray-50"
                }`}
              >
                <div className="flex items-start gap-4">
                  <div className="mt-1">
                    {n.type === "ALERT" && <ShieldAlert className="h-5 w-5 text-red-600" />}
                    {n.type === "WARNING" && <AlertTriangle className="h-5 w-5 text-amber-600" />}
                    {n.type === "INFO" && <Info className="h-5 w-5 text-blue-600" />}
                  </div>

                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <h4 className="text-sm font-semibold text-gray-900">{n.title}</h4>
                      <Badge variant="outline" className="text-[10px] uppercase font-mono">
                        {n.module}
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-600 leading-snug">{n.message}</p>
                    <span className="text-xs text-gray-400 block pt-1">{n.timestamp}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <Button
                    size="sm"
                    variant="ghost"
                    className="text-xs text-gray-500 hover:text-blue-600"
                    onClick={() => handleToggleRead(n.id)}
                  >
                    {n.read ? "Mark Unread" : "Mark Read"}
                  </Button>
                  <Button
                    size="icon"
                    variant="ghost"
                    className="text-gray-400 hover:text-red-600 h-8 w-8"
                    onClick={() => handleDelete(n.id)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            ))
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default NotificationPage;
