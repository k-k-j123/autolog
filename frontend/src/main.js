import "./styles.css";

const API_BASE = "/api";
const state = {
  token: localStorage.getItem("autolog.token") || "",
  userId: localStorage.getItem("autolog.userId") || "",
  user: null,
  vehicles: [],
  selectedVehicleId: localStorage.getItem("autolog.selectedVehicleId") || "",
  fuelLogs: [],
  loading: false
};

const app = document.querySelector("#app");

function saveSession(token, userId) {
  state.token = token;
  state.userId = String(userId);
  localStorage.setItem("autolog.token", token);
  localStorage.setItem("autolog.userId", String(userId));
}

function clearSession() {
  state.token = "";
  state.userId = "";
  state.user = null;
  state.vehicles = [];
  state.selectedVehicleId = "";
  state.fuelLogs = [];
  localStorage.removeItem("autolog.token");
  localStorage.removeItem("autolog.userId");
  localStorage.removeItem("autolog.selectedVehicleId");
}

async function request(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };

  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers
  });

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  const data = text ? parseResponse(text) : null;

  if (!response.ok) {
    const message = typeof data === "string" ? data : data?.message;
    throw new Error(message || `Request failed with ${response.status}`);
  }

  return data;
}

function parseResponse(text) {
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function formatDate(value) {
  if (!value) return "Not set";
  return new Intl.DateTimeFormat(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(value));
}

function formatDateTime(value) {
  if (!value) return "Not available";
  return new Intl.DateTimeFormat(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

function money(value) {
  return Number(value || 0).toLocaleString(undefined, {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
  });
}

function setNotice(message, type = "info") {
  const notice = document.querySelector("[data-notice]");
  if (!notice) return;
  notice.textContent = message;
  notice.className = `notice ${type}`;
}

async function withLoading(action, successMessage) {
  state.loading = true;
  render();
  try {
    await action();
    if (successMessage) setNotice(successMessage, "success");
  } catch (error) {
    setNotice(error.message || "Something went wrong", "error");
    if (error.message.includes("401") || error.message.toLowerCase().includes("unauthorized")) {
      clearSession();
    }
  } finally {
    state.loading = false;
    render();
  }
}

async function loadDashboard() {
  if (!state.token || !state.userId) return;

  const [user, vehicles] = await Promise.all([
    request(`/users/${state.userId}`),
    request(`/vehicles/user/${state.userId}`)
  ]);

  state.user = user;
  state.vehicles = vehicles || [];

  if (!state.vehicles.some((vehicle) => String(vehicle.id) === String(state.selectedVehicleId))) {
    state.selectedVehicleId = state.vehicles[0]?.id ? String(state.vehicles[0].id) : "";
  }

  if (state.selectedVehicleId) {
    localStorage.setItem("autolog.selectedVehicleId", state.selectedVehicleId);
    state.fuelLogs = await request(`/fuelLogs/vehicle/${state.selectedVehicleId}`);
  } else {
    state.fuelLogs = [];
    localStorage.removeItem("autolog.selectedVehicleId");
  }
}

function getSelectedVehicle() {
  return state.vehicles.find((vehicle) => String(vehicle.id) === String(state.selectedVehicleId));
}

function dashboardStats() {
  const totalFuel = state.fuelLogs.reduce((sum, log) => sum + Number(log.fuelAmount || 0), 0);
  const totalCost = state.fuelLogs.reduce((sum, log) => sum + Number(log.fuelCost || 0), 0);
  const avgMileage = state.fuelLogs.length
    ? state.fuelLogs.reduce((sum, log) => sum + Number(log.mileage || 0), 0) / state.fuelLogs.length
    : 0;

  return { totalFuel, totalCost, avgMileage };
}

function render() {
  app.innerHTML = state.token ? dashboardTemplate() : authTemplate();
  bindEvents();
}

function authTemplate() {
  return `
    <main class="auth-page">
      <section class="auth-panel">
        <div>
          <p class="eyebrow">Autolog</p>
          <h1>Track vehicles, fuel spend, and mileage.</h1>
          <p class="muted">Sign in or create an account to manage your garage and fuel history.</p>
        </div>

        <div class="auth-grid">
          <form data-form="login" class="panel">
            <h2>Login</h2>
            <label>Email
              <input name="email" type="email" autocomplete="email" required />
            </label>
            <label>Password
              <input name="password" type="password" autocomplete="current-password" required />
            </label>
            <button type="submit" ${state.loading ? "disabled" : ""}>Login</button>
          </form>

          <form data-form="signup" class="panel secondary-panel">
            <h2>Sign up</h2>
            <label>Email
              <input name="email" type="email" autocomplete="email" required />
            </label>
            <label>Password
              <input name="password" type="password" autocomplete="new-password" minlength="6" required />
            </label>
            <button type="submit" ${state.loading ? "disabled" : ""}>Create account</button>
          </form>
        </div>

        <p data-notice class="notice"></p>
      </section>
    </main>
  `;
}

function dashboardTemplate() {
  const selectedVehicle = getSelectedVehicle();
  const stats = dashboardStats();

  return `
    <main class="app-shell">
      <aside class="sidebar">
        <div>
          <p class="eyebrow">Autolog</p>
          <h1>Garage</h1>
          <p class="muted">${state.user?.email || "Loading account..."}</p>
        </div>
        <nav class="vehicle-list">
          ${state.vehicles.length ? state.vehicles.map(vehicleButtonTemplate).join("") : `<p class="empty">No vehicles yet.</p>`}
        </nav>
        <button data-action="logout" class="ghost">Logout</button>
      </aside>

      <section class="content">
        <header class="topbar">
          <div>
            <h2>${selectedVehicle?.name || "Add your first vehicle"}</h2>
            <p class="muted">${selectedVehicle ? `${selectedVehicle.number || "No number"} · Purchased ${formatDate(selectedVehicle.pdate)}` : "Vehicles and fuel logs appear here."}</p>
          </div>
          <p data-notice class="notice"></p>
        </header>

        <section class="stats-grid">
          <article class="stat">
            <span>Vehicles</span>
            <strong>${state.vehicles.length}</strong>
          </article>
          <article class="stat">
            <span>Fuel logged</span>
            <strong>${stats.totalFuel.toFixed(1)} L</strong>
          </article>
          <article class="stat">
            <span>Total cost</span>
            <strong>${money(stats.totalCost)}</strong>
          </article>
          <article class="stat">
            <span>Average mileage</span>
            <strong>${stats.avgMileage.toFixed(1)} km/L</strong>
          </article>
        </section>

        <section class="main-grid">
          <div class="stack">
            ${vehicleFormTemplate()}
            ${selectedVehicle ? fuelLogFormTemplate(selectedVehicle) : ""}
          </div>

          <div class="stack">
            ${profileTemplate()}
            ${selectedVehicle ? fuelLogsTemplate(selectedVehicle) : emptyStateTemplate()}
          </div>
        </section>
      </section>
    </main>
  `;
}

function vehicleButtonTemplate(vehicle) {
  const active = String(vehicle.id) === String(state.selectedVehicleId) ? "active" : "";
  return `
    <button data-action="select-vehicle" data-id="${vehicle.id}" class="vehicle-pill ${active}">
      <span>${vehicle.name || "Unnamed vehicle"}</span>
      <small>${vehicle.number || "No number"}</small>
    </button>
  `;
}

function vehicleFormTemplate() {
  return `
    <article class="panel">
      <div class="panel-header">
        <div>
          <h3>Add vehicle</h3>
          <p class="muted">Registers a vehicle under your user id.</p>
        </div>
      </div>
      <form data-form="vehicle" class="form-grid">
        <label>Name
          <input name="name" placeholder="Honda City" required />
        </label>
        <label>Number
          <input name="number" placeholder="KL 01 AB 1234" required />
        </label>
        <label>Purchase date
          <input name="pdate" type="date" required />
        </label>
        <button type="submit" ${state.loading ? "disabled" : ""}>Add vehicle</button>
      </form>
      ${state.selectedVehicleId ? `<button data-action="delete-vehicle" class="danger">Delete selected vehicle</button>` : ""}
    </article>
  `;
}

function fuelLogFormTemplate(vehicle) {
  return `
    <article class="panel">
      <div class="panel-header">
        <div>
          <h3>Add fuel log</h3>
          <p class="muted">${vehicle.name} receives the new entry.</p>
        </div>
      </div>
      <form data-form="fuel-log" class="form-grid">
        <label>Odometer
          <input name="odometerReading" type="number" min="0" step="1" required />
        </label>
        <label>Fuel amount
          <input name="fuelAmount" type="number" min="0" step="0.01" required />
        </label>
        <label>Fuel cost
          <input name="fuelCost" type="number" min="0" step="0.01" required />
        </label>
        <label>Mileage
          <input name="mileage" type="number" min="0" step="0.01" required />
        </label>
        <button type="submit" ${state.loading ? "disabled" : ""}>Add fuel log</button>
      </form>
    </article>
  `;
}

function profileTemplate() {
  return `
    <article class="panel">
      <div class="panel-header">
        <div>
          <h3>Account</h3>
          <p class="muted">User id ${state.userId}</p>
        </div>
      </div>
      <form data-form="profile" class="form-grid">
        <label>Email
          <input name="email" type="email" value="${state.user?.email || ""}" required />
        </label>
        <label>New password
          <input name="password" type="password" placeholder="Leave blank to keep current password" />
        </label>
        <button type="submit" ${state.loading ? "disabled" : ""}>Update profile</button>
      </form>
      <button data-action="delete-user" class="danger">Delete account</button>
    </article>
  `;
}

function fuelLogsTemplate(vehicle) {
  const logs = [...state.fuelLogs].sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));

  return `
    <article class="panel">
      <div class="panel-header">
        <div>
          <h3>Fuel logs</h3>
          <p class="muted">${vehicle.name} · ${logs.length} entries</p>
        </div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Odometer</th>
              <th>Fuel</th>
              <th>Cost</th>
              <th>Mileage</th>
              <th>Created</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            ${logs.length ? logs.map(logRowTemplate).join("") : `<tr><td colspan="6" class="empty">No fuel logs for this vehicle.</td></tr>`}
          </tbody>
        </table>
      </div>
    </article>
  `;
}

function logRowTemplate(log) {
  return `
    <tr>
      <td>${Number(log.odometerReading || 0).toLocaleString()} km</td>
      <td>${Number(log.fuelAmount || 0).toFixed(2)} L</td>
      <td>${money(log.fuelCost)}</td>
      <td>${Number(log.mileage || 0).toFixed(2)} km/L</td>
      <td>${formatDateTime(log.createdAt)}</td>
      <td><button data-action="delete-fuel" data-id="${log.id}" class="icon-danger">Delete</button></td>
    </tr>
  `;
}

function emptyStateTemplate() {
  return `
    <article class="panel empty-panel">
      <h3>No vehicle selected</h3>
      <p class="muted">Create a vehicle to start logging fuel entries.</p>
    </article>
  `;
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function bindEvents() {
  document.querySelector('[data-form="login"]')?.addEventListener("submit", (event) => {
    event.preventDefault();
    const payload = formData(event.currentTarget);
    withLoading(async () => {
      const response = await request("/auth/login", {
        method: "POST",
        body: JSON.stringify(payload)
      });
      saveSession(response.token, response.userId);
      await loadDashboard();
    }, "Logged in.");
  });

  document.querySelector('[data-form="signup"]')?.addEventListener("submit", (event) => {
    event.preventDefault();
    const payload = formData(event.currentTarget);
    withLoading(async () => {
      await request("/auth/signup", {
        method: "POST",
        body: JSON.stringify(payload)
      });
      const response = await request("/auth/login", {
        method: "POST",
        body: JSON.stringify(payload)
      });
      saveSession(response.token, response.userId);
      await loadDashboard();
    }, "Account created.");
  });

  document.querySelector('[data-form="vehicle"]')?.addEventListener("submit", (event) => {
    event.preventDefault();
    const payload = formData(event.currentTarget);
    withLoading(async () => {
      const vehicle = await request(`/vehicles/user/${state.userId}`, {
        method: "POST",
        body: JSON.stringify(payload)
      });
      state.selectedVehicleId = String(vehicle.id);
      await loadDashboard();
    }, "Vehicle added.");
  });

  document.querySelector('[data-form="fuel-log"]')?.addEventListener("submit", (event) => {
    event.preventDefault();
    const data = formData(event.currentTarget);
    const payload = {
      odometerReading: Number(data.odometerReading),
      fuelAmount: Number(data.fuelAmount),
      fuelCost: Number(data.fuelCost),
      mileage: Number(data.mileage)
    };
    withLoading(async () => {
      await request(`/fuelLogs/vehicle/${state.selectedVehicleId}`, {
        method: "POST",
        body: JSON.stringify(payload)
      });
      await loadDashboard();
    }, "Fuel log added.");
  });

  document.querySelector('[data-form="profile"]')?.addEventListener("submit", (event) => {
    event.preventDefault();
    const data = formData(event.currentTarget);
    const payload = {
      ...state.user,
      email: data.email
    };
    if (data.password) payload.password = data.password;

    withLoading(async () => {
      state.user = await request(`/users/${state.userId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
      });
    }, "Profile updated.");
  });

  document.querySelectorAll("[data-action='select-vehicle']").forEach((button) => {
    button.addEventListener("click", () => {
      state.selectedVehicleId = button.dataset.id;
      localStorage.setItem("autolog.selectedVehicleId", state.selectedVehicleId);
      withLoading(loadDashboard);
    });
  });

  document.querySelector("[data-action='delete-vehicle']")?.addEventListener("click", () => {
    if (!confirm("Delete this vehicle and its fuel logs?")) return;
    withLoading(async () => {
      await request(`/vehicles/user/${state.userId}/vehicle/${state.selectedVehicleId}`, {
        method: "DELETE"
      });
      state.selectedVehicleId = "";
      await loadDashboard();
    }, "Vehicle deleted.");
  });

  document.querySelectorAll("[data-action='delete-fuel']").forEach((button) => {
    button.addEventListener("click", () => {
      withLoading(async () => {
        await request(`/fuelLogs/vehicle/${state.selectedVehicleId}/fuel/${button.dataset.id}`, {
          method: "DELETE"
        });
        await loadDashboard();
      }, "Fuel log deleted.");
    });
  });

  document.querySelector("[data-action='delete-user']")?.addEventListener("click", () => {
    if (!confirm("Delete your account and all related data?")) return;
    withLoading(async () => {
      await request(`/users/${state.userId}`, {
        method: "DELETE"
      });
      clearSession();
    }, "Account deleted.");
  });

  document.querySelector("[data-action='logout']")?.addEventListener("click", () => {
    clearSession();
    render();
  });
}

if (state.token && state.userId) {
  withLoading(loadDashboard);
} else {
  render();
}
