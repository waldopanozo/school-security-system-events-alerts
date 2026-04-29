import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from "react";
const api = import.meta.env.VITE_API_URL ?? "http://localhost:8080";
export default function App() {
    const [role, setRole] = useState("principal");
    const [token, setToken] = useState("");
    const [log, setLog] = useState("No actions yet");
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
    return (_jsxs("main", { className: "container", children: [_jsx("h1", { children: "School Safety System with Events and Alerts" }), _jsx("p", { children: "MVP demo: incidents, evacuation drills, parent communications, and postmortem." }), _jsxs("div", { className: "card", children: [_jsx("label", { children: "Role" }), _jsxs("select", { value: role, onChange: (e) => setRole(e.target.value), children: [_jsx("option", { value: "principal", children: "principal" }), _jsx("option", { value: "teacher", children: "teacher" }), _jsx("option", { value: "parent", children: "parent" })] }), _jsx("button", { onClick: login, children: "Generate token" }), _jsx("button", { onClick: createEvent, disabled: !token || role === "parent", children: "Create test event" })] }), _jsx("pre", { children: log })] }));
}
