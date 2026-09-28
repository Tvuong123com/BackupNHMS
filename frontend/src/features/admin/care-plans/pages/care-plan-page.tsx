import CarePlanOptions from "../components/care-plan-options";
import CarePlanStatistical from "../components/care-plan-statistical";
import CarePlanTable from "../components/care-plan-table";
import {
  getCarePlanList,
  searchCarePlans,
} from "@/services/care-plan/care-plan-services";
import { useEffect, useState } from "react";
import type { GetCarePlanListResponse } from "@/services/care-plan/care-plan-types";
import { Loader2, AlertCircle, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";

const CarePlanPage = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [carePlanResponse, setCarePlanResponse] =
    useState<GetCarePlanListResponse | null>(null);

  useEffect(() => {
    loadCarePlans();
  }, []);

  const handleSearch = async (residentName: any, status: any) => {
    try {
      setLoading(true);
      setError(null);
      const response = await searchCarePlans(residentName, status);
      setCarePlanResponse(response);
    } catch (err: any) {
      console.error(err);
      setError("Failed to search care plans. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const loadCarePlans = async (residentName?: any, status?: any) => {
    try {
      setLoading(true);
      setError(null);
      const response = status
        ? await searchCarePlans(residentName, status)
        : await getCarePlanList();

      setCarePlanResponse(response);
    } catch (err: any) {
      console.error(err);
      setError("Unable to load care plans from server. Please verify backend service.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Breadcrumbs & Header */}
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-medium">
          <span>Care Planning</span>
          <ChevronRight className="size-3 text-slate-300 dark:text-slate-700" />
          <span className="text-slate-600 dark:text-slate-300">Care Plans</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 dark:text-white tracking-tight">Care Plans</h1>
        <p className="text-sm text-slate-400 dark:text-slate-500 font-medium">
          {carePlanResponse?.data?.list?.length || 0} care plans · sorted by Date Added (newest first)
        </p>
      </div>

      <CarePlanOptions onSearch={handleSearch} />

      {error && (
        <div className="flex items-center justify-between p-4 rounded-xl bg-red-50 border border-red-200 text-red-800 text-sm">
          <div className="flex items-center gap-2">
            <AlertCircle className="h-5 w-5 text-red-600 shrink-0" />
            <span>{error}</span>
          </div>
          <Button variant="outline" size="sm" onClick={() => loadCarePlans()} className="border-red-200 hover:bg-red-100 text-red-800">
            Retry
          </Button>
        </div>
      )}

      {loading && !carePlanResponse ? (
        <div className="flex flex-col items-center justify-center p-16 bg-white rounded-2xl border border-gray-200 shadow-sm space-y-3">
          <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
          <p className="text-sm font-medium text-gray-500">Loading Care Plans...</p>
        </div>
      ) : carePlanResponse ? (
        <>
          <CarePlanStatistical
            carePlans={carePlanResponse.data?.list || []}
            carePlanMetadata={carePlanResponse.metadata}
          />
          <CarePlanTable
            isLoading={loading}
            carePlans={carePlanResponse.data?.list || []}
          />
        </>
      ) : null}
    </div>
  );
};

export default CarePlanPage;
