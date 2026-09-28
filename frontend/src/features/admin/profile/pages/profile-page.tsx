import { useState } from "react";
import {
  UserCircle,
  Mail,
  Phone,
  ShieldCheck,
  KeyRound,
  Save,
  Building,
  Calendar,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { useCurrentUser } from "@/features/auth/hooks/use-current-user";

const ProfilePage = () => {
  const { user } = useCurrentUser();
  const [firstName, setFirstName] = useState("Daniel");
  const [lastName, setLastName] = useState("Brooks");
  const [phone, setPhone] = useState("916-555-9999");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setTimeout(() => {
      setIsSubmitting(false);
      toast.success("Profile information updated successfully!");
    }, 600);
  };

  return (
    <div className="space-y-6 pb-12 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
          User Profile & Settings
        </h1>
        <p className="mt-1 text-sm text-gray-500">
          Manage your personal information, role details, and security credentials.
        </p>
      </div>

      {/* User Card Summary Header */}
      <Card className="border-gray-100 shadow-sm bg-gradient-to-r from-blue-900 to-indigo-900 text-white overflow-hidden">
        <CardContent className="p-8 flex flex-col sm:flex-row items-center gap-6">
          <div className="h-20 w-20 rounded-full bg-blue-500/20 border-2 border-blue-400/40 flex items-center justify-center text-3xl font-extrabold text-blue-200 shadow-inner">
            DB
          </div>
          <div className="space-y-1 text-center sm:text-left">
            <div className="flex items-center justify-center sm:justify-start gap-2">
              <h2 className="text-2xl font-bold">{firstName} {lastName}</h2>
              <Badge className="bg-blue-500/30 text-blue-200 hover:bg-blue-500/30 border border-blue-400/30 font-semibold">
                System Administrator
              </Badge>
            </div>
            <p className="text-sm text-blue-200/80 flex items-center justify-center sm:justify-start gap-2">
              <Mail className="h-4 w-4" /> {user?.sub || "daniel.brooks@nhms-demo.local"}
            </p>
            <div className="pt-2 flex items-center justify-center sm:justify-start gap-4 text-xs text-blue-300/70">
              <span className="flex items-center gap-1"><Building className="h-3.5 w-3.5" /> NHMS Central Headquarters</span>
              <span className="flex items-center gap-1"><Calendar className="h-3.5 w-3.5" /> Joined July 2026</span>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Profile Edit Form */}
      <div className="grid gap-6 md:grid-cols-3">
        <Card className="md:col-span-2 border-gray-100 shadow-sm">
          <CardHeader>
            <CardTitle className="text-lg font-bold text-gray-900">Personal Information</CardTitle>
            <CardDescription>Update your personal and contact details</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSaveProfile} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="firstName" className="text-xs font-semibold text-gray-700">First Name</Label>
                  <Input
                    id="firstName"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="lastName" className="text-xs font-semibold text-gray-700">Last Name</Label>
                  <Input
                    id="lastName"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="email" className="text-xs font-semibold text-gray-700">Email Address</Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                  <Input
                    id="email"
                    type="email"
                    value={user?.sub || "daniel.brooks@nhms-demo.local"}
                    disabled
                    className="pl-9 bg-gray-50 text-gray-500 cursor-not-allowed"
                  />
                </div>
                <p className="text-[11px] text-gray-400">Email address cannot be changed for administrative accounts.</p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="phone" className="text-xs font-semibold text-gray-700">Phone Number</Label>
                <div className="relative">
                  <Phone className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                  <Input
                    id="phone"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    className="pl-9"
                  />
                </div>
              </div>

              <div className="pt-4 flex justify-end">
                <Button type="submit" disabled={isSubmitting} className="bg-blue-600 hover:bg-blue-700 font-semibold">
                  <Save className="mr-2 h-4 w-4" /> Save Changes
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>

        {/* Security & Access Info */}
        <Card className="border-gray-100 shadow-sm">
          <CardHeader>
            <CardTitle className="text-lg font-bold text-gray-900">Security & Roles</CardTitle>
            <CardDescription>System access permissions</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="p-3 rounded-lg bg-gray-50 border border-gray-100">
              <span className="text-xs font-semibold text-gray-500 uppercase block mb-1">Primary Role</span>
              <span className="text-sm font-bold text-gray-900 flex items-center gap-1.5">
                <ShieldCheck className="h-4 w-4 text-blue-600" /> System_Administrator
              </span>
            </div>

            <div className="p-3 rounded-lg bg-gray-50 border border-gray-100">
              <span className="text-xs font-semibold text-gray-500 uppercase block mb-1">MFA Authentication</span>
              <span className="text-xs font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded inline-block">
                Active / Enabled
              </span>
            </div>

            <div className="pt-2">
              <Button variant="outline" className="w-full text-xs font-semibold">
                <KeyRound className="mr-2 h-4 w-4" /> Change Password
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default ProfilePage;
