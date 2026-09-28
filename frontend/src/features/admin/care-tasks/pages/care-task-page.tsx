import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ByCnaTab } from "../tabs/by-cna-tab";
import { ByResidentTab } from "../tabs/by-resident-tab";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";
import { ChevronRight } from "lucide-react";

const tabs = [
  {
    value: "by-cna",
    label: "By CNA",
    permission: PERMISSIONS.CARE_TASK_VIEW,
    content: <ByCnaTab />,
  },
  {
    value: "by-resident",
    label: "By Resident",
    permission: PERMISSIONS.CARE_TASK_VIEW,
    content: <ByResidentTab />,
  },
];

const CareTaskPage = () => {
  const { can } = usePermissions();
  const visibleTabs = tabs.filter((t) => can(t.permission));

  return (
    <div className="space-y-6">
      {/* Breadcrumbs & Header */}
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-medium">
          <span>Care Planning</span>
          <ChevronRight className="size-3 text-slate-300 dark:text-slate-700" />
          <span className="text-slate-600 dark:text-slate-300">Care Tasks</span>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 dark:text-white tracking-tight">Care Tasks</h1>
        <p className="text-sm text-slate-400 dark:text-slate-500 font-medium">
          Manage daily assignments and task execution.
        </p>
      </div>

      {/* Tabs */}
      <Tabs defaultValue={visibleTabs[0]?.value} className="gap-0">
        <TabsList className="w-full">
          {visibleTabs.map((t) => (
            <TabsTrigger key={t.value} value={t.value} className="px-5">
              {t.label}
            </TabsTrigger>
          ))}
        </TabsList>
        {visibleTabs.map((t) => (
          <TabsContent key={t.value} value={t.value} className="mt-0">
            {t.content}
          </TabsContent>
        ))}
      </Tabs>
    </div>
  );
};

export default CareTaskPage;
