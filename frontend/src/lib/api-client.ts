import axios from "axios";

const getApiBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_URL;
  if (envUrl !== undefined && envUrl !== "") {
    const trimmed = envUrl.replace(/\/+$/, "");
    return trimmed.endsWith("/api/v1") ? trimmed : `${trimmed}/api/v1`;
  }
  return "/api/v1";
};

export const apiClient = axios.create({
  baseURL: getApiBaseUrl(),
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token") || localStorage.getItem("eldcare_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Optionally, you can add a response interceptor here later 
// to globally handle 401s or other standard API errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Extract the backend's error message so it's easier to catch
    if (error.response && error.response.data && error.response.data.message) {
      error.message = error.response.data.message;
    }
    return Promise.reject(error);
  }
);
