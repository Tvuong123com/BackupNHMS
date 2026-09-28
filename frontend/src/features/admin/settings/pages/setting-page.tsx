import { useState } from "react";
import { toast } from "sonner";
import { 
  Building, 
  ShieldAlert, 
  Sliders, 
  BellRing, 
  ShieldCheck, 
  Save, 
  Lock, 
  KeyRound, 
  Clock, 
  FileSpreadsheet, 
  CheckCircle2,
  RefreshCw,
  Mail,
  Phone,
  MapPin,
  Flame,
} from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { PermissionTab } from "../tabs/permission-tab";
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const SettingPage = () => {
  const [isSaving, setIsSaving] = useState(false);

  // General Facility Profile State
  const [facilityProfile, setFacilityProfile] = useState({
    facilityName: "ElderCare Senior Living & Health Center",
    address: "123 Healthcare Ave, Medical District, NY 10001",
    phone: "(555) 234-5678",
    emergencyContact: "(555) 911-0000",
    adminEmail: "admin@eldercare.com",
    timezone: "America/New_York (EST)",
  });

  // Security & Compliance State
  const [securitySettings, setSecuritySettings] = useState({
    phiLogging: true,
    mfaEnforcement: true,
    passwordComplexity: true,
    sessionTimeout: "15",
    auditRetentionDays: "365",
  });

  // Operational Rules State
  const [operationalRules, setOperationalRules] = useState({
    reassessmentCycleDays: "90",
    emergencySlaMins: "15",
    autoArchiveDischargedDays: "30",
    allowCareLevelOverride: true,
  });

  // Notifications State
  const [notifications, setNotifications] = useState({
    emailCriticalIncidents: true,
    smsEmergencyAlerts: true,
    dailyDigestEmail: false,
    auditAlertThreshold: "5",
  });

  const handleSaveSettings = (sectionName: string) => {
    setIsSaving(true);
    setTimeout(() => {
      setIsSaving(false);
      toast.success(`${sectionName} saved successfully!`, {
        description: "Your system configuration preferences have been updated.",
        icon: <CheckCircle2 className="h-5 w-5 text-emerald-600" />,
      });
    }, 600);
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-indigo-950 p-8 rounded-2xl text-white shadow-md flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold backdrop-blur-md mb-2">
            <Sliders className="h-3.5 w-3.5" /> System Control Console
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">System Settings & Governance</h1>
          <p className="text-slate-300 text-sm mt-1 max-w-2xl">
            Manage global facility profiles, security parameters, HIPAA compliance policies, operational SLAs, and system permission matrix.
          </p>
        </div>

        <Badge variant="outline" className="border-blue-400/40 text-blue-200 bg-blue-500/10 px-3 py-1.5 text-xs font-mono">
          System Ver: v2.4.0-PROD
        </Badge>
      </div>

      {/* Settings Navigation Tabs */}
      <Tabs defaultValue="facility" className="w-full space-y-6">
        <TabsList variant="line" className="w-full flex flex-wrap items-center justify-start gap-2 bg-transparent border-b border-slate-200 rounded-none h-11 p-0">
          <TabsTrigger
            value="facility"
            className="flex items-center gap-2 pb-3 pt-2 px-4 font-semibold text-sm text-slate-500 hover:text-slate-800 bg-transparent data-[state=active]:text-blue-600 data-active:text-blue-600 data-[state=active]:after:bg-blue-600 data-active:after:bg-blue-600 rounded-none shadow-none cursor-pointer"
          >
            <Building className="h-4 w-4 mb-0.5" /> Facility Profile
          </TabsTrigger>
          <TabsTrigger
            value="security"
            className="flex items-center gap-2 pb-3 pt-2 px-4 font-semibold text-sm text-slate-500 hover:text-slate-800 bg-transparent data-[state=active]:text-purple-600 data-active:text-purple-600 data-[state=active]:after:bg-purple-600 data-active:after:bg-purple-600 rounded-none shadow-none cursor-pointer"
          >
            <ShieldAlert className="h-4 w-4 mb-0.5" /> Security & HIPAA
          </TabsTrigger>
          <TabsTrigger
            value="operations"
            className="flex items-center gap-2 pb-3 pt-2 px-4 font-semibold text-sm text-slate-500 hover:text-slate-800 bg-transparent data-[state=active]:text-emerald-600 data-active:text-emerald-600 data-[state=active]:after:bg-emerald-600 data-active:after:bg-emerald-600 rounded-none shadow-none cursor-pointer"
          >
            <Clock className="h-4 w-4 mb-0.5" /> Operational Rules
          </TabsTrigger>
          <TabsTrigger
            value="notifications"
            className="flex items-center gap-2 pb-3 pt-2 px-4 font-semibold text-sm text-slate-500 hover:text-slate-800 bg-transparent data-[state=active]:text-amber-600 data-active:text-amber-600 data-[state=active]:after:bg-amber-600 data-active:after:bg-amber-600 rounded-none shadow-none cursor-pointer"
          >
            <BellRing className="h-4 w-4 mb-0.5" /> Alerts & Notifications
          </TabsTrigger>
          <TabsTrigger
            value="permissions"
            className="flex items-center gap-2 pb-3 pt-2 px-4 font-semibold text-sm text-slate-500 hover:text-slate-800 bg-transparent data-[state=active]:text-indigo-600 data-active:text-indigo-600 data-[state=active]:after:bg-indigo-600 data-active:after:bg-indigo-600 rounded-none shadow-none cursor-pointer"
          >
            <ShieldCheck className="h-4 w-4 mb-0.5" /> Permission Matrix
          </TabsTrigger>
        </TabsList>

        {/* 1. Facility Profile Tab */}
        <TabsContent value="facility" className="space-y-6">
          <Card className="border-gray-100 shadow-sm">
            <CardHeader className="border-b border-gray-100 bg-gray-50/50">
              <CardTitle className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <Building className="h-5 w-5 text-blue-600" /> Facility Identity & Contact Info
              </CardTitle>
              <CardDescription>Master organizational details used across official resident documents, invoices, and care plans</CardDescription>
            </CardHeader>
            <CardContent className="p-6 space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="facilityName" className="font-semibold text-xs text-gray-700">Facility Master Name</Label>
                  <Input
                    id="facilityName"
                    value={facilityProfile.facilityName}
                    onChange={(e) => setFacilityProfile({ ...facilityProfile, facilityName: e.target.value })}
                    className="font-medium"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="adminEmail" className="font-semibold text-xs text-gray-700">System Admin Email</Label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                    <Input
                      id="adminEmail"
                      className="pl-9 font-medium"
                      value={facilityProfile.adminEmail}
                      onChange={(e) => setFacilityProfile({ ...facilityProfile, adminEmail: e.target.value })}
                    />
                  </div>
                </div>

                <div className="space-y-2 md:col-span-2">
                  <Label htmlFor="address" className="font-semibold text-xs text-gray-700">Physical Address</Label>
                  <div className="relative">
                    <MapPin className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                    <Input
                      id="address"
                      className="pl-9 font-medium"
                      value={facilityProfile.address}
                      onChange={(e) => setFacilityProfile({ ...facilityProfile, address: e.target.value })}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="phone" className="font-semibold text-xs text-gray-700">Primary Telephone</Label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                    <Input
                      id="phone"
                      className="pl-9 font-medium"
                      value={facilityProfile.phone}
                      onChange={(e) => setFacilityProfile({ ...facilityProfile, phone: e.target.value })}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="emergencyContact" className="font-semibold text-xs text-rose-700 flex items-center gap-1">
                    <Flame className="h-3.5 w-3.5 text-rose-600" /> Emergency Hotline
                  </Label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-2.5 h-4 w-4 text-rose-500" />
                    <Input
                      id="emergencyContact"
                      className="pl-9 font-bold text-rose-700 border-rose-200 bg-rose-50/30"
                      value={facilityProfile.emergencyContact}
                      onChange={(e) => setFacilityProfile({ ...facilityProfile, emergencyContact: e.target.value })}
                    />
                  </div>
                </div>
              </div>
            </CardContent>
            <CardFooter className="bg-gray-50/50 border-t border-gray-100 flex justify-end py-4 px-6">
              <Button onClick={() => handleSaveSettings("Facility Profile")} disabled={isSaving} className="bg-blue-600 hover:bg-blue-700 font-semibold">
                {isSaving ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                Save Facility Profile
              </Button>
            </CardFooter>
          </Card>
        </TabsContent>

        {/* 2. Security & HIPAA Tab */}
        <TabsContent value="security" className="space-y-6">
          <Card className="border-gray-100 shadow-sm">
            <CardHeader className="border-b border-gray-100 bg-gray-50/50">
              <CardTitle className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <ShieldAlert className="h-5 w-5 text-purple-600" /> Security Policies & HIPAA Compliance Controls
              </CardTitle>
              <CardDescription>Enforce strict access controls, session lifecycle, and PHI audit trails for healthcare regulatory compliance</CardDescription>
            </CardHeader>
            <CardContent className="p-6 space-y-6">
              <div className="space-y-4">
                <div className="p-4 rounded-xl bg-purple-50/40 border border-purple-100 flex items-center justify-between">
                  <div className="space-y-0.5">
                    <div className="flex items-center gap-2">
                      <h4 className="font-bold text-gray-900 text-sm">HIPAA PHI Access Trail Audit Logging</h4>
                      <Badge className="bg-purple-600 text-white text-[10px]">MANDATORY</Badge>
                    </div>
                    <p className="text-xs text-gray-500">Record all resident health record views, medical history exports, and sensitive field accesses</p>
                  </div>
                  <Switch
                    checked={securitySettings.phiLogging}
                    onCheckedChange={(val) => setSecuritySettings({ ...securitySettings, phiLogging: val })}
                  />
                </div>

                <div className="p-4 rounded-xl bg-gray-50 border border-gray-100 flex items-center justify-between">
                  <div className="space-y-0.5">
                    <h4 className="font-bold text-gray-900 text-sm">Enforce Two-Factor Authentication (2FA / MFA)</h4>
                    <p className="text-xs text-gray-500">Require all Admin, DON, and Nurse roles to verify login via TOTP authenticator app</p>
                  </div>
                  <Switch
                    checked={securitySettings.mfaEnforcement}
                    onCheckedChange={(val) => setSecuritySettings({ ...securitySettings, mfaEnforcement: val })}
                  />
                </div>

                <div className="p-4 rounded-xl bg-gray-50 border border-gray-100 flex items-center justify-between">
                  <div className="space-y-0.5">
                    <h4 className="font-bold text-gray-900 text-sm">Strict Password Complexity Standard</h4>
                    <p className="text-xs text-gray-500">Require minimum 12 characters with uppercase, numbers, and special characters</p>
                  </div>
                  <Switch
                    checked={securitySettings.passwordComplexity}
                    onCheckedChange={(val) => setSecuritySettings({ ...securitySettings, passwordComplexity: val })}
                  />
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 pt-2">
                  <div className="space-y-2">
                    <Label className="font-semibold text-xs text-gray-700">Idle Session Timeout</Label>
                    <Select
                      value={securitySettings.sessionTimeout}
                      onValueChange={(val) => setSecuritySettings({ ...securitySettings, sessionTimeout: val })}
                    >
                      <SelectTrigger className="font-semibold">
                        <SelectValue placeholder="Select timeout" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="15">15 Minutes (Recommended for HIPAA)</SelectItem>
                        <SelectItem value="30">30 Minutes</SelectItem>
                        <SelectItem value="60">60 Minutes</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label className="font-semibold text-xs text-gray-700">Audit Log Data Retention Period</Label>
                    <Select
                      value={securitySettings.auditRetentionDays}
                      onValueChange={(val) => setSecuritySettings({ ...securitySettings, auditRetentionDays: val })}
                    >
                      <SelectTrigger className="font-semibold">
                        <SelectValue placeholder="Select retention" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="180">180 Days</SelectItem>
                        <SelectItem value="365">1 Year (365 Days)</SelectItem>
                        <SelectItem value="730">2 Years (730 Days)</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </div>
            </CardContent>
            <CardFooter className="bg-gray-50/50 border-t border-gray-100 flex justify-end py-4 px-6">
              <Button onClick={() => handleSaveSettings("Security & HIPAA Settings")} disabled={isSaving} className="bg-purple-600 hover:bg-purple-700 font-semibold">
                {isSaving ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                Save Security Policies
              </Button>
            </CardFooter>
          </Card>
        </TabsContent>

        {/* 3. Operational Rules Tab */}
        <TabsContent value="operations" className="space-y-6">
          <Card className="border-gray-100 shadow-sm">
            <CardHeader className="border-b border-gray-100 bg-gray-50/50">
              <CardTitle className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <Clock className="h-5 w-5 text-emerald-600" /> Care Operations & Response SLAs
              </CardTitle>
              <CardDescription>Configure assessment recalculation cycles and incident escalation SLA windows</CardDescription>
            </CardHeader>
            <CardContent className="p-6 space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label className="font-semibold text-xs text-gray-700">ADL Re-assessment Mandatory Cycle</Label>
                  <Select
                    value={operationalRules.reassessmentCycleDays}
                    onValueChange={(val) => setOperationalRules({ ...operationalRules, reassessmentCycleDays: val })}
                  >
                    <SelectTrigger className="font-semibold">
                      <SelectValue placeholder="Select cycle" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="30">Every 30 Days (Monthly)</SelectItem>
                      <SelectItem value="90">Every 90 Days (Quarterly Standard)</SelectItem>
                      <SelectItem value="180">Every 180 Days (Semi-Annual)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label className="font-semibold text-xs text-gray-700">Emergency Incident Resolution SLA Window</Label>
                  <Select
                    value={operationalRules.emergencySlaMins}
                    onValueChange={(val) => setOperationalRules({ ...operationalRules, emergencySlaMins: val })}
                  >
                    <SelectTrigger className="font-semibold">
                      <SelectValue placeholder="Select SLA" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="15">15 Minutes (High Priority Emergency)</SelectItem>
                      <SelectItem value="30">30 Minutes</SelectItem>
                      <SelectItem value="60">60 Minutes</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="p-4 rounded-xl bg-gray-50 border border-gray-100 md:col-span-2 flex items-center justify-between">
                  <div className="space-y-0.5">
                    <h4 className="font-bold text-gray-900 text-sm">Allow Care Level Manual Override by DON</h4>
                    <p className="text-xs text-gray-500">Permit Director of Nursing to override calculated ADL Care Level with written clinical justification</p>
                  </div>
                  <Switch
                    checked={operationalRules.allowCareLevelOverride}
                    onCheckedChange={(val) => setOperationalRules({ ...operationalRules, allowCareLevelOverride: val })}
                  />
                </div>
              </div>
            </CardContent>
            <CardFooter className="bg-gray-50/50 border-t border-gray-100 flex justify-end py-4 px-6">
              <Button onClick={() => handleSaveSettings("Operational Rules")} disabled={isSaving} className="bg-emerald-600 hover:bg-emerald-700 font-semibold">
                {isSaving ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                Save Operational Rules
              </Button>
            </CardFooter>
          </Card>
        </TabsContent>

        {/* 4. Alerts & Notifications Tab */}
        <TabsContent value="notifications" className="space-y-6">
          <Card className="border-gray-100 shadow-sm">
            <CardHeader className="border-b border-gray-100 bg-gray-50/50">
              <CardTitle className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <BellRing className="h-5 w-5 text-amber-600" /> Automated System Alert Dispatch Settings
              </CardTitle>
              <CardDescription>Manage real-time notifications for critical incidents, fall alerts, and daily care plan digests</CardDescription>
            </CardHeader>
            <CardContent className="p-6 space-y-4">
              <div className="p-4 rounded-xl bg-amber-50/40 border border-amber-100 flex items-center justify-between">
                <div className="space-y-0.5">
                  <h4 className="font-bold text-gray-900 text-sm">Email Alerts for Critical Incidents & Medical Emergencies</h4>
                  <p className="text-xs text-gray-500">Instantly notify DON, Medical Director, and duty nurses when a high-severity incident is logged</p>
                </div>
                <Switch
                  checked={notifications.emailCriticalIncidents}
                  onCheckedChange={(val) => setNotifications({ ...notifications, emailCriticalIncidents: val })}
                />
              </div>

              <div className="p-4 rounded-xl bg-gray-50 border border-gray-100 flex items-center justify-between">
                <div className="space-y-0.5">
                  <h4 className="font-bold text-gray-900 text-sm">SMS Emergency Alerts for On-Call Personnel</h4>
                  <p className="text-xs text-gray-500">Dispatch SMS notifications to assigned emergency contacts for code red events</p>
                </div>
                <Switch
                  checked={notifications.smsEmergencyAlerts}
                  onCheckedChange={(val) => setNotifications({ ...notifications, smsEmergencyAlerts: val })}
                />
              </div>

              <div className="p-4 rounded-xl bg-gray-50 border border-gray-100 flex items-center justify-between">
                <div className="space-y-0.5">
                  <h4 className="font-bold text-gray-900 text-sm">Daily CNA Task Completion Digest Email</h4>
                  <p className="text-xs text-gray-500">Send end-of-shift daily task completion summaries to nursing supervisors</p>
                </div>
                <Switch
                  checked={notifications.dailyDigestEmail}
                  onCheckedChange={(val) => setNotifications({ ...notifications, dailyDigestEmail: val })}
                />
              </div>
            </CardContent>
            <CardFooter className="bg-gray-50/50 border-t border-gray-100 flex justify-end py-4 px-6">
              <Button onClick={() => handleSaveSettings("Alert Settings")} disabled={isSaving} className="bg-amber-600 hover:bg-amber-700 font-semibold">
                {isSaving ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                Save Alert Settings
              </Button>
            </CardFooter>
          </Card>
        </TabsContent>

        {/* 5. Permission Matrix Tab */}
        <TabsContent value="permissions" className="mt-6">
          <PermissionTab />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default SettingPage;
