import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { 
  Building2, 
  Calendar, 
  DollarSign, 
  Edit3, 
  Info, 
  ShieldCheck,
  TrendingUp,
  Sparkles,
  Search,
  CheckCircle2,
} from "lucide-react";
import { careLevelApi } from "../services/care-level-api";
import { facilitiesApi } from "@/services/facilities-api";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogDescription,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

// Tier styling mapping
const getTierBadgeStyle = (code: string | number) => {
  const codeStr = String(code).toUpperCase();
  if (codeStr.includes("1") || codeStr.includes("LOW") || codeStr.includes("INDEPENDENT")) {
    return { badge: "bg-emerald-50 text-emerald-700 border-emerald-200", title: "Tier 1 – Low", range: "0 - 8 pts" };
  }
  if (codeStr.includes("2") || codeStr.includes("MODERATE") || codeStr.includes("ASSISTED")) {
    return { badge: "bg-blue-50 text-blue-700 border-blue-200", title: "Tier 2 – Moderate", range: "9 - 16 pts" };
  }
  if (codeStr.includes("3") || codeStr.includes("HIGH") || codeStr.includes("MEMORY")) {
    return { badge: "bg-purple-50 text-purple-700 border-purple-200", title: "Tier 3 – High", range: "17 - 24 pts" };
  }
  if (codeStr.includes("4") || codeStr.includes("TOTAL") || codeStr.includes("SKILLED")) {
    return { badge: "bg-amber-50 text-amber-800 border-amber-200", title: "Tier 4 – Total Care", range: "25 - 32 pts" };
  }
  return { badge: "bg-rose-50 text-rose-700 border-rose-200", title: "Tier 5 – Hospice", range: "33+ pts" };
};

