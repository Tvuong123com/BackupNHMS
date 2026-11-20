import { Tabs, TabsContent, TabsList, TabsTrigger } from "../../ui/tabs";
import CarePlainDetailTabActivity from "../../pages/care-plan-detail/tab/care-plan-detail-tab-acitivity";
import CarePlanDetailTabOverview from "../../pages/care-plan-detail/tab/care-plan-detail-tab-overview";
import CarePlanDetailTabCost from "../../pages/care-plan-detail/tab/care-plan-detail-tab-cost";
import type { CarePlanGoal } from "@/services/care-plan/care-plan-types";

type CarePlanNavBarProps = {
  listCareGoals: CarePlanGoal[];
  lastReview: string;
  nextReview: string;
  cycle: number;
  locTier?: number;
  status?: string;
  createdAt?: string;
};
export default function CarePlanNavBar(props: CarePlanNavBarProps) {
  return (
    <div>
      <Tabs defaultValue="overview" className="w-full">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="activity">Activity</TabsTrigger>
          <TabsTrigger value="cost">Cost</TabsTrigger>
        </TabsList>

        <TabsContent value="overview">
          <CarePlanDetailTabOverview
            listCareGoals={props.listCareGoals}
            reviewCycle={props.cycle}
            lastReview={props.lastReview}
            nextReview={props.nextReview}
          ></CarePlanDetailTabOverview>
        </TabsContent>

        <TabsContent value="activity">
          <CarePlainDetailTabActivity
            listCareGoals={props.listCareGoals}
            reviewCycle={props.cycle}
            lastReview={props.lastReview}
            nextReview={props.nextReview}
            createdAt={props.createdAt}
            status={props.status}
          />
        </TabsContent>

        <TabsContent value="cost">
          <CarePlanDetailTabCost
            locTier={props.locTier}
            status={props.status}
            cycle={props.cycle}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}
