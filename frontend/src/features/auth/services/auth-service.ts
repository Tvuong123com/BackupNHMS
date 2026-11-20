import { axiosInstance as axios } from "@/lib/axios";
import type { PermissionCode } from "@/common/permissions";
import { permissionsForRole } from "../role-permissions";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  sessionId: string;
  role: string;
  name: string;
}

export interface ResetPasswordRequest {
  email: string;
}

export interface CurrentUser {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roleName: string;
  permissions: PermissionCode[];
}

export const fetchCurrentUser = async (): Promise<CurrentUser> => {
  const response = await axios.get<Omit<CurrentUser, "permissions">>("/auth/me");
  return { ...response.data, permissions: permissionsForRole(response.data.roleName) };
};

export const authService = {
  Login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axios.post("/auth/login", data);
    return response.data;
  },

  ResetPassword: async (data: ResetPasswordRequest): Promise<void> => {
    await axios.post("/auth/reset-password", data);
  },
};
