import { defineStore } from 'pinia';
import { mergeDeepRight } from 'ramda';
import type { UserTable, ChatTable, ChannelTable } from '@/types.d';

export const STORES = {
  USER: 'user',
  CHATS: 'chats',
  CHANNELS: 'channels'
} as const;

interface StoreDataTypes {
  [STORES.USER]: UserTable;
  [STORES.CHATS]: ChatTable;
  [STORES.CHANNELS]: ChannelTable;
}

type StoreNames = typeof STORES[keyof typeof STORES];

export const useIndexedDBStore = defineStore('indexedDB', () => {
  const DB_NAME = 'blahblah_db';
  const DB_VERSION = 1;

  /**
   * Initializes the IndexedDB database with the specified stores.
   * @returns A promise that resolves to the initialized database instance.
   */
  async function initDB(): Promise<IDBDatabase> {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(DB_NAME, DB_VERSION);

      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result);

      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result;

        Object.values(STORES).forEach((storeName) => {
          if (!db.objectStoreNames.contains(storeName)) {
            db.createObjectStore(storeName, { keyPath: 'id' });
          }
        });
      };
    });
  }

  /**
   * Completely removes the entire database including all stores and their data.
   * @returns A promise that resolves when the database is successfully deleted.
   */
  async function deleteDB(): Promise<void> {
    return new Promise((resolve, reject) => {
      const request = indexedDB.deleteDatabase(DB_NAME);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  /**
   * Gets an object store instance for the specified store name.
   * @param storeName - The name of the store to access.
   * @param mode - Access mode for the store, defaults to `readonly`.
   * @returns A promise that resolves to the requested object store.
   */
  async function getStore(
    storeName: StoreNames,
    mode: IDBTransactionMode = 'readonly'
  ): Promise<IDBObjectStore> {
    const db = await initDB();
    const transaction = db.transaction(storeName, mode);
    return transaction.objectStore(storeName);
  }

  /**
   * Retrieves a single row from the specified store by its ID.
   * @param storeName - The name of the store to query.
   * @param id - The ID of the row to retrieve.
   * @returns A promise that resolves to the retrieved row or null if not found.
   */
  async function getTableRow<T extends StoreNames>(
    storeName: T,
    id: number
  ): Promise<StoreDataTypes[T] | null> {
    const store = await getStore(storeName);

    return new Promise((resolve, reject) => {
      const request = store.get(id);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result);
    });
  }

  /**
   * Retrieves all rows from the specified store.
   * @param storeName - The name of the store to query.
   * @returns A promise that resolves to an array of all rows in the store.
   */
  async function getAllTableRows<T extends StoreNames>(
    storeName: T
  ): Promise<StoreDataTypes[T][]> {
    const store = await getStore(storeName);

    return new Promise((resolve, reject) => {
      const request = store.getAll();
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result);
    });
  }

  /**
   * Creates one or more new rows in the specified store.
   * @param storeName - The name of the store to add to.
   * @param items - The items to add to the store.
   * @returns A promise that resolves to the array of created items.
   */
  async function createTableRow<T extends StoreNames>(
    storeName: T,
    ...items: StoreDataTypes[T][]
  ): Promise<StoreDataTypes[T][]> {
    const store = await getStore(storeName, 'readwrite');

    return new Promise((resolve, reject) => {
      const transaction = store.transaction;
      transaction.oncomplete = () => resolve(items);
      transaction.onerror = () => reject(transaction.error);
      items.forEach(item => store.add(item));
    });
  }

  /**
   * Updates an existing row in the specified store.
   * @param storeName - The name of the store to update.
   * @param id - The ID of the row to update.
   * @param data - The partial data to merge with the existing row.
   * @returns A promise that resolves to the updated row.
   */
  async function updateTableRow<T extends StoreNames>(
    storeName: T,
    id: number,
    data: Partial<StoreDataTypes[T]>
  ): Promise<StoreDataTypes[T]> {
    const store = await getStore(storeName, 'readwrite');

    return new Promise((resolve, reject) => {
      const getRequest: IDBRequest<StoreDataTypes[T]> = store.get(id);

      getRequest.onerror = () => reject(getRequest.error);
      getRequest.onsuccess = () => {
        const updatedData = mergeDeepRight(
          getRequest.result, { ...data, id }
        ) as StoreDataTypes[T];

        const putRequest = store.put(updatedData);
        putRequest.onerror = () => reject(putRequest.error);
        putRequest.onsuccess = () => resolve(updatedData);
      };
    });
  }

  /**
   * Updates a specific field in a table row, using either a direct value or an updater function.
   * @param storeName - The name of the store to update.
   * @param id - The ID of the row to update.
   * @param field - The field name to update.
   * @param updater - Either a new value or a function that receives the current value and returns the new value.
   * @returns A promise that resolves to the updated row.
   * @example
   * // Update with direct value
   * await updateTableField(STORES.CHANNELS, 1, 'name', 'New Name')
   * // Update with function
   * await updateTableField(STORES.CHANNELS, 1, 'messages', (allMessages) => [...allMessages, newMessage])
   */
  async function updateTableField<T extends StoreNames, K extends keyof StoreDataTypes[T]>(
    storeName: T,
    id: number,
    field: K,
    updater: ((currentValue: StoreDataTypes[T][K]) => StoreDataTypes[T][K]) | StoreDataTypes[T][K]
  ): Promise<StoreDataTypes[T]> {
    const store = await getStore(storeName, 'readwrite');

    return new Promise((resolve, reject) => {
      const getRequest: IDBRequest<StoreDataTypes[T]> = store.get(id);

      getRequest.onerror = () => reject(getRequest.error);
      getRequest.onsuccess = () => {
        const currentData = getRequest.result;
        const newValue = (updater instanceof Function)
          ? updater(currentData[field]) : updater;

        const updatedData = mergeDeepRight(
          currentData, { [field]: newValue }
        ) as StoreDataTypes[T];

        const putRequest = store.put(updatedData);
        putRequest.onerror = () => reject(putRequest.error);
        putRequest.onsuccess = () => resolve(updatedData);
      };
    });
  }

  /**
   * Deletes a single row from the specified store.
   * @param storeName - The name of the store to delete from.
   * @param id - The ID of the row to delete.
   * @returns A promise that resolves when the row is deleted.
   */
  async function deleteTableRow(
    storeName: StoreNames,
    id: number
  ): Promise<void> {
    const store = await getStore(storeName, 'readwrite');

    return new Promise((resolve, reject) => {
      const request = store.delete(id);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  /**
   * Deletes all rows from the specified store.
   * @param storeName - The name of the store to clear.
   * @returns A promise that resolves when all rows are deleted.
   */
  async function deleteAllTableRows(
    storeName: StoreNames
  ): Promise<void> {
    const store = await getStore(storeName, 'readwrite');
    return new Promise((resolve, reject) => {
      const request = store.clear();
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  return {
    initDB,
    deleteDB,
    getTableRow,
    getAllTableRows,
    createTableRow,
    updateTableRow,
    updateTableField,
    deleteTableRow,
    deleteAllTableRows,
  };
});
