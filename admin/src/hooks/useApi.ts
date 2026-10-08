import { useState, useEffect, useCallback } from "react";
import api from "../lib/api";

export function useApi<T>(url: string, params?: Record<string, unknown>, deps: unknown[] = []) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetch = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get(url, { params });
      setData(res.data.data);
    } catch (e: any) {
      setError(e.response?.data?.error?.message || e.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  }, [url, JSON.stringify(params), ...deps]);

  useEffect(() => { fetch(); }, [fetch]);

  return { data, loading, error, refetch: fetch };
}
