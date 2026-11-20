import axios from "axios";

const getBaseUrl = () => {
  if (import.meta.env.VITE_API_URL !== undefined && import.meta.env.VITE_API_URL !== "") {
    return import.meta.env.VITE_API_URL;
  }
  if (import.meta.env.PROD) {
    return "";
  }
  return "http://localhost:8080";
};

export const axiosInstance = axios.create({
  baseURL: getBaseUrl(),
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token") || localStorage.getItem("eldcare_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