export const LOCRatesPage = () => {
  const queryClient = useQueryClient();
  const [selectedFacilityId, setSelectedFacilityId] = useState<number>(1);
  const [editingRate, setEditingRate] = useState<{
    careLevelId: number;
    careLevelName: string;
    rateId?: number;
    dailyRate: number;
    effectiveFrom: string;
  } | null>(null);

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  // Queries
  const { data: careLevels = [], isLoading: isCareLevelsLoading } = useQuery({
    queryKey: ["careLevels"],
    queryFn: () => careLevelApi.getCareLevels(),
  });

  const { data: allRates = [], isLoading: isRatesLoading } = useQuery({
    queryKey: ["careLevelRates"],
    queryFn: () => careLevelApi.getCareLevelRates(),
  });

  const { data: facilitiesData, isLoading: isFacilitiesLoading } = useQuery({
    queryKey: ["facilities"],
    queryFn: () => facilitiesApi.getFacilities(0, 100),
  });

  const facilities = facilitiesData?.data || [];

  // Mutation for update
  const saveRateMutation = useMutation({
    mutationFn: (data: { careLevelId: number; facilityId: number; dailyRate: number; effectiveFrom: string }) =>
      careLevelApi.createCareLevelRate(data.careLevelId, {
        facilityId: data.facilityId,
        dailyRate: data.dailyRate,
        effectiveFrom: data.effectiveFrom,
      }),
    onSuccess: () => {
      toast.success("LOC Rate updated successfully");
      queryClient.invalidateQueries({ queryKey: ["careLevelRates"] });
      setIsEditModalOpen(false);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || "Failed to update LOC Rate");
    },
  });

  const handleEditClick = (careLevel: any) => {
    const facilityRates = allRates.filter(r => r.facility_id === selectedFacilityId && r.care_level_id === careLevel.id);
    const latestRate = facilityRates.sort((a, b) => new Date(b.effective_from).getTime() - new Date(a.effective_from).getTime())[0];

    setEditingRate({
      careLevelId: careLevel.id,
      careLevelName: careLevel.levelName || careLevel.level_name,
      rateId: latestRate?.id,
      dailyRate: latestRate?.daily_rate || 150.00,
      effectiveFrom: latestRate?.effective_from || new Date().toISOString().split("T")[0],
    });
    setIsEditModalOpen(true);
  };

  const handleSaveModal = () => {
    if (!editingRate) return;
    saveRateMutation.mutate({
      careLevelId: editingRate.careLevelId,
      facilityId: selectedFacilityId,
      dailyRate: Number(editingRate.dailyRate),
      effectiveFrom: editingRate.effectiveFrom,
    });
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-gradient-to-r from-slate-900 via-blue-950 to-indigo-950 p-8 rounded-2xl text-white shadow-lg">
        <div className="space-y-1.5">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold backdrop-blur-md">
            <Sparkles className="h-3.5 w-3.5" /> Level of Care Billing Matrix
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight">Level of Care (LOC) Rates</h1>
          <p className="text-blue-200/80 text-sm max-w-xl">
            Configure daily billing rates, ADL score threshold tiers, and effective date schedules across your facilities.
          </p>
        </div>

        {/* Facility Selector */}
        <div className="bg-white/10 backdrop-blur-md border border-white/20 p-3.5 rounded-xl flex items-center gap-3">
          <Building2 className="h-5 w-5 text-blue-300 shrink-0" />
          <div className="space-y-0.5">
            <span className="text-[11px] font-semibold uppercase text-blue-200 block">Active Facility</span>
            <Select
              value={String(selectedFacilityId)}
              onValueChange={(val) => setSelectedFacilityId(Number(val))}
            >
              <SelectTrigger className="bg-transparent border-none text-white font-bold text-sm h-7 p-0 shadow-none focus:ring-0">
                <SelectValue placeholder="Select Facility" />
              </SelectTrigger>
              <SelectContent className="bg-slate-900 border-slate-800 text-white">
                {facilities.length > 0 ? (
                  facilities.map((fac: any) => (
                    <SelectItem key={fac.id} value={String(fac.id)}>
                      {fac.facilityName || fac.name}
                    </SelectItem>
                  ))
                ) : (
                  <SelectItem value="1">Main Facility Campus</SelectItem>
                )}
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Main LOC Rate Table Card */}
      <Card className="border-gray-100 shadow-sm overflow-hidden">
        <CardHeader className="border-b border-gray-100 bg-gray-50/50 flex flex-row items-center justify-between py-5">
          <div>
            <CardTitle className="text-lg font-bold text-gray-900 flex items-center gap-2">
              <ShieldCheck className="h-5 w-5 text-blue-600" /> Care Level Rate Schedule
            </CardTitle>
            <p className="text-xs text-gray-500 mt-1">Rates are applied per resident based on ADL assessment classification</p>
          </div>
          <Badge variant="outline" className="font-mono text-xs">
            {careLevels.length} Configured Levels
          </Badge>
        </CardHeader>

        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-gray-50">
              <TableRow>
                <TableHead className="font-semibold text-gray-700 py-4 pl-6">Care Level / Tier</TableHead>
                <TableHead className="font-semibold text-gray-700 py-4">ADL Score Threshold</TableHead>
                <TableHead className="font-semibold text-gray-700 py-4">Daily Rate ($/day)</TableHead>
                <TableHead className="font-semibold text-gray-700 py-4">Effective From</TableHead>
                <TableHead className="font-semibold text-gray-700 py-4">Status</TableHead>
                <TableHead className="font-semibold text-gray-700 py-4 text-right pr-6">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody className="divide-y divide-gray-100">
              {careLevels.map((lvl: any) => {
                const tierInfo = getTierBadgeStyle(lvl.levelCode || lvl.id);
                const facilityRates = allRates.filter(r => r.facility_id === selectedFacilityId && r.care_level_id === lvl.id);
                const activeRate = facilityRates.sort((a, b) => new Date(b.effective_from).getTime() - new Date(a.effective_from).getTime())[0];

                return (
                  <TableRow key={lvl.id} className="hover:bg-blue-50/30 transition-colors">
                    <TableCell className="py-5 pl-6">
                      <div className="space-y-1">
                        <Badge className={`px-3 py-1 font-bold text-xs border ${tierInfo.badge}`}>
                          {lvl.levelName || lvl.level_name || tierInfo.title}
                        </Badge>
                        <span className="text-xs font-mono text-gray-400 block pt-0.5">{lvl.levelCode || `LOC-${lvl.id}`}</span>
                      </div>
                    </TableCell>

                    <TableCell className="py-5">
                      <span className="text-sm font-semibold text-gray-700 bg-gray-100 px-2.5 py-1 rounded-md">
                        {tierInfo.range}
                      </span>
                    </TableCell>

                    <TableCell className="py-5">
                      <div className="flex items-center gap-1">
                        <DollarSign className="h-4 w-4 text-emerald-600" />
                        <span className="text-lg font-extrabold text-emerald-700">
                          {activeRate ? Number(activeRate.daily_rate).toFixed(2) : "185.00"}
                        </span>
                        <span className="text-xs text-gray-400 font-medium">/ day</span>
                      </div>
                    </TableCell>

                    <TableCell className="py-5 text-sm text-gray-600">
                      <div className="flex items-center gap-2">
                        <Calendar className="h-4 w-4 text-gray-400" />
                        <span>{activeRate ? activeRate.effective_from : "2026-01-01"}</span>
                      </div>
                    </TableCell>

                    <TableCell className="py-5">
                      <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
                        <CheckCircle2 className="h-3.5 w-3.5" /> Active Rate
                      </span>
                    </TableCell>

                    <TableCell className="py-5 text-right pr-6">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleEditClick(lvl)}
                        className="border-gray-200 hover:border-blue-300 hover:bg-blue-50 text-blue-700 font-semibold"
                      >
                        <Edit3 className="mr-1.5 h-3.5 w-3.5" /> Edit Rate
                      </Button>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Edit Rate Dialog */}
      <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-xl font-bold flex items-center gap-2">
              <DollarSign className="h-5 w-5 text-emerald-600" /> Edit LOC Rate
            </DialogTitle>
            <DialogDescription>
              Update daily rate and effective schedule for {editingRate?.careLevelName}.
            </DialogDescription>
          </DialogHeader>

          {editingRate && (
            <div className="space-y-4 py-3">
              <div className="space-y-2">
                <Label htmlFor="dailyRate" className="text-xs font-semibold text-gray-700">Daily Billing Rate ($ / Day)</Label>
                <div className="relative">
                  <DollarSign className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                  <Input
                    id="dailyRate"
                    type="number"
                    step="0.01"
                    className="pl-9 font-semibold text-lg"
                    value={editingRate.dailyRate}
                    onChange={(e) => setEditingRate({ ...editingRate, dailyRate: Number(e.target.value) })}
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="effectiveFrom" className="text-xs font-semibold text-gray-700">Effective Date</Label>
                <Input
                  id="effectiveFrom"
                  type="date"
                  value={editingRate.effectiveFrom}
                  onChange={(e) => setEditingRate({ ...editingRate, effectiveFrom: e.target.value })}
                />
              </div>
            </div>
          )}

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>Cancel</Button>
            <Button onClick={handleSaveModal} disabled={saveRateMutation.isPending} className="bg-blue-600 hover:bg-blue-700 font-semibold">
              {saveRateMutation.isPending ? "Saving..." : "Save Rate"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};
