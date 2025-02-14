import { defineStore } from 'pinia';
import { useUserStore } from '@/stores/user';

// https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods
type HttpMethod = 'GET' | 'HEAD' | 'POST' | 'PUT' | 'DELETE' | 'CONNECT' | 'OPTIONS' | 'TRACE' | 'PATCH';
type RequestOptions = RequestInit & { timeout?: number }

const BASE_URL = 'http://localhost:8165/api/';
const DEFAULT_TIMEOUT = 20000; // 20 seconds

export const useRequestStore = defineStore('request', () => {
  const userStore = useUserStore();

  async function request(endpoint: string, options: RequestOptions = {}) {
    const { timeout = DEFAULT_TIMEOUT, ...fetchOptions } = options;
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeout);

    try {
      const response = await fetch(
        new URL(endpoint.replace(/^\/+|\/+$/g, ''), BASE_URL),
        { ...fetchOptions, signal: controller.signal }
      );

      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        const message = data.message || `${response.status} ${response.statusText}`;
        throw new Error(`Network request failed: ${message}`);
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return response.json();
      }

      return response.text();
    } catch (error) {
      if (error instanceof Error) {
        if (error.name !== 'AbortError') throw error;
        throw new Error(`Request timed out after ${timeout}ms`);
      }
      throw error;
    } finally {
      clearTimeout(timeoutId);
    }
  };

  function createOptions(
    method: HttpMethod,
    data?: object,
    options: RequestOptions = {}
  ): RequestOptions {
    const headers = new Headers(options.headers);

    if (data) headers.set('Content-Type', 'application/json');
    if (userStore.token) headers.set('Authorization', `Bearer ${userStore.token}`);

    return { ...options, method, headers, ...(data ? { body: JSON.stringify(data) } : {}) };
  }

  return {
    GET: (path: string, options?: RequestOptions) =>
      request(path, createOptions('GET', undefined, options)),

    POST: (path: string, data: object, options?: RequestOptions) =>
      request(path, createOptions('POST', data, options)),

    PUT: (path: string, data: object, options?: RequestOptions) =>
      request(path, createOptions('PUT', data, options)),

    PATCH: (path: string, data: object, options?: RequestOptions) =>
      request(path, createOptions('PATCH', data, options)),

    DELETE: (path: string, options?: RequestOptions) =>
      request(path, createOptions('DELETE', undefined, options))
  };
});
