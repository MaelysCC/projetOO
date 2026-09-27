const API_URL = "http://localhost:8080";

export async function request(url, options = {}) {
  const response = await fetch(`${API_URL}${url}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers
    },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Erreur HTTP ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export const api = {
  // Works
  getWorks: () => request("/works"),

  addWork: (work) =>
    request("/works", {
      method: "POST",
      body: JSON.stringify(work)
    }),

  updateWork: (id, work) =>
    request(`/works/${id}`, {
      method: "PUT",
      body: JSON.stringify(work)
    }),

  deleteWork: (id) =>
    request(`/works/${id}`, {
      method: "DELETE"
    }),

  // Users
  getUsers: () => request("/users"),

  addUser: (user) =>
    request("/users", {
      method: "POST",
      body: JSON.stringify(user)
    }),

  // Entries
  getEntries: (userId) =>
    request(`/users/${userId}/entries`),

  addEntry: (userId, entry) =>
    request(`/users/${userId}/entries`, {
      method: "POST",
      body: JSON.stringify(entry)
    }),

  updateEntry: (entryId, entry) =>
    request(`/entries/${entryId}`, {
      method: "PUT",
      body: JSON.stringify(entry)
    }),

  deleteEntry: (entryId) =>
    request(`/entries/${entryId}`, {
      method: "DELETE"
    })
};
