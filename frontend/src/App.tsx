import { useState } from "react";

type Role = "principal" | "teacher" | "parent";

const api = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export default function App() {
  const [role, setRole] = useState<Role>("principal");
  const [token, setToken] = useState("");
  const [log, setLog] = useState<string>("No actions yet");

  async function login() {
    const res = await fetch(`${api}/api/v1/auth/token`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: role }),
    });
    const data = await res.json();
    setToken(data.token);
    setLog(`Session active for role ${role}`);
  }

  async function createEvent() {
    const res = await fetch(`${api}/api/v1/events`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        "Idempotency-Key": crypto.randomUUID(),
      },
      body: JSON.stringify({
        type: "INCIDENT",
        incidentType: "FIGHT",
        severity: "HIGH",
        location: "Central courtyard",
      }),
    });
    const data = await res.json();
    setLog(`Event created: ${data.id ?? JSON.stringify(data)}`);
  }

  return (
    <main className="container">
      <h1>School Safety System with Events and Alerts</h1>
      <p>
        MVP demo: incidents, evacuation drills, parent communications, and postmortem.
      </p>
      <div className="card">
        <label>Role</label>
        <select value={role} onChange={(e) => setRole(e.target.value as Role)}>
          <option value="principal">principal</option>
          <option value="teacher">teacher</option>
          <option value="parent">parent</option>
        </select>
        <button onClick={login}>Generate token</button>
        <button onClick={createEvent} disabled={!token || role === "parent"}>
          Create test event
        </button>
      </div>
      <pre>{log}</pre>
    </main>
  );
}
