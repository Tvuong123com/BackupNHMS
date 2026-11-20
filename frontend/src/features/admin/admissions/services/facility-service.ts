import { axiosInstance } from "@/lib/axios";
import type { ApiResponse } from "@/types/api";
import type { FacilitySelectResponse } from "../types/facility-type";

const BASE_URL = "/api/v1/admin/facility-settings";

export const getFacilityForSelect = async () => {
  const { data } = await axiosInstance.get<
    ApiResponse<FacilitySelectResponse[]>
  >(`${BASE_URL}/select`, { timeout: 15000 });
  return data;
};
