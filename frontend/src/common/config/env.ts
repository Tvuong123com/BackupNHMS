const getApiUrl = () => {
  if (import.meta.env.VITE_API_URL !== undefined && import.meta.env.VITE_API_URL !== "") {
    return import.meta.env.VITE_API_URL;
  }
  if (import.meta.env.PROD) {
    return "";
  }
  return "http://localhost:8080";
};

export const ENV = {
  API_URL: getApiUrl(),
  MODE: import.meta.env.MODE,
};
